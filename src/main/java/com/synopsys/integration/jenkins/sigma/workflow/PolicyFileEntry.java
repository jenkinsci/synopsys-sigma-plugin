package com.synopsys.integration.jenkins.sigma.workflow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.common.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.common.ValidationResult;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

public class PolicyFileEntry extends AbstractDescribableImpl<PolicyFileEntry> implements AppendableArgument {
    private String policyFilePath;

    @DataBoundConstructor
    public PolicyFileEntry(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    @SuppressWarnings("unused")
    public String getPolicyFilePath() {
        return policyFilePath;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(policyFilePath)) {
            argumentListBuilder.add("--policy");
            argumentListBuilder.add(policyFilePath.trim());
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        boolean empty = ValidationHelper.isFormFieldEmpty(getPolicyFilePath());
        if (empty) {
            return ValidationResult.error("--policy", getPolicyFilePath(), "File path cannot be empty");
        }
        return ValidationResult.success("--policy", getPolicyFilePath());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PolicyFileEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_policy_entry_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckPolicyFilePath(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = ValidationHelper.isFormFieldEmpty(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
