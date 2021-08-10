package com.synopsys.integration.jenkins.sigma.workflow;

import hudson.util.ArgumentListBuilder;

public interface AppendableArgument {
    void appendToArgumentList(ArgumentListBuilder argumentListBuilder);
}
