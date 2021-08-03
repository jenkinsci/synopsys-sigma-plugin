package com.synopsys.integration.jenkins.sigma.tool;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.workflow.SigmaBinaryStep;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;

public class SigmaToolInstallation extends ToolInstallation implements EnvironmentSpecific<SigmaToolInstallation>, NodeSpecific<SigmaToolInstallation>, Serializable {

    @DataBoundConstructor
    public SigmaToolInstallation(final String name, final String home, final List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public SigmaToolInstallation forEnvironment(EnvVars environment) {
        return new SigmaToolInstallation(getName(), environment.expand(getHome()), getProperties().toList());
    }

    public SigmaToolInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new SigmaToolInstallation(getName(), translateFor(node, log), getProperties().toList());
    }

    @Extension
    public static final class DescriptorImpl extends ToolDescriptor<SigmaToolInstallation> {

        @Override
        public String getDisplayName() {
            return "Sigma";
        }

        @Override
        public List<? extends ToolInstaller> getDefaultInstallers() {
            return Collections.singletonList(new SigmaBinaryInstaller(null, null, SigmaBinaryInstaller.DEFAULT_TIMEOUT_SECONDS));
        }

        @Override
        public SigmaToolInstallation[] getInstallations() {
            return getSigmaBinaryDescriptor().getInstallations();
        }

        @Override
        public void setInstallations(final SigmaToolInstallation... installations) {
            getSigmaBinaryDescriptor().setInstallations(installations);
        }

        private SigmaBinaryStep.DescriptorImpl getSigmaBinaryDescriptor() {
            return Jenkins.getInstance().getDescriptorByType(SigmaBinaryStep.DescriptorImpl.class);
        }
    }
}
