package com.synopsys.integration.jenkins.sigma.validator;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;

import hudson.FilePath;

public interface ArgumentValidator {
    ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory);
}
