package com.synopsys.integration.jenkins.sigma.extension.workflow.analyze;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

public class AnalyzeNameValueArgumentEntry extends AnalyzeArgumentEntry {
    private String name;
    private String value;

    @DataBoundConstructor
    public AnalyzeNameValueArgumentEntry(final String name, final String value) {
        super(AnalyzeArgumentType.NAME_VALUE_PAIR);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeArgumentEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_argument_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = StringUtils.isBlank(value);
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
            boolean empty = StringUtils.isBlank(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
