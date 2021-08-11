package com.synopsys.integration.jenkins.sigma.common;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public interface AppendableArgument {
    void appendToArgumentList(ArgumentListBuilder argumentListBuilder);

    ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory);
}
