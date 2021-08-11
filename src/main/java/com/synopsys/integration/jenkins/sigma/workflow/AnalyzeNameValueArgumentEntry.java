package com.synopsys.integration.jenkins.sigma.workflow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.common.CommandArgumentHelper;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
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
            return CommandArgumentHelper.isFormFieldEmpty(value);
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckValue(@QueryParameter String value) throws IOException, ServletException {
            return CommandArgumentHelper.isFormFieldEmpty(value);
        }
    }
}
