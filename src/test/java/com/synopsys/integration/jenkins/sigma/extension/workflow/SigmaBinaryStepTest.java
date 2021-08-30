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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.StreamBuildListener;
import hudson.model.TaskListener;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.util.ListBoxModel;
import hudson.util.StreamTaskListener;

public class SigmaBinaryStepTest {
    // this isn't used in this class but the getDescriptor method internally calls Jenkins.getInstance().
    // Need the Jenkins rule to be able to test the descriptors.
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testStepFields() {
        SigmaBinaryStep step = new SigmaBinaryStep();
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.setCommandLine("analyze");
        assertEquals("sigma-test", step.getSigmaToolName());
        assertEquals("analyze", step.getCommandLine());
        assertFalse(step.isIgnorePolicies());
    }

    @Test
    public void testDescriptor() {
        SigmaBinaryStep.DescriptorImpl descriptor = new SigmaBinaryStep.DescriptorImpl();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        descriptor.setInstallations(toolInstallation);
        ListBoxModel toolOptions = descriptor.doFillSigmaToolNameItems();
        assertFalse(toolOptions.isEmpty());
        assertTrue(descriptor.hasToolsConfigured());
        assertEquals(toolInstallation.getDescriptor(), descriptor.getToolDescriptor());
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

    @Test
    public void testPipelinePreviousBuildResult() throws IOException, InterruptedException {
        SigmaBinaryStep step = new SigmaBinaryStep();
        SigmaToolInstallation toolInstallation = new SigmaToolInstallation("sigma-test", "home", Collections.emptyList());
        step.setSigmaToolName("sigma-test");
        step.setIgnorePolicies(false);
        step.getDescriptor().setInstallations(toolInstallation);
        Run<?, ?> run = Mockito.mock(Run.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TaskListener taskListener = new StreamTaskListener(byteArrayOutputStream);
        Launcher launcher = new Launcher.LocalLauncher(taskListener);
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars environment = prop.getEnvVars();

        ArgumentCaptor<Result> buildResultCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.when(run.getResult()).thenReturn(Result.ABORTED);
        Mockito.doNothing().when(run).setResult(buildResultCaptor.capture());
        step.perform(run, null, environment, launcher, taskListener);
        assertEquals(Result.UNSTABLE, buildResultCaptor.getValue());
    }
}
