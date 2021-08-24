package com.synopsys.integration.jenkins.sigma.extension.tool;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import hudson.FilePath;
import hudson.ProxyConfiguration;

public class BinaryUpdateCheck implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String downloadUrl;
    private final int timeoutInMilliseconds;

    public BinaryUpdateCheck(final String downloadUrl, final int timeoutInMilliseconds) {
        this.downloadUrl = downloadUrl;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    public boolean isUpToDate(FilePath installedFrom, FilePath timestampPath) throws IOException, InterruptedException {
        boolean sameInstalledFromURL = installedFrom.exists() && installedFrom.readToString().equals(downloadUrl);
        boolean notModifiedSinceInstalled = !hasModifiedSinceInstalled(timestampPath);
        return sameInstalledFromURL && notModifiedSinceInstalled;
    }

    public boolean hasModifiedSinceInstalled(FilePath timestampFilePath) throws IOException, InterruptedException {
        if (!timestampFilePath.exists()) {
            // if timestamp file doesn't exist assume it has modified to attempt a download.
            return true;
        }
        long lastModified = timestampFilePath.lastModified();
        URL binarySourceUrl = new URL(downloadUrl);
        URLConnection binaryHostConnection = ProxyConfiguration.open(binarySourceUrl);
        if (lastModified != 0) {
            binaryHostConnection.setIfModifiedSince(lastModified);
        }
        binaryHostConnection.setConnectTimeout(timeoutInMilliseconds);
        binaryHostConnection.connect();
        long serverLastModified = binaryHostConnection.getLastModified();
        return serverLastModified > lastModified;
    }
}
