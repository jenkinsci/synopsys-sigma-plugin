package com.synopsys.integration.jenkins.sigma.tool;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import hudson.FilePath;
import jenkins.security.MasterToSlaveCallable;

public class FileDownloader extends MasterToSlaveCallable<Void, IOException> {
    private final String downloadUrl;
    private final FilePath downloadLocation;
    private final int timeout;

    public FileDownloader(final String downloadUrl, final FilePath downloadLocation, final int timeout) {
        this.downloadUrl = downloadUrl;
        this.downloadLocation = downloadLocation;
        this.timeout = timeout;
    }

    @Override
    public Void call() throws IOException {
        File fileToWrite = new File(downloadLocation.getRemote());
        URL binarySourceUrl = new URL(downloadUrl);
        FileUtils.copyURLToFile(binarySourceUrl, fileToWrite, timeout, timeout);
        //TODO do we need to make the file executable?
        return null;
    }
}
