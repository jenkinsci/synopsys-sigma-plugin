package com.synopsys.integration.jenkins.sigma.extension.tool;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstallerDescriptor;

public class SigmaBinaryInstaller extends DownloadFromUrlInstaller {
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private final String downloadUrl;
    private final int timeout;

    @DataBoundConstructor
    public SigmaBinaryInstaller(String label, String downloadUrl, int timeout) {
        super(label);
        this.downloadUrl = downloadUrl;
        this.timeout = timeout;
    }

    @SuppressWarnings("unused")
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @SuppressWarnings("unused")
    public int getTimeout() {
        return timeout;
    }

    @Override
    public FilePath performInstallation(ToolInstallation tool, Node node, TaskListener log) {
        FilePath installLocation = preferredLocation(tool, node);
        try {
            final VirtualChannel virtualChannel = node.getChannel();
            if (virtualChannel == null) {
                throw new AbortException("Configured node \"" + node.getDisplayName() + "\" is either not connected or offline.  Cannot install Sigma.");
            }
            // timeout is in seconds convert to milliseconds.
            int timeoutInMilliseconds = timeout * 1000;
            virtualChannel.call(new FileDownloadInstaller(downloadUrl, installLocation, timeoutInMilliseconds, log));
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace(log.error("Failed to install Sigma on Node %s cause.", node.getDisplayName()));
            String errorMessage = String.format("Failed to install Sigma on Node %s.", node.getDisplayName());
            throw new RuntimeException(errorMessage, ex);
        }
        return installLocation;
    }

    @Extension
    public static final class DescriptorImpl extends ToolInstallerDescriptor<SigmaBinaryInstaller> {
        public String getDisplayName() {
            return Messages.installer_displayName();
        }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return toolType == SigmaToolInstallation.class;
        }

        //TODO add validator for download URL.
        //TODO add credentials ID in case the URL is to synopsys community.
    }
}
