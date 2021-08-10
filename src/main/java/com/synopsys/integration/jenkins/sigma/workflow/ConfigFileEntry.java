package com.synopsys.integration.jenkins.sigma.workflow;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class ConfigFileEntry extends AbstractDescribableImpl<ConfigFileEntry> {
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
            return "Config File Path";
        }
    }
}
