package com.synopsys.integration.jenkins.sigma.workflow;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class AnalyzeDirectoryEntry extends AbstractDescribableImpl<AnalyzeDirectoryEntry> {
    private String subDirectoryPath;

    @DataBoundConstructor
    public AnalyzeDirectoryEntry(final String subDirectoryPath) {
        this.subDirectoryPath = subDirectoryPath;
    }

    @SuppressWarnings("unused")
    public String getSubDirectoryPath() {
        return subDirectoryPath;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeDirectoryEntry> {
        @Override
        public String getDisplayName() {
            return "Analyze Sub-Directory";
        }
    }
}
