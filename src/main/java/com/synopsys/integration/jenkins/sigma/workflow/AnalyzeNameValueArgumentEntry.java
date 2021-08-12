package com.synopsys.integration.jenkins.sigma.workflow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.common.ValidationResult;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

public class AnalyzeNameValueArgumentEntry extends AnalyzeArgumentEntry {
    private String name;
    private String value;

    @DataBoundConstructor
    public AnalyzeNameValueArgumentEntry(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        // TODO may not need to test if the validate is called first.
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            if (getName().trim().startsWith("--")) {
                argumentListBuilder.add(getName().trim());
            } else {
                argumentListBuilder.add(String.format("--%s", getName().trim()));
            }
            argumentListBuilder.add(getValue().trim());
        }
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        boolean nameValid = ValidationHelper.isNameValid(getName());
        if (!nameValid) {
            return ValidationResult.error(getName(), getValue(), "Argument name is invalid. It is a reserved argument name.");
        }
        if (StringUtils.isBlank(getValue())) {
            return ValidationResult.error(getName(), getValue(), "Argument value cannot be empty");
        }

        return ValidationResult.success(getName(), getValue());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeArgumentEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_argument_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = ValidationHelper.isFormFieldEmpty(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }

            if (!ValidationHelper.isNameValid(value)) {
                return FormValidation.error(Messages.build_commandline_reserved_name());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckValue(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = ValidationHelper.isFormFieldEmpty(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
