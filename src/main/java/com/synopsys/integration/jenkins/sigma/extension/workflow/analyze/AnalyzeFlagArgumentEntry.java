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

public class AnalyzeFlagArgumentEntry extends AnalyzeArgumentEntry {
    private final String name;

    @DataBoundConstructor
    public AnalyzeFlagArgumentEntry(String name) {
        super(AnalyzeArgumentType.FLAG);
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeArgumentEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_argument_flag_displayName();
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
    }
}
