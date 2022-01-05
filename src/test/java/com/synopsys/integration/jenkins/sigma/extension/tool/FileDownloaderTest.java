/*
 * Copyright (c) 2021 Synopsys, Inc. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.sigma.extension.tool;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.synopsys.integration.jenkins.sigma.utils.SigmaTestUtil;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;

public class FileDownloaderTest {
    private static final String HOME_DIRECTORY = "build/tmp/test/file_download_test/home";
    private SigmaTestUtil sigmaTestUtil = new SigmaTestUtil();
    private File homeDirectory = new File(HOME_DIRECTORY);

    @Before
    public void initializeData() throws IOException {
        sigmaTestUtil.loadProperties();
        homeDirectory.mkdirs();
    }

    @After
    public void cleanupDirectories() {
        FileUtils.deleteQuietly(homeDirectory);
    }

    @Test
    public void testInstall() throws IOException, InterruptedException {
        String downloadUrl = sigmaTestUtil.getDownloadUrl();
        int timeout = sigmaTestUtil.getTimeoutInSeconds() * 1000;
        FilePath downloadLocation = new FilePath(homeDirectory);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TaskListener log = new StreamTaskListener(byteArrayOutputStream);
        BinaryUpdateCheck updateCheck = new BinaryUpdateCheck(downloadUrl, timeout) {
            @Override
            public boolean isUpToDate(final FilePath installedFrom, final FilePath timestampPath) throws IOException, InterruptedException {
                return false;
            }
        };
        FileDownloadInstaller installer = new FileDownloadInstaller(downloadUrl, downloadLocation, timeout, log, updateCheck);
        installer.call();
        assertTrue(downloadLocation.exists());
        String binaryName = "sigma";
        if (Functions.isWindows()) {
            binaryName += ".exe";
        }
        assertTrue(downloadLocation.child(binaryName).exists());
    }

    @Test
    public void testInstallUpToDate() throws IOException, InterruptedException {
        String downloadUrl = sigmaTestUtil.getDownloadUrl();
        FilePath downloadLocation = new FilePath(homeDirectory);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TaskListener log = new StreamTaskListener(byteArrayOutputStream);
        BinaryUpdateCheck updateCheck = new BinaryUpdateCheck(downloadUrl, 0) {
            @Override
            public boolean isUpToDate(final FilePath installedFrom, final FilePath timestampPath) {
                return true;
            }
        };
        FileDownloadInstaller installer = new FileDownloadInstaller(downloadUrl, downloadLocation, 1, log, updateCheck);
        installer.call();
        assertTrue(byteArrayOutputStream.toString().contains("Skipping tool installation"));
    }

    @Test(expected = IOException.class)
    public void testUpdateCheckIOException() throws IOException, InterruptedException {
        String downloadUrl = sigmaTestUtil.getDownloadUrl();
        FilePath downloadLocation = new FilePath(homeDirectory);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TaskListener log = new StreamTaskListener(byteArrayOutputStream);
        BinaryUpdateCheck updateCheck = new BinaryUpdateCheck(downloadUrl, 1) {
            @Override
            public boolean isUpToDate(final FilePath installedFrom, final FilePath timestampPath) throws IOException {
                throw new IOException("Test I/O exception");
            }
        };
        FileDownloadInstaller installer = new FileDownloadInstaller(downloadUrl, downloadLocation, 1, log, updateCheck);
        installer.call();
    }
}
