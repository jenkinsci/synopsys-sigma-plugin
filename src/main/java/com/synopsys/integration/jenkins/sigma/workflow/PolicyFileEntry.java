package com.synopsys.integration.jenkins.sigma.workflow;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class PolicyFileEntry extends AbstractDescribableImpl<PolicyFileEntry> {
    private String policyFilePath;

    @DataBoundConstructor
    public PolicyFileEntry(final String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    @SuppressWarnings("unused")
    public String getPolicyFilePath() {
        return policyFilePath;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PolicyFileEntry> {
        @Override
        public String getDisplayName() {
            return "Policy File Path";
        }
    }
}
