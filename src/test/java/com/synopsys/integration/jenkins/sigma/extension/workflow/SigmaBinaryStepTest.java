package com.synopsys.integration.jenkins.sigma.extension.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.StreamBuildListener;
import hudson.util.ListBoxModel;

public class SigmaBinaryStepTest {
    // this isn't used in this class but the getDescriptor method internally calls Jenkins.getInstance().
    // Need the Jenkins rule to be able to test the descriptors.
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testStepFields() {
        SigmaBinaryStep step = new SigmaBinaryStep();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.setCommandLine("analyze");
        step.getDescriptor().setInstallations(toolInstallation);
        ListBoxModel toolOptions = step.getDescriptor().doFillSigmaToolNameItems();
        assertEquals(step.getDescriptor().getToolDescriptor(), toolInstallation.getDescriptor());
        assertEquals("sigma-test", step.getSigmaToolName());
        assertEquals("analyze", step.getCommandLine());
        assertFalse(step.isIgnorePolicies());
        assertTrue(step.getDescriptor().hasToolsConfigured());
        assertFalse(toolOptions.isEmpty());
    }

    @Test
    public void testPreviousBuildResult() throws IOException, InterruptedException {
        SigmaBinaryStep step = new SigmaBinaryStep();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.setCommandLine("analyze");
        step.getDescriptor().setInstallations(toolInstallation);
        AbstractBuild<?, ?> build = Mockito.mock(AbstractBuild.class);
        Launcher launcher = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BuildListener buildListener = new StreamBuildListener(byteArrayOutputStream);
        Mockito.when(build.getResult()).thenReturn(Result.ABORTED);

        boolean result = step.perform(build, launcher, buildListener);
        assertFalse(result);
    }

    @Test
    public void testNodeMissing() throws IOException, InterruptedException {
        SigmaBinaryStep step = new SigmaBinaryStep();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.setCommandLine("analyze");
        step.getDescriptor().setInstallations(toolInstallation);
        AbstractBuild<?, ?> build = Mockito.mock(AbstractBuild.class);
        Launcher launcher = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BuildListener buildListener = new StreamBuildListener(byteArrayOutputStream);

        boolean result = step.perform(build, launcher, buildListener);
        assertNull(build.getBuiltOn());
        assertFalse(result);
    }

    @Test
    public void testChannelMissing() throws IOException, InterruptedException {
        SigmaBinaryStep step = new SigmaBinaryStep();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.setCommandLine("analyze");
        step.getDescriptor().setInstallations(toolInstallation);
        AbstractBuild<?, ?> build = Mockito.mock(AbstractBuild.class);
        Node node = Mockito.mock(Node.class);
        Mockito.when(build.getBuiltOn()).thenReturn(node);

        Launcher launcher = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BuildListener buildListener = new StreamBuildListener(byteArrayOutputStream);

        boolean result = step.perform(build, launcher, buildListener);
        assertNull(build.getBuiltOn().getChannel());
        assertFalse(result);
    }
}
