package com.synopsys.integration.jenkins.sigma.workflow;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;

public class PolicyFileEntry extends AbstractDescribableImpl<PolicyFileEntry> implements AppendableArgument {
    private String policyFilePath;

    @DataBoundConstructor
    public PolicyFileEntry(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    @SuppressWarnings("unused")
    public String getPolicyFilePath() {
        return policyFilePath;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(policyFilePath)) {
            argumentListBuilder.add("--policy");
            argumentListBuilder.add(policyFilePath.trim());
        }
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PolicyFileEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_policy_entry_displayName();
        }
    }
}
