package com.synopsys.integration.jenkins.sigma.argument.analyze;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.argument.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

public class DirectoryArgument implements AppendableArgument, ArgumentValidator {
    private String subDirectoryPath;

    private DirectoryArgument(final String subDirectoryPath) {
        this.subDirectoryPath = subDirectoryPath;
    }

    public static DirectoryArgument of(String subDirectoryPath) {
        return new DirectoryArgument(subDirectoryPath);
    }

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
}
