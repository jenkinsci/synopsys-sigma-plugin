package com.synopsys.integration.jenkins.sigma.workflow;

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

import com.synopsys.integration.jenkins.sigma.tool.SigmaToolInstallation;

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
    private boolean ignorePolicies;
    private List<AnalyzeArgumentEntry> additionalAnalyzeArguments;
    private String workingDirectory;
    private List<ConfigFileEntry> configFileEntries;
    private List<PolicyFileEntry> policyFileEntries;
    private List<AnalyzeDirectoryEntry> analyzeDirectories;

    @DataBoundConstructor
    public SigmaBinaryStep(final String sigmaToolName) {
        this.sigmaToolName = sigmaToolName;
    }

    @SuppressWarnings("unused")
    public String getSigmaToolName() {
        return sigmaToolName;
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
    public List<PolicyFileEntry> getPolicyFileEntries() {
        return policyFileEntries;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setPolicyFileEntries(final List<PolicyFileEntry> policyFileEntries) {
        this.policyFileEntries = initEntryList(policyFileEntries);
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

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        logger.info("Executing Sigma binary Build Step.");
        try {
            if (Result.ABORTED.equals(build.getResult())) {
                throw new AbortException(FAILURE_MESSAGE + "The build was aborted.");
            }

            SigmaBuildContext sigmaBuildContext = createBuildContext(build, launcher, listener);

            if (!sigmaBuildContext.getNode().isPresent()) {
                throw new AbortException(FAILURE_MESSAGE + "Could not access node.");
            }

            if (!sigmaBuildContext.getVirtualChannel().isPresent()) {
                String nodeName = sigmaBuildContext.getNode()
                    .map(Node::getDisplayName)
                    .orElse("Unknown node");
                throw new AbortException(FAILURE_MESSAGE + "Configured node \"" + nodeName + "\" is either not connected or offline.");
            }

            ArgumentListBuilder commandLineBuilder = new ArgumentListBuilder();
            // order of the arguments matters:
            // 1. executable
            // 2. config files
            // 3. policy files
            // 4. working directory
            // 5. analyze sub-command
            // 6. format jenkins
            // 7. additional arguments
            // 8. directories to analyze (always the last argument)
            commandLineBuilder = addSigmaExecutableToCommand(sigmaBuildContext, commandLineBuilder);
            commandLineBuilder = addCommandLineArguments(commandLineBuilder, configFileEntries);
            commandLineBuilder = addCommandLineArguments(commandLineBuilder, policyFileEntries);
            commandLineBuilder = addCommandLineArguments(commandLineBuilder, CommonCommandLineEntry.toAppendableList());
            commandLineBuilder = addCommandLineArguments(commandLineBuilder, additionalAnalyzeArguments);
            commandLineBuilder = addCommandLineArguments(commandLineBuilder, analyzeDirectories);
            FilePath workingDirectory = getWorkingDirectory(build, sigmaBuildContext);
            Result result = executeSigma(sigmaBuildContext, commandLineBuilder, workingDirectory);
            if (result == Result.SUCCESS) {
                return true;
            }
        } catch (final InterruptedException e) {
            logger.error("[ERROR] Synopsys Sigma thread was interrupted.", e);
            build.setResult(Result.ABORTED);
            Thread.currentThread().interrupt();
        } catch (final Exception ex) {
            logger.error("[ERROR] " + ex.getMessage());
            logger.debug(ex.getMessage(), ex);
            ex.printStackTrace(listener.fatalError(FAILURE_MESSAGE + "sigma command execution failed"));
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

    private ArgumentListBuilder addSigmaExecutableToCommand(SigmaBuildContext sigmaBuildContext, ArgumentListBuilder commandLineBuilder) throws IOException, InterruptedException {
        Optional<SigmaToolInstallation> sigmaTool = getSigma();
        Launcher launcher = sigmaBuildContext.getLauncher();
        if (sigmaTool.isPresent()) {
            Optional<Node> nodeOptional = sigmaBuildContext.getNode();
            if (nodeOptional.isPresent()) {
                Node node = nodeOptional.get();
                BuildListener listener = sigmaBuildContext.getListener();
                EnvVars environment = sigmaBuildContext.getEnvironment();
                SigmaToolInstallation installation = sigmaTool.get().forNode(node, listener);
                installation = installation.forEnvironment(environment);
                logger.info("Sigma tool installation found. {}", installation.getHome());
                commandLineBuilder.add(installation.getExecutablePath(launcher));
            }
        } else {
            // sigma tool installation not defined try to run sigma with a simple command.  It will work if sigma is on executable on the path
            if (sigmaBuildContext.getLauncher().isUnix()) {
                commandLineBuilder.add(SigmaToolInstallation.UNIX_SIGMA_COMMAND);
            } else {
                commandLineBuilder.add(SigmaToolInstallation.WINDOWS_SIGMA_COMMAND);
            }
        }
        return commandLineBuilder;
    }

    private <T extends AppendableArgument> ArgumentListBuilder addCommandLineArguments(ArgumentListBuilder argumentListBuilder, List<T> argumentListItems) {
        if (argumentListItems != null) {
            for (AppendableArgument appendableArgument : argumentListItems) {
                appendableArgument.appendToArgumentList(argumentListBuilder);
            }
        }
        return argumentListBuilder;
    }

    private Result executeSigma(SigmaBuildContext sigmaBuildContext, ArgumentListBuilder commandLineBuilder, FilePath workingDirectory) throws IOException, InterruptedException {
        int returnCode = sigmaBuildContext.getLauncher()
            .launch()
            .cmds(commandLineBuilder)
            .envs(sigmaBuildContext.getEnvironment())
            .pwd(workingDirectory)
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
            return "Execute Synopsys Sigma static analysis with Binary";
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
