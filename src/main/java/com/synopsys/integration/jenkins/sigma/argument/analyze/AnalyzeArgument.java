package com.synopsys.integration.jenkins.sigma.argument.analyze;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.argument.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class AnalyzeArgument implements AppendableArgument, ArgumentValidator {
    private static final String COMMAND_FLAG_ANALYZE = "analyze";

    private AnalyzeArgument() {
    }

    public static AnalyzeArgument of() {
        return new AnalyzeArgument();
    }

    @Override
    public void appendToArgumentList(final ArgumentListBuilder argumentListBuilder) {
        argumentListBuilder.add(COMMAND_FLAG_ANALYZE);
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        return ValidationResult.success(COMMAND_FLAG_ANALYZE);
    }
}
