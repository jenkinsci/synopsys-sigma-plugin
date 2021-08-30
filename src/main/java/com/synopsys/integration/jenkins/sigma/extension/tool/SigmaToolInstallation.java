/*
 * Copyright (c) 2021 Synopsys, Inc. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.sigma.extension.tool;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.extension.workflow.SigmaBinaryStep;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
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

    public Optional<String> getExecutablePath(Launcher launcher, TaskListener listener) {
        FilePath homeFilePath = new FilePath(launcher.getChannel(), getHome());
        String execName = launcher.isUnix() ? UNIX_SIGMA_COMMAND : WINDOWS_SIGMA_COMMAND;
        FilePath executableFilePath = homeFilePath.child(execName);
        try {
            if (executableFilePath.exists()) {
                return Optional.of(executableFilePath.getRemote());
            }
        } catch (IOException ex) {
            listener.error("Error getting tool installation path %s", ex.getMessage());
        } catch (InterruptedException ex) {
            listener.error("Error getting tool installation path %s", ex.getMessage());
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
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
            SigmaBinaryInstaller installer = new SigmaBinaryInstaller(null);
            installer.setTimeout(SigmaBinaryInstaller.DEFAULT_TIMEOUT_SECONDS);
            return Collections.singletonList(installer);
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
