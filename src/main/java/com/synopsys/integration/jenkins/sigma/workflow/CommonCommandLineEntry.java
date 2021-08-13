package com.synopsys.integration.jenkins.sigma.workflow;

import java.util.Collections;
import java.util.List;

import com.synopsys.integration.jenkins.sigma.common.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.common.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.common.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class CommonCommandLineEntry implements AppendableArgument {

    private CommonCommandLineEntry() {
        // hide default constructor
    }

    public static List<AppendableArgument> toAppendableList() {
        return Collections.singletonList(new CommonCommandLineEntry());
    }

    @Override
    public void appendToArgumentList(final ArgumentListBuilder argumentListBuilder) {
        argumentListBuilder.add("--working-dir");
        argumentListBuilder.add("${WORKSPACE}/.sigma-dir");
        argumentListBuilder.add("analyze");
        argumentListBuilder.add("--format");
        argumentListBuilder.add("jenkins");
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        return ValidationResult.success("common command line arguments", "--working-dir ${WORKSPACE}/idir analyze --format jenkins");
    }
}
