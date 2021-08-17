package com.synopsys.integration.jenkins.sigma.argument;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class PolicyFileArgument implements AppendableArgument, ArgumentValidator {
    public static final String ARGUMENT_NAME_POLICY = "--policy";
    private String policyFilePath;

    private PolicyFileArgument(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    public static PolicyFileArgument of(String policyFilePath) {
        return new PolicyFileArgument(policyFilePath);
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(policyFilePath)) {
            argumentListBuilder.add(ARGUMENT_NAME_POLICY);
            argumentListBuilder.add(policyFilePath.trim());
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        boolean empty = StringUtils.isBlank(policyFilePath);
        if (empty) {
            return ValidationResult.error(ARGUMENT_NAME_POLICY, policyFilePath, "File path cannot be empty");
        }
        return ValidationResult.success(ARGUMENT_NAME_POLICY, policyFilePath);
    }
}
