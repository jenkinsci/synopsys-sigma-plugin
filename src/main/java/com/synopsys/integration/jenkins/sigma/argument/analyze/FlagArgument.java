package com.synopsys.integration.jenkins.sigma.argument.analyze;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class FlagArgument extends NamedAnalyzeArgument {

    private FlagArgument(String name) {
        super(name);
    }

    public static FlagArgument of(String name) {
        return new FlagArgument(name);
    }

    @Override
    public void appendToArgumentList(final ArgumentListBuilder argumentListBuilder) {
        if (getName().trim().startsWith("--")) {
            argumentListBuilder.add(getName().trim());
        } else if (getName().trim().startsWith("-")) {
            argumentListBuilder.add(getName().trim());
        } else {
            argumentListBuilder.add(String.format("--%s", getName().trim()));
        }
    }

    @Override
    public ValidationResult validateArgument(final SigmaBuildContext buildContext, final FilePath workingDirectory) {
        if (StringUtils.isBlank(getName())) {
            return ValidationResult.error(getName(), "Argument name is invalid. Cannot be empty.");
        }
        if (!ValidationHelper.isNameValid(getName())) {
            return ValidationResult.error(getName(), "Argument name is invalid. It is a reserved argument name.");
        }
        return ValidationResult.success(getName());
    }
}
