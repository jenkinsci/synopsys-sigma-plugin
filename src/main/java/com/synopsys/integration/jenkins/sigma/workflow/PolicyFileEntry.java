package com.synopsys.integration.jenkins.sigma.workflow;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.common.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.common.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class PolicyFileEntry implements AppendableArgument {
    private static final String COMMAND_FLAG_POLICY = "--policy";
    private String policyFilePath;

    private PolicyFileEntry(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    public static List<PolicyFileEntry> toAppendableList(String policyFilePath) {
        return Collections.singletonList(new PolicyFileEntry(policyFilePath));
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
