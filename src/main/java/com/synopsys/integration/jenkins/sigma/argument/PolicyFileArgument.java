package com.synopsys.integration.jenkins.sigma.argument;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class PolicyFileArgument implements AppendableArgument, ArgumentValidator {
    private static final String COMMAND_FLAG_POLICY = "--policy";
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
            argumentListBuilder.add(COMMAND_FLAG_POLICY);
            argumentListBuilder.add(policyFilePath.trim());
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        boolean empty = ValidationHelper.isFormFieldEmpty(policyFilePath);
        if (empty) {
            return ValidationResult.error(COMMAND_FLAG_POLICY, policyFilePath, "File path cannot be empty");
        }
        return ValidationResult.success(COMMAND_FLAG_POLICY, policyFilePath);
    }
}
