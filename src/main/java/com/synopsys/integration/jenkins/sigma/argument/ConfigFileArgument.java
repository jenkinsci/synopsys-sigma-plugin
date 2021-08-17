package com.synopsys.integration.jenkins.sigma.argument;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class ConfigFileArgument implements AppendableArgument, ArgumentValidator {
    private static final String COMMAND_FLAG_CONFIG = "--config";
    private String configFilePath;

    private ConfigFileArgument(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public static ConfigFileArgument of(String configFilePath) {
        return new ConfigFileArgument(configFilePath);
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    @Override
    public void appendToArgumentList(ArgumentListBuilder argumentListBuilder) {
        if (StringUtils.isNotBlank(configFilePath)) {
            argumentListBuilder.add(COMMAND_FLAG_CONFIG);
            argumentListBuilder.add(configFilePath.trim());
        }
    }

    @Override
    public ValidationResult validateArgument(SigmaBuildContext buildContext, FilePath workingDirectory) {
        boolean empty = StringUtils.isBlank(getConfigFilePath());
        if (empty) {
            return ValidationResult.error(COMMAND_FLAG_CONFIG, getConfigFilePath(), "File path cannot be empty");
        }
        return ValidationResult.success(COMMAND_FLAG_CONFIG, getConfigFilePath());
    }
}
