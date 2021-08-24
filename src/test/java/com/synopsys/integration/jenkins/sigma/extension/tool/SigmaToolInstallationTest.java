package com.synopsys.integration.jenkins.sigma.extension.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.utils.CreateFilePathOnNode;

import hudson.Launcher;

public class SigmaToolInstallationTest {
    private static final String HOME_DIRECTORY = "build/tmp/test/home";
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @After
    public void cleanupExecutableFile() {
        FileUtils.deleteQuietly(new File(HOME_DIRECTORY));
    }

    @Test
    public void testDescriptor() {
        SigmaToolInstallation installation = new SigmaToolInstallation("sigma-test", HOME_DIRECTORY, Collections.emptyList());
        SigmaToolInstallation.DescriptorImpl descriptor = (SigmaToolInstallation.DescriptorImpl) installation.getDescriptor();
        descriptor.setInstallations(installation);
        assertEquals(Messages.installation_displayName(), descriptor.getDisplayName());
        assertEquals(1, descriptor.getInstallations().length);
        assertEquals(installation, descriptor.getInstallations()[0]);
        assertEquals(1, descriptor.getDefaultInstallers().size());
    }

    @Test
    public void testExecutablePathNull() throws IOException, InterruptedException {
        SigmaToolInstallation installation = new SigmaToolInstallation("sigma-test", HOME_DIRECTORY, Collections.emptyList());
        Launcher launcher = jenkinsRule.createLocalLauncher();
        String path = installation.getExecutablePath(launcher);
        assertNull(path);
    }

    @Test
    public void testExecutablePath() throws IOException, InterruptedException {
        SigmaToolInstallation installation = new SigmaToolInstallation("sigma-test", HOME_DIRECTORY, Collections.emptyList());
        Launcher launcher = jenkinsRule.createLocalLauncher();
        String excutablePath = HOME_DIRECTORY + "/" + SigmaToolInstallation.UNIX_SIGMA_COMMAND;
        launcher.getChannel().call(new CreateFilePathOnNode(excutablePath));
        String path = installation.getExecutablePath(launcher);
        assertNotNull(path);
        assertTrue(path.contains(excutablePath));
    }
}
