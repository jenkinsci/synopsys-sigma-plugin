package com.synopsys.integration.jenkins.sigma.workflow;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;

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

    @Extension
    public static class DescriptorImpl extends Descriptor<ConfigFileEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_config_entry_displayName();
        }
    }
}
