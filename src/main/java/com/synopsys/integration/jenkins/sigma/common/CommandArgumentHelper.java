package com.synopsys.integration.jenkins.sigma.common;

import java.util.LinkedList;
import java.util.List;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

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

    public static <T extends AppendableArgument> List<ValidationResult> validateArguments(SigmaBuildContext sigmaBuildContext, FilePath workingDirectory, List<T> argumentListItems) {
        List<ValidationResult> results = new LinkedList<>();
        if (argumentListItems != null) {
            for (AppendableArgument appendableArgument : argumentListItems) {
                results.add(appendableArgument.validateArgument(sigmaBuildContext, workingDirectory));
            }
        }
        return results;
    }
}
