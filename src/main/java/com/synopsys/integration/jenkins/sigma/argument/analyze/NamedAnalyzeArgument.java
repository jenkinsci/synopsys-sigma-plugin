package com.synopsys.integration.jenkins.sigma.argument.analyze;

import com.synopsys.integration.jenkins.sigma.argument.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;

public abstract class NamedAnalyzeArgument implements AppendableArgument, ArgumentValidator {
    private String name;

    public NamedAnalyzeArgument(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
