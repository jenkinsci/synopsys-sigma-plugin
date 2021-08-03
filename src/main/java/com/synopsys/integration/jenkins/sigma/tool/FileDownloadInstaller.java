package com.synopsys.integration.jenkins.sigma.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import hudson.FilePath;
import hudson.Functions;
import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import jenkins.security.MasterToSlaveCallable;

public class FileDownloader extends MasterToSlaveCallable<Void, IOException> {
    private static final String LOG_PREFIX = "Sigma installation: ";
    private static final String INSTALLED_FROM_FILE_NAME = ".installedFrom";
    private static final String TIMESTAMP_FILE_NAME = ".timestamp";

    private final String downloadUrl;
    private final FilePath downloadLocation;
    private final int timeout;
    private final TaskListener log;

    public FileDownloader(final String downloadUrl, final FilePath downloadLocation, final int timeout, final TaskListener log) {
        this.downloadUrl = downloadUrl;
        this.downloadLocation = downloadLocation;
        this.timeout = timeout;
        this.log = log;
    }

    @Override
    public Void call() throws IOException {
        URL binarySourceUrl = new URL(downloadUrl);
        try {
            FilePath installedFrom = downloadLocation.child(INSTALLED_FROM_FILE_NAME);
            FilePath timestampPath = downloadLocation.child(TIMESTAMP_FILE_NAME);
            if (isUpToDate(installedFrom, timestampPath, binarySourceUrl)) {
                log.getLogger().println(LOG_PREFIX + "Skipping tool installation already up to date on node.");
                return null;
            }
            downloadLocation.mkdirs();
            FilePath binaryPath = downloadLocation.child("sigma");
            installBinary(binarySourceUrl, installedFrom, timestampPath, binaryPath);
        } catch (InterruptedException ex) {
            ex.printStackTrace(log.error(LOG_PREFIX + "Error installing Sigma binary on node."));
        }

        return null;
    }

    private boolean isUpToDate(FilePath installedFrom, FilePath timestampPath, URL binarySourceUrl) throws IOException, InterruptedException {
        boolean sameInstalledFromURL = hasSameInstalledFromURL(installedFrom);
        boolean notModifiedSinceInstalled = !hasModifiedSinceInstalled(timestampPath, binarySourceUrl);
        return sameInstalledFromURL && notModifiedSinceInstalled;
    }

    private boolean hasSameInstalledFromURL(FilePath installedFrom) throws IOException, InterruptedException {
        return installedFrom.exists() && installedFrom.readToString().equals(downloadUrl);
    }

    private boolean hasModifiedSinceInstalled(FilePath timestampFilePath, URL binarySourceUrl) throws IOException, InterruptedException {
        if (!timestampFilePath.exists()) {
            return false;
        }
        long lastModified = timestampFilePath.lastModified();
        URLConnection binaryHostConnection;
        try {
            binaryHostConnection = ProxyConfiguration.open(binarySourceUrl);
            if (lastModified != 0) {
                binaryHostConnection.setIfModifiedSince(lastModified);
            }
            binaryHostConnection.setConnectTimeout(timeout);
            binaryHostConnection.connect();
            long serverLastModified = binaryHostConnection.getLastModified();
            if (serverLastModified > lastModified) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace(log.error(LOG_PREFIX + "Error checking last modified time from URL %s", downloadUrl));
            return false;
        }
    }

    private void installBinary(URL binarySourceUrl, FilePath installedFrom, FilePath timestampFilePath, FilePath binaryPath) {
        File fileToWrite = new File(binaryPath.getRemote());
        try {
            log.getLogger().println(LOG_PREFIX + "Installing Sigma binary...");
            URLConnection binaryHostConnection = ProxyConfiguration.open(binarySourceUrl);
            binaryHostConnection.setConnectTimeout(timeout);
            binaryHostConnection.connect();
            writeFile(binaryHostConnection, fileToWrite);
            installedFrom.write(downloadUrl, StandardCharsets.UTF_8.name());
            timestampFilePath.touch(binaryHostConnection.getLastModified());
            // set the binary to be executable on linux based systems
            if (!Functions.isWindows()) {
                fileToWrite.setExecutable(true, false);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace(log.error(LOG_PREFIX + "Error downloading Sigma binary file from: %s to location: %s", downloadUrl, fileToWrite));
        }
    }

    private void writeFile(URLConnection binaryHostConnection, File fileToWrite) throws IOException {
        try (InputStream inputStream = binaryHostConnection.getInputStream()) {
            FileUtils.copyToFile(inputStream, fileToWrite);
        }
    }
}
