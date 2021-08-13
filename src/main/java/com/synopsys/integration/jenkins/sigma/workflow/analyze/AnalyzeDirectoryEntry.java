package com.synopsys.integration.jenkins.sigma.workflow.analyze;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.synopsys.integration.jenkins.sigma.Messages;
import com.synopsys.integration.jenkins.sigma.common.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationHelper;
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
        String path = getSubDirectoryPath();
        String fieldName = "analyze directory";
        try {
            boolean subDirectoryEmpty = ValidationHelper.isFormFieldEmpty(path);
            FormValidation relativePath = isRelativeFilePath(workingDirectory, path);

            if (subDirectoryEmpty) {
                return ValidationResult.error(fieldName, path, "The file path cannot be empty.");
            }

            if (relativePath.kind == FormValidation.Kind.ERROR) {
                return ValidationResult.error(fieldName, path, "The file path isn't a relative path with respect to the workspace.");
            }

            boolean exists = doesFileExist(workingDirectory, path);
            if (!exists) {
                return ValidationResult.error(fieldName, path, "The file path does not exist in the workspace.");
            }

        } catch (IOException | InterruptedException ex) {
            return ValidationResult.error(fieldName, path, ex.getMessage());
        }
        return ValidationResult.success(fieldName, path);
    }

    private FormValidation isRelativeFilePath(FilePath workingDirectory, String filePath) throws IOException {
        return workingDirectory.validateRelativeDirectory(filePath);
    }

    private boolean doesFileExist(FilePath workingDirectory, String filePath) throws IOException, InterruptedException {
        FilePath path = new FilePath(workingDirectory, filePath);
        return path.exists();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalyzeDirectoryEntry> {
        @Override
        public String getDisplayName() {
            return Messages.workflow_analyze_directory_displayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckSubDirectoryPath(@QueryParameter String value) throws IOException, ServletException {
            boolean empty = ValidationHelper.isFormFieldEmpty(value);
            if (empty) {
                return FormValidation.error(Messages.build_commandline_empty_field());
            }
            return FormValidation.ok();
        }
    }
}
