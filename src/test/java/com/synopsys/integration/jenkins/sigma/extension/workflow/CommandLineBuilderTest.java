package com.synopsys.integration.jenkins.sigma.extension.workflow;

import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;
import com.synopsys.integration.jenkins.sigma.utils.ArgumentListAssertions;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.remoting.VirtualChannel;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.tools.ToolLocationNodeProperty;
import hudson.util.ArgumentListBuilder;
import hudson.util.DescribableList;

public class CommandLineBuilderTest {

    @Test
    public void testDefaultCommandLineUnix() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, false, null);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma", "analyze", "--format", "jenkins");
    }

    @Test
    public void testCommandLineWithIgnoresPolicyUnix() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, true, null);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma", "analyze", CommandLineBuilder.COMMAND_TOKEN_IGNORE_POLICIES, "--format", "jenkins");
    }

    @Test
    public void testDefaultCommandLineWindows() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.FALSE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, false, null);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma.exe", "analyze", "--format", "jenkins");
    }

    @Test
    public void testCommandLineWithIgnoresPolicyWindows() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.FALSE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, true, null);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma.exe", "analyze", CommandLineBuilder.COMMAND_TOKEN_IGNORE_POLICIES, "--format", "jenkins");
    }

    @Test
    public void testCommandLineOverrideWithIgnorePolicies() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();

        String commandLineOverride = "--config config/file/path --policy policy/file/path analyze --format gitlab";
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, true, commandLineOverride);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma", "--config", "config/file/path", "--policy", "policy/file/path", "analyze", CommandLineBuilder.COMMAND_TOKEN_IGNORE_POLICIES, "--format", "gitlab");
    }

    @Test
    public void testCommandLineOverrideMissingAnalyze() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();

        String commandLineOverride = "--config config/file/path --policy policy/file/path checkers";
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, true, commandLineOverride);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma", "--config", "config/file/path", "--policy", "policy/file/path", "checkers");
    }

    @Test
    public void testCommandLineOverrideEmptyString() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);

        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(listener.getLogger()).thenReturn(System.out);

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, null, false, "              \t\n");
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "sigma", "analyze", "--format", "jenkins");
    }

    @Test
    public void testDefaultWithToolPath() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);
        DescribableList<NodeProperty<?>, NodePropertyDescriptor> nodePropertyList = Mockito.mock(DescribableList.class);
        ToolLocationNodeProperty nodeProperty = Mockito.mock(ToolLocationNodeProperty.class);
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaToolInstallation sigmaToolInstallation = new SigmaToolInstallation("sigma-test", "test/home", Collections.emptyList());

        Mockito.when(virtualChannel.call(Mockito.any())).thenReturn(sigmaToolInstallation.getHome() + "/" + SigmaToolInstallation.UNIX_SIGMA_COMMAND);
        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(launcher.getChannel()).thenReturn(virtualChannel);
        Mockito.when(listener.getLogger()).thenReturn(System.out);
        Mockito.when(nodeProperty.getHome(Mockito.any(SigmaToolInstallation.class))).thenReturn("test/home");
        Mockito.when(node.getNodeProperties()).thenReturn(nodePropertyList);
        Mockito.when(nodePropertyList.get(Mockito.eq(ToolLocationNodeProperty.class))).thenReturn(nodeProperty);

        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, sigmaToolInstallation, false, null);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "test/home/sigma", "analyze", "--format", "jenkins");
    }

    @Test
    public void testCommandLineOverrideWithToolPath() throws Exception {
        VirtualChannel virtualChannel = Mockito.mock(VirtualChannel.class);
        Launcher launcher = Mockito.mock(Launcher.class);
        BuildListener listener = Mockito.mock(BuildListener.class);
        Node node = Mockito.mock(Node.class);
        DescribableList<NodeProperty<?>, NodePropertyDescriptor> nodePropertyList = Mockito.mock(DescribableList.class);
        ToolLocationNodeProperty nodeProperty = Mockito.mock(ToolLocationNodeProperty.class);
        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        SigmaToolInstallation sigmaToolInstallation = new SigmaToolInstallation("sigma-test", "test/home", Collections.emptyList());

        Mockito.when(virtualChannel.call(Mockito.any())).thenReturn(sigmaToolInstallation.getHome() + "/" + SigmaToolInstallation.UNIX_SIGMA_COMMAND);
        Mockito.when(launcher.isUnix()).thenReturn(Boolean.TRUE);
        Mockito.when(launcher.getChannel()).thenReturn(virtualChannel);
        Mockito.when(listener.getLogger()).thenReturn(System.out);
        Mockito.when(nodeProperty.getHome(Mockito.any(SigmaToolInstallation.class))).thenReturn("test/home");
        Mockito.when(node.getNodeProperties()).thenReturn(nodePropertyList);
        Mockito.when(nodePropertyList.get(Mockito.eq(ToolLocationNodeProperty.class))).thenReturn(nodeProperty);

        String commandLineOverride = "--config config/file/path --policy policy/file/path checkers";
        SigmaBuildContext sigmaBuildContext = new SigmaBuildContext(launcher, listener, node, virtualChannel, envVars);
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder(sigmaBuildContext, sigmaToolInstallation, true, commandLineOverride);
        ArgumentListBuilder argumentListBuilder = commandLineBuilder.buildArgumentList();
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, "test/home/sigma", "--config", "config/file/path", "--policy", "policy/file/path", "checkers");
    }
}
