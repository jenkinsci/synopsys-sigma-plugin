package com.synopsys.integration.jenkins.sigma.extension.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.extension.CommandLineArgumentBuilder;
import com.synopsys.integration.jenkins.sigma.extension.JenkinsDataConverter;
import com.synopsys.integration.jenkins.sigma.extension.UserInputValidator;
import com.synopsys.integration.jenkins.sigma.extension.UserProvidedArgumentData;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeArgumentEntry;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeDirectoryEntry;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.Result;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;

public class SigmaBinaryStep extends Builder {
    public static final String FAILURE_MESSAGE = "Unable to perform Synopsys Sigma static analysis: ";
    private static final Logger logger = LoggerFactory.getLogger(SigmaBinaryStep.class);

    private final String sigmaToolName;
    private List<AnalyzeArgumentEntry> additionalAnalyzeArguments;
    private List<ConfigFileEntry> configFileEntries;
    private String policyFilePath;
    private List<AnalyzeDirectoryEntry> analyzeDirectories;

    @DataBoundConstructor
    public SigmaBinaryStep(final String sigmaToolName) {
        this.sigmaToolName = sigmaToolName;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        logger.info("Executing Sigma binary Build Step.");
        try {
            if (Result.ABORTED.equals(build.getResult())) {
                throw new AbortException(FAILURE_MESSAGE + "The build was aborted.");
            }

            SigmaBuildContext sigmaBuildContext = createBuildContext(build, launcher, listener);
            Node node = sigmaBuildContext.getNode().orElseThrow(() -> new AbortException(FAILURE_MESSAGE + "Could not access node."));
            sigmaBuildContext.getVirtualChannel().orElseThrow(() -> new AbortException(FAILURE_MESSAGE + "Configured node \"" + node.getDisplayName() + "\" is either not connected or offline."));

            FilePath workingDirectory = getWorkingDirectory(build, sigmaBuildContext);
            JenkinsDataConverter jenkinsDataConverter = new JenkinsDataConverter(getSigma().orElse(null), policyFilePath, configFileEntries, additionalAnalyzeArguments, analyzeDirectories);
            UserProvidedArgumentData userProvidedArgumentData = jenkinsDataConverter.convertData();
            UserInputValidator userInputValidator = new UserInputValidator(sigmaBuildContext, workingDirectory, userProvidedArgumentData);

            List<ValidationResult> validationResults = userInputValidator.validate();
            boolean hasValidationErrors = validationResults.stream()
                .anyMatch(ValidationResult::isError);
            if (hasValidationErrors) {
                logValidationErrors(sigmaBuildContext, validationResults);
                build.setResult(Result.ABORTED);
                return false;
            }

            CommandLineArgumentBuilder commandLineArgumentBuilder = new CommandLineArgumentBuilder(sigmaBuildContext, userProvidedArgumentData);
            ArgumentListBuilder argumentListBuilder = commandLineArgumentBuilder.buildArgumentList();

            Result result = executeSigma(sigmaBuildContext, argumentListBuilder, workingDirectory);
            if (result == Result.SUCCESS) {
                return true;
            }
        } catch (final InterruptedException e) {
            listener.error("[ERROR] Synopsys Sigma thread was interrupted.", e);
            build.setResult(Result.ABORTED);
            Thread.currentThread().interrupt();
        } catch (final Exception ex) {
            listener.error("[ERROR] " + ex.getMessage());
            ex.printStackTrace(listener.fatalError(FAILURE_MESSAGE + "sigma command execution failed."));
            build.setResult(Result.UNSTABLE);
        }
        return false;
    }

    private SigmaBuildContext createBuildContext(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        final Node node = build.getBuiltOn();
        final VirtualChannel virtualChannel = node.getChannel();
        EnvVars environment = build.getEnvironment(listener);
        return new SigmaBuildContext(launcher, listener, node, virtualChannel, environment);
    }

    private FilePath getWorkingDirectory(final AbstractBuild<?, ?> build, SigmaBuildContext sigmaBuildContext) throws AbortException {
        FilePath workingDirectory;
        if (build.getWorkspace() == null) {
            Optional<VirtualChannel> virtualChannel = sigmaBuildContext.getVirtualChannel();
            if (virtualChannel.isPresent()) {
                workingDirectory = new FilePath(virtualChannel.get(), build.getProject().getCustomWorkspace());
            } else {
                throw new AbortException(FAILURE_MESSAGE + "Could not determine working directory");
            }
        } else {
            workingDirectory = build.getWorkspace();
        }
        return workingDirectory;
    }

    private void logValidationErrors(SigmaBuildContext sigmaBuildContext, List<ValidationResult> validationResults) {
        sigmaBuildContext.getListener().error("[ERROR] The configuration of Sigma command line arguments is invalid. See the details below: ");
        for (ValidationResult result : validationResults) {
            if (result.isError()) {
                if (result.hasValue()) {
                    sigmaBuildContext.getListener().error("[ERROR] %s: %s cause: %s", result.getName(), result.getValue(), result.getErrorMessage());
                } else {
                    sigmaBuildContext.getListener().error("[ERROR] %s: cause: %s", result.getName(), result.getErrorMessage());
                }
            }
        }
    }

    private Result executeSigma(SigmaBuildContext sigmaBuildContext, ArgumentListBuilder commandLineBuilder, FilePath workingDirectory) throws IOException, InterruptedException {
        ArgumentListBuilder commands = commandLineBuilder;
        if (!sigmaBuildContext.getLauncher().isUnix()) {
            // convert to a windows command line
            commands = commandLineBuilder.toWindowsCommand();
        }

        int returnCode = sigmaBuildContext.getLauncher()
            .launch()
            .cmds(commands)
            .envs(sigmaBuildContext.getEnvironment())
            .pwd(workingDirectory)
            .stdout(sigmaBuildContext.getListener())
            .join();

        if (returnCode != 0) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }

    private Optional<SigmaToolInstallation> getSigma() {
        Predicate<SigmaToolInstallation> sigmaToolFilter = (installation) -> sigmaToolName != null && sigmaToolName.equals(installation.getName());
        return Arrays.stream(getDescriptor().getInstallations()).filter(sigmaToolFilter).findFirst();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @SuppressWarnings("unused")
    public String getSigmaToolName() {
        return sigmaToolName;
    }

    @SuppressWarnings("unused")
    public String getPolicyFilePath() {
        return policyFilePath;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPolicyFilePath(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    @SuppressWarnings("unused")
    public List<AnalyzeArgumentEntry> getAdditionalAnalyzeArguments() {
        return additionalAnalyzeArguments;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAdditionalAnalyzeArguments(final List<AnalyzeArgumentEntry> additionalAnalyzeArguments) {
        this.additionalAnalyzeArguments = initEntryList(additionalAnalyzeArguments);
    }

    @SuppressWarnings("unused")
    public List<ConfigFileEntry> getConfigFileEntries() {
        return configFileEntries;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setConfigFileEntries(final List<ConfigFileEntry> configFileEntries) {
        this.configFileEntries = initEntryList(configFileEntries);
    }

    @SuppressWarnings("unused")
    public List<AnalyzeDirectoryEntry> getAnalyzeDirectories() {
        return analyzeDirectories;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAnalyzeDirectories(final List<AnalyzeDirectoryEntry> analyzeDirectories) {
        this.analyzeDirectories = initEntryList(analyzeDirectories);
    }

    private <T> List<T> initEntryList(List<T> entries) {
        return entries == null ? Collections.emptyList() : new ArrayList<>(entries);
    }

    @Extension
    @Symbol("sigma")
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @CopyOnWrite
        private volatile SigmaToolInstallation[] installations = new SigmaToolInstallation[0];

        public DescriptorImpl() {
            load();
        }

        protected DescriptorImpl(Class<? extends SigmaBinaryStep> clazz) {
            super(clazz);
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return Messages.workflow_step_displayName();
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }

        public SigmaToolInstallation.DescriptorImpl getToolDescriptor() {
            return ToolInstallation.all().get(SigmaToolInstallation.DescriptorImpl.class);
        }

        public SigmaToolInstallation[] getInstallations() {
            return Arrays.copyOf(installations, installations.length);
        }

        public void setInstallations(SigmaToolInstallation... installations) {
            this.installations = installations;
            save();
        }

        @SuppressWarnings("unused")
        public boolean hasToolsConfigured() {
            return installations.length > 0;
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillSigmaToolNameItems() {
            ListBoxModel items = new ListBoxModel();
            for (SigmaToolInstallation installation : installations) {
                items.add(installation.getName());
            }

            return items;
        }
    }
}
