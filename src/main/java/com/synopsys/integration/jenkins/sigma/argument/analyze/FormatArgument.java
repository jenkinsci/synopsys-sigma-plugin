package com.synopsys.integration.jenkins.sigma.argument.analyze;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.argument.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class FormatArgument implements AppendableArgument, ArgumentValidator {

    private static final String COMMAND_FLAG_FORMAT = "--format";
    private static final String FORMAT_JENKINS = "jenkins";

    private FormatArgument() {
    }

    public static FormatArgument of() {
        return new FormatArgument();
    }

    @Override
    public void appendToArgumentList(final ArgumentListBuilder argumentListBuilder) {
        argumentListBuilder.add(COMMAND_FLAG_FORMAT);
        argumentListBuilder.add(FORMAT_JENKINS);
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        return ValidationResult.success(COMMAND_FLAG_FORMAT, FORMAT_JENKINS);
    }
}
