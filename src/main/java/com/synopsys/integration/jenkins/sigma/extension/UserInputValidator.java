package com.synopsys.integration.jenkins.sigma.extension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.argument.PolicyFileArgument;
import com.synopsys.integration.jenkins.sigma.validator.ArgumentValidator;
import com.synopsys.integration.jenkins.sigma.validator.ValidationResult;

import hudson.FilePath;

public class UserInputValidator {
    private SigmaBuildContext sigmaBuildContext;
    private FilePath workingDirectory;
    private UserProvidedArgumentData userProvidedArgumentData;

    public UserInputValidator(SigmaBuildContext sigmaBuildContext, FilePath workingDirectory, UserProvidedArgumentData userProvidedArgumentData) {
        this.sigmaBuildContext = sigmaBuildContext;
        this.workingDirectory = workingDirectory;
        this.userProvidedArgumentData = userProvidedArgumentData;
    }

    public List<ValidationResult> validate() {
        List<ValidationResult> validationResults = new LinkedList<>();
        validationResults.addAll(validateArguments(userProvidedArgumentData.getConfigFileEntries()));
        Optional<String> policyFilePath = userProvidedArgumentData.getPolicyFilePath();
        if (policyFilePath.isPresent()) {
            validationResults.addAll(validateArguments(Collections.singletonList(PolicyFileArgument.of(policyFilePath.get()))));
        }
        validationResults.addAll(validateArguments(userProvidedArgumentData.getAdditionalAnalyzeArguments()));
        validationResults.addAll(validateArguments(userProvidedArgumentData.getAnalyzeDirectories()));

        return validationResults;
    }

    private <T extends ArgumentValidator> List<ValidationResult> validateArguments(List<T> argumentListItems) {
        List<ValidationResult> results = new LinkedList<>();
        if (argumentListItems != null) {
            for (ArgumentValidator argumentValidator : argumentListItems) {
                results.add(argumentValidator.validateArgument(sigmaBuildContext, workingDirectory));
            }
        }
        return results;
    }
}
