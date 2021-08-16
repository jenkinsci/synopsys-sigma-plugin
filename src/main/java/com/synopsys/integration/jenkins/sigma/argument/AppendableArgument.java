package com.synopsys.integration.jenkins.sigma.argument;

import hudson.util.ArgumentListBuilder;

public interface AppendableArgument {
    void appendToArgumentList(ArgumentListBuilder argumentListBuilder);
}
