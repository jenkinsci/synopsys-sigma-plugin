package com.synopsys.integration.jenkins.sigma.mocks;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.TopLevelItem;
import hudson.remoting.Callable;
import hudson.slaves.NodeDescriptor;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.ClockDifference;
import hudson.util.DescribableList;

public class TestNode extends Node {
    @NonNull
    @Override
    public String getNodeName() {
        return "Test Node";
    }

    @Override
    public void setNodeName(final String name) {

    }

    @Override
    public String getNodeDescription() {
        return null;
    }

    @Override
    public Launcher createLauncher(final TaskListener listener) {
        return null;
    }

    @Override
    public int getNumExecutors() {
        return 0;
    }

    @Override
    public Mode getMode() {
        return null;
    }

    @Override
    protected Computer createComputer() {
        return null;
    }

    @Override
    public String getLabelString() {
        return null;
    }

    @Override
    public FilePath getWorkspaceFor(final TopLevelItem item) {
        return null;
    }

    @Override
    public FilePath getRootPath() {
        return null;
    }

    @NonNull
    @Override
    public DescribableList<NodeProperty<?>, NodePropertyDescriptor> getNodeProperties() {
        return null;
    }

    @Override
    public NodeDescriptor getDescriptor() {
        return null;
    }

    @Override
    public Callable<ClockDifference, IOException> getClockDifferenceCallable() {
        return null;
    }
}
