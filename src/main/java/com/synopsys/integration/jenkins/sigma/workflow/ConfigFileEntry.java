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

public class ConfigFileEntry extends AbstractDescribableImpl<ConfigFileEntry> implements AppendableArgument {
    private String configFilePath;

    @DataBoundConstructor
    public ConfigFileEntry(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @SuppressWarnings("unused")
    public String getConfigFilePath() {
        return configFilePath;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(configFilePath)) {
            argumentListBuilder.add("--config");
            argumentListBuilder.add(configFilePath.trim());
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        boolean empty = ValidationHelper.isFormFieldEmpty(getConfigFilePath());
        if (empty) {
            return ValidationResult.error("--config", getConfigFilePath(), "File path cannot be empty");
        }
        return ValidationResult.success("--config", getConfigFilePath());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ConfigFileEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_config_entry_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckConfigFilePath(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = ValidationHelper.isFormFieldEmpty(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
