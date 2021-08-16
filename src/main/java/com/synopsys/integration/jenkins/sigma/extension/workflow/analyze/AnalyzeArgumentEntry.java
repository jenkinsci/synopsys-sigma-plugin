package com.synopsys.integration.jenkins.sigma.extension.workflow.analyze;

import hudson.model.AbstractDescribableImpl;

public abstract class AnalyzeArgumentEntry extends AbstractDescribableImpl<AnalyzeArgumentEntry> {
    private final AnalyzeArgumentType type;

    public AnalyzeArgumentEntry(AnalyzeArgumentType argumentType) {
        this.type = argumentType;
    }

    public AnalyzeArgumentType getType() {
        return type;
    }
}
