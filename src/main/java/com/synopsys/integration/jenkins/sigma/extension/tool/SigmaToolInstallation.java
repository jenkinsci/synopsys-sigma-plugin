package com.synopsys.integration.jenkins.sigma.extension.tool;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.extension.workflow.SigmaBinaryStep;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Functions;
import hudson.Launcher;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;
import jenkins.security.MasterToSlaveCallable;

public class SigmaToolInstallation extends ToolInstallation implements EnvironmentSpecific<SigmaToolInstallation>, NodeSpecific<SigmaToolInstallation>, Serializable {
    public static final String UNIX_SIGMA_COMMAND = "sigma";
    public static final String WINDOWS_SIGMA_COMMAND = "sigma.exe";

    @DataBoundConstructor
    public SigmaToolInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public SigmaToolInstallation forEnvironment(EnvVars environment) {
        return new SigmaToolInstallation(getName(), environment.expand(getHome()), getProperties().toList());
    }

    public SigmaToolInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new SigmaToolInstallation(getName(), translateFor(node, log), getProperties().toList());
    }

    public String getExecutablePath(Launcher launcher) throws IOException, InterruptedException {
        return launcher.getChannel().call(new GetExecutablePath(getHome()));
    }

    private static final class GetExecutablePath extends MasterToSlaveCallable<String, IOException> {
        private final String sigmaHome;

        public GetExecutablePath(String sigmaHome) {
            this.sigmaHome = sigmaHome;
        }

        @Override
        public String call() {
            String execName = (Functions.isWindows()) ? WINDOWS_SIGMA_COMMAND : UNIX_SIGMA_COMMAND;
            File exe = new File(sigmaHome, execName);
            if (exe.exists()) {
                return exe.getPath();
            }
            return null;
        }
    }

    @Extension
    @Symbol("sigmaTool")
    public static final class DescriptorImpl extends ToolDescriptor<SigmaToolInstallation> {

        @Override
        public String getDisplayName() {
            return Messages.installation_displayName();
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
            return Jenkins.get().getDescriptorByType(SigmaBinaryStep.DescriptorImpl.class);
        }
    }
}
