package com.synopsys.integration.jenkins.sigma.argument.analyze;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ValidationHelper;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class NameValueArgument extends NamedAnalyzeArgument {
    private String value;

    private NameValueArgument(String name, String value) {
        super(name);
        this.value = value;
    }

    public static NameValueArgument of(String name, String value) {
        return new NameValueArgument(name, value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(getName()) && StringUtils.isNotBlank(value)) {
            if (getName().trim().startsWith("--")) {
                argumentListBuilder.add(getName().trim());
            } else if (getName().trim().startsWith("-")) {
                argumentListBuilder.add(getName().trim());
            } else {
                argumentListBuilder.add(String.format("--%s", getName().trim()));
            }
            argumentListBuilder.add(getValue().trim());
        }
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        if (ValidationHelper.isFormFieldEmpty(getName())) {
            return ValidationResult.error(getName(), "Argument name is invalid. Cannot be empty.");
        }
        if (!ValidationHelper.isNameValid(getName())) {
            return ValidationResult.error(getName(), "Argument name is invalid. It is a reserved argument name.");
        }
        if (StringUtils.isBlank(getValue())) {
            return ValidationResult.error(getName(), getValue(), "Argument value cannot be empty");
        }

        return ValidationResult.success(getName(), getValue());
    }
}
