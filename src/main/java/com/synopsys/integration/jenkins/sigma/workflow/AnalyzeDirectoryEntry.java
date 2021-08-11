package com.synopsys.integration.jenkins.sigma.workflow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.common.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.common.CommandArgumentHelper;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationResult;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

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

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        try {
            FormValidation subDirectoryDefined = CommandArgumentHelper.isFormFieldEmpty(getSubDirectoryPath());
            FormValidation relativePath = CommandArgumentHelper.isRelativeFilePath(workingDirectory, getSubDirectoryPath());
            if (subDirectoryDefined.kind == FormValidation.Kind.ERROR) {
                return ValidationResult.error("analyze directory", getSubDirectoryPath(), "sub-directory cannot be empty.");
            }

            if (relativePath.kind == FormValidation.Kind.ERROR) {
                return ValidationResult.error("analyze directory", getSubDirectoryPath(), "sub-directory isn't a relative path with respect to the workspace.");
            }
        } catch (IOException ex) {
            return ValidationResult.error("analyze directory", getSubDirectoryPath(), ex.getMessage());
        }
        return ValidationResult.success("analyze directory", getSubDirectoryPath());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeDirectoryEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_directory_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckSubDirectoryPath(@QueryParameter String value) throws IOException, ServletException {
            return CommandArgumentHelper.isFormFieldEmpty(value);
        }
    }
}
