package com.synopsys.integration.jenkins.sigma.workflow;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.jenkins.sigma.tool.SigmaToolInstallation;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
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
import hudson.util.ListBoxModel;

public class SigmaBinaryStep extends Builder {
    public static final String FAILURE_MESSAGE = "Unable to perform Synopsys Sigma static analysis: ";
    private static final Logger logger = LoggerFactory.getLogger(SigmaBinaryStep.class);

    private final String sigmaToolName;

    @DataBoundConstructor
    public SigmaBinaryStep(final String sigmaToolName) {
        this.sigmaToolName = sigmaToolName;
    }

    @SuppressWarnings("unused")
    public String getSigmaToolName() {
        return sigmaToolName;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        logger.info("Executing Sigma binary Build Step.");
        try {
            if (Result.ABORTED.equals(build.getResult())) {
                throw new AbortException(FAILURE_MESSAGE + "The build was aborted.");
            }

            final Node node = build.getBuiltOn();
            if (node == null) {
                throw new AbortException(FAILURE_MESSAGE + "Could not access node.");
            }

            final VirtualChannel virtualChannel = node.getChannel();
            if (virtualChannel == null) {
                throw new AbortException(FAILURE_MESSAGE + "Configured node \"" + node.getDisplayName() + "\" is either not connected or offline.");
            }
            // get the working directory.
            EnvVars env = build.getEnvironment(listener);
            Optional<SigmaToolInstallation> sigmaTool = getSigma();
            if (sigmaTool.isPresent()) {
                if (node != null) {
                    SigmaToolInstallation installation = sigmaTool.get().forNode(node, listener);
                    installation = installation.forEnvironment(env);
                    logger.info("Sigma tool installation found. {}", installation.getHome());
                    return true;
                }
            }
        } catch (final Exception e) {
            logger.error("[ERROR] " + e.getMessage());
            logger.debug(e.getMessage(), e);
            build.setResult(Result.UNSTABLE);
        }
        return false;
    }

    public Optional<SigmaToolInstallation> getSigma() {
        Predicate<SigmaToolInstallation> sigmaToolFilter = (installation) -> sigmaToolName != null && sigmaToolName.equals(installation.getName());
        return Arrays.stream(getDescriptor().getInstallations()).filter(sigmaToolFilter).findFirst();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
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
