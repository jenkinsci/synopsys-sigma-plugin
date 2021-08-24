package com.synopsys.integration.jenkins.sigma.extension.workflow;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

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
import jenkins.tasks.SimpleBuildStep;

public class SigmaBinaryStep extends Builder implements SimpleBuildStep {
    public static final String FAILURE_MESSAGE = "Unable to perform Synopsys Sigma static analysis: ";

    private String sigmaToolName;
    private String commandLine;
    private boolean ignorePolicies;

    @DataBoundConstructor
    public SigmaBinaryStep() {}

    public String getSigmaToolName() {
        return sigmaToolName;
    }

    @DataBoundSetter
    public void setSigmaToolName(final String sigmaToolName) {
        this.sigmaToolName = sigmaToolName;
    }

    public String getCommandLine() {
        return commandLine;
    }

    @DataBoundSetter
    public void setCommandLine(final String commandLine) {
        this.commandLine = commandLine;
    }

    public boolean isIgnorePolicies() {
        return ignorePolicies;
    }

    @DataBoundSetter
    public void setIgnorePolicies(final boolean ignorePolicies) {
        this.ignorePolicies = ignorePolicies;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Executing Sigma binary Build Step.");
        try {
            if (Result.ABORTED.equals(build.getResult())) {
                throw new AbortException(FAILURE_MESSAGE + "The build was aborted.");
            }

            SigmaBuildContext sigmaBuildContext = createBuildContext(build, launcher, listener);
            Node node = sigmaBuildContext.getNode().orElseThrow(() -> new AbortException(FAILURE_MESSAGE + "Could not access node."));
            sigmaBuildContext.getVirtualChannel().orElseThrow(() -> new AbortException(FAILURE_MESSAGE + "Configured node \"" + node.getDisplayName() + "\" is either not connected or offline."));

            FilePath workingDirectory = getWorkingDirectory(build, sigmaBuildContext);
            CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, getSigma().orElse(null), ignorePolicies, commandLine);
            ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();

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
        Node node = build.getBuiltOn();
        VirtualChannel virtualChannel = null;
        if (node != null) {
            virtualChannel = node.getChannel();
        }
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
