package com.synopsys.integration.jenkins.sigma.extension.workflow.analyze;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

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
            return Messages.workflow_analyze_directory_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckSubDirectoryPath(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = StringUtils.isBlank(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
