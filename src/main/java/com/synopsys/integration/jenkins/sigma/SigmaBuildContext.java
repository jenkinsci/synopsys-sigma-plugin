package com.synopsys.integration.jenkins.sigma;

import java.util.Optional;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.remoting.VirtualChannel;

public class SigmaBuildContext {
    private final Launcher launcher;
    private final BuildListener listener;
    private final Node node;
    private final VirtualChannel virtualChannel;
    private final EnvVars environment;

    public SigmaBuildContext(Launcher launcher, BuildListener listener, Node node, VirtualChannel virtualChannel, EnvVars environment) {
        this.launcher = launcher;
        this.listener = listener;
        this.node = node;
        this.virtualChannel = virtualChannel;
        this.environment = environment;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public BuildListener getListener() {
        return listener;
    }

    public Optional<Node> getNode() {
        return Optional.ofNullable(node);
    }

    public Optional<VirtualChannel> getVirtualChannel() {
        return Optional.ofNullable(virtualChannel);
    }

    public EnvVars getEnvironment() {
        return environment;
    }
}
