package com.synopsys.integration.jenkins.sigma.workflow;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;

public class AnalyzeArgumentEntry extends AbstractDescribableImpl<AnalyzeArgumentEntry> implements AppendableArgument {
    private String name;
    private String value;

    @DataBoundConstructor
    public AnalyzeArgumentEntry(final String name, final String value) {
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
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            if (getName().trim().startsWith("--")) {
                argumentListBuilder.add(getName().trim());
            } else {
                argumentListBuilder.add(String.format("--%s", getName().trim()));
            }
            argumentListBuilder.add(getValue().trim());
        }
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeArgumentEntry> {
        @Override
        public String getDisplayName() {
            return "Analyze Argument";
        }
    }
}
