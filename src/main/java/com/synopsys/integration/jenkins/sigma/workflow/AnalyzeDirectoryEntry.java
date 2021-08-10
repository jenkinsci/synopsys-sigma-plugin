package com.synopsys.integration.jenkins.sigma.workflow;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;

public class AnalyzeDirectoryEntry extends AbstractDescribableImpl<AnalyzeDirectoryEntry> implements AppendableArgument {
    private String subDirectoryPath;

    @DataBoundConstructor
    public AnalyzeDirectoryEntry(final String subDirectoryPath) {
        this.subDirectoryPath = subDirectoryPath;
    }

    @SuppressWarnings("unused")
    public String getSubDirectoryPath() {
        return subDirectoryPath;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(subDirectoryPath)) {
            argumentListBuilder.add(subDirectoryPath.trim());
        }
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeDirectoryEntry> {
        @Override
        public String getDisplayName() {
            return "Analyze Sub-Directory";
        }
    }
}
