package com.synopsys.integration.jenkins.sigma.argument;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class WorkingDirectoryArgument implements AppendableArgument, ArgumentValidator {

    private static final String COMMAND_FLAG_WORKING_DIRECTORY = "--working-dir";
    private static final String WORKSPACE_SIGMA_DIR = "${WORKSPACE}/.sigma-dir";

    private WorkingDirectoryArgument() {
    }

    public static WorkingDirectoryArgument of() {
        return new WorkingDirectoryArgument();
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        argumentListBuilder.add(COMMAND_FLAG_WORKING_DIRECTORY);
        argumentListBuilder.add(WORKSPACE_SIGMA_DIR);
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        return ValidationResult.success(COMMAND_FLAG_WORKING_DIRECTORY, WORKSPACE_SIGMA_DIR);
    }
}
