package com.synopsys.integration.jenkins.sigma.common;

import java.util.Optional;

import javax.annotation.Nonnull;

public class ValidationResult {
    private final String name;
    private final String value;
    private final String errorMessage;

    private ValidationResult(String name, String value, String errorMessage) {
        this.name = name;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult success(@Nonnull String name, @Nonnull String value) {
        return new ValidationResult(name, value, null);
    }

    public static ValidationResult error(@Nonnull String name, @Nonnull String value, @Nonnull String errorMessage) {
        return new ValidationResult(name, value, errorMessage);
    }

    public boolean isError() {
        return getErrorMessage().isPresent();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
