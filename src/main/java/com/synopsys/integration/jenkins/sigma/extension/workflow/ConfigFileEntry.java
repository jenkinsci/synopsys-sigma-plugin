package com.synopsys.integration.jenkins.sigma.extension.workflow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

public class ConfigFileEntry extends AbstractDescribableImpl<ConfigFileEntry> {
    private static final String COMMAND_FLAG_CONFIG = "--config";
    private String configFilePath;

    @DataBoundConstructor
    public ConfigFileEntry(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @SuppressWarnings("unused")
    public String getConfigFilePath() {
        return configFilePath;
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
