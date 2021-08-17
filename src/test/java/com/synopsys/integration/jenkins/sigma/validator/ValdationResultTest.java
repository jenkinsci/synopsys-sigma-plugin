package com.synopsys.integration.jenkins.sigma.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValdationResultTest {

    @Test
    public void testSuccessWithName() {
        String name = "FieldName";
        ValidationResult result = ValidationResult.success(name);
        assertEquals(name, result.getName());
        assertFalse(result.isError());
        assertFalse(result.getValue().isPresent());
        assertFalse(result.hasValue());
        assertFalse(result.getErrorMessage().isPresent());
    }

    @Test
    public void testSuccessNameValue() {
        String name = "FieldName";
        String value = "FieldValue";
        ValidationResult result = ValidationResult.success(name, value);
        assertEquals(name, result.getName());
        assertTrue(result.getValue().isPresent());
        assertTrue(result.hasValue());
        assertEquals(value, result.getValue().get());
        assertFalse(result.isError());
        assertFalse(result.getErrorMessage().isPresent());
    }

    @Test
    public void testErrorWithName() {
        String name = "FieldName";
        String errorMessage = "Error Message!";
        ValidationResult result = ValidationResult.error(name, errorMessage);
        assertEquals(name, result.getName());
        assertFalse(result.getValue().isPresent());
        assertFalse(result.hasValue());
        assertTrue(result.isError());
        assertTrue(result.getErrorMessage().isPresent());
        assertEquals(errorMessage, result.getErrorMessage().get());
    }

    @Test
    public void testErrorWithNameValue() {
        String name = "FieldName";
        String value = "FieldValue";
        String errorMessage = "Error Message!";
        ValidationResult result = ValidationResult.error(name, value, errorMessage);
        assertEquals(name, result.getName());
        assertTrue(result.getValue().isPresent());
        assertTrue(result.hasValue());
        assertEquals(value, result.getValue().get());
        assertTrue(result.isError());
        assertTrue(result.getErrorMessage().isPresent());
        assertEquals(errorMessage, result.getErrorMessage().get());
    }
}
