package com.synopsys.integration.jenkins.sigma.workflow;

import java.util.Collections;
import java.util.List;

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
        argumentListBuilder.add("${WORKSPACE}/idir");
        argumentListBuilder.add("analyze");
        argumentListBuilder.add("--format");
        argumentListBuilder.add("jenkins");
    }
}
