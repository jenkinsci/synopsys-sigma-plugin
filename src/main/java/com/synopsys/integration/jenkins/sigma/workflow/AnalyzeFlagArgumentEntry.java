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

public class AnalyzeFlagArgumentEntry extends AnalyzeArgumentEntry {
    private final String name;

    @DataBoundConstructor
    public AnalyzeFlagArgumentEntry(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @Override
    public void appendToArgumentList(final ArgumentListBuilder argumentListBuilder) {
        // TODO may not need to test if the validate is called first.
        if (StringUtils.isNotBlank(name)) {
            if (getName().trim().startsWith("--")) {
                argumentListBuilder.add(getName().trim());
            } else {
                argumentListBuilder.add(String.format("--%s", getName().trim()));
            }
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        return ValidationResult.success(getName(), "");
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeArgumentEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_argument_flag_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            return CommandArgumentHelper.isFormFieldEmpty(value);
        }
    }
}
