package com.synopsys.integration.jenkins.sigma;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.synopsys.integration.jenkins.sigma.mocks.TestBuildListener;
import com.synopsys.integration.jenkins.sigma.mocks.TestChannel;
import com.synopsys.integration.jenkins.sigma.mocks.TestNode;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.slaves.EnvironmentVariablesNodeProperty;

public class SigmaBuildContextTest {
    @Test
    public void testBuildContext() {
        TaskListener taskListener = null;
        BuildListener buildListener = new TestBuildListener();
        VirtualChannel virtualChannel = new TestChannel();
        Node node = new TestNode();
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        Launcher launcher = new Launcher.LocalLauncher(taskListener);
        SigmaBuildContext buildContext = new SigmaBuildContext(launcher, buildListener, node, virtualChannel, envVars);
        assertEquals(launcher, buildContext.getLauncher());
        assertEquals(buildListener, buildContext.getListener());
        assertTrue(buildContext.getNode().isPresent());
        assertEquals(node, buildContext.getNode().get());
        assertEquals(virtualChannel, buildContext.getVirtualChannel().get());
        assertEquals(envVars, buildContext.getEnvironment());
    }

    @Test
    public void testBuildContextEmptyFields() {
        TaskListener taskListener = null;
        BuildListener buildListener = new TestBuildListener();
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        Launcher launcher = new Launcher.LocalLauncher(taskListener);
        SigmaBuildContext buildContext = new SigmaBuildContext(launcher, buildListener, null, null, envVars);
        assertEquals(launcher, buildContext.getLauncher());
        assertEquals(buildListener, buildContext.getListener());
        assertEquals(envVars, buildContext.getEnvironment());
        assertFalse(buildContext.getNode().isPresent());
        assertFalse(buildContext.getVirtualChannel().isPresent());
    }
}
