package com.synopsys.integration.jenkins.sigma.validator;

import java.util.Optional;

import javax.annotation.Nullable;

public class ValidationResult {
    private final String name;
    private final String value;
    private final String errorMessage;

    private ValidationResult(String name, String value, String errorMessage) {
        this.name = name;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult success(String name, @Nullable String value) {
        return new ValidationResult(name, value, null);
    }

    public static ValidationResult success(String name) {
        return new ValidationResult(name, null, null);
    }

    public static ValidationResult error(String name, @Nullable String value, @Nullable String errorMessage) {
        return new ValidationResult(name, value, errorMessage);
    }

    public static ValidationResult error(String name, @Nullable String errorMessage) {
        return new ValidationResult(name, null, errorMessage);
    }

    public boolean isError() {
        return getErrorMessage().isPresent();
    }

    public boolean hasValue() {
        return getValue().isPresent();
    }

    public String getName() {
        return name;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
