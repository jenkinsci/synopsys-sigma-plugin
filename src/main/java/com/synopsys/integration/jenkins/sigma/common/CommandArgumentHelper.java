package com.synopsys.integration.jenkins.sigma.common;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.Messages;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

public class CommandArgumentHelper {

    private CommandArgumentHelper() {
        // TODO: Add a set of strings of disallowed command line parameters.
        // can't instantiate
    }

    public static <T extends AppendableArgument> ArgumentListBuilder addCommandLineArguments(ArgumentListBuilder argumentListBuilder, List<T> argumentListItems) {
        if (argumentListItems != null) {
            for (AppendableArgument appendableArgument : argumentListItems) {
                appendableArgument.appendToArgumentList(argumentListBuilder);
            }
        }
        return argumentListBuilder;
    }

    public static FormValidation isRelativeFilePath(@Nonnull FilePath workingDirectory, @Nonnull String filePath) throws IOException {
        return workingDirectory.validateRelativeDirectory(filePath);
    }

    public static boolean doesFileExist(@Nonnull FilePath workingDirectory, @Nonnull String filePath) throws IOException, InterruptedException {
        FilePath path = new FilePath(workingDirectory, filePath);
        return path.exists();
    }

    public static FormValidation isFormFieldEmpty(String value) {
        if (StringUtils.isBlank(value)) {
            return FormValidation.error(Messages.build_commandline_empty_field());
        }
        return FormValidation.ok();
    }
}
