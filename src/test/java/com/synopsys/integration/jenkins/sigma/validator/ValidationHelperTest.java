package com.synopsys.integration.jenkins.sigma.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationHelperTest {
    @Test
    public void testFormFieldEmpty() {
        assertTrue(ValidationHelper.isFormFieldEmpty(""));
        assertTrue(ValidationHelper.isFormFieldEmpty(null));
        assertTrue(ValidationHelper.isFormFieldEmpty("          "));
        assertTrue(ValidationHelper.isFormFieldEmpty("\t"));
    }

    @Test
    public void testFormFieldPopulated() {
        assertFalse(ValidationHelper.isFormFieldEmpty("For field value"));
    }

    @Test
    public void testNameValid() {
        assertTrue(ValidationHelper.isNameValid("--valid-argument-name"));
        assertTrue(ValidationHelper.isNameValid("-valid-argument-name"));
        assertTrue(ValidationHelper.isNameValid("valid-argument-name"));
    }

    @Test
    public void testNameInvalid() {
        for (String invalidName : ValidationHelper.RESERVED_ARGUMENT_NAMES) {
            String invalidName1 = String.format("--%s", invalidName);
            String invalidName2 = String.format("-%s", invalidName);
            String invalidName3 = String.format("%s", invalidName);
            assertFalse(String.format("Invalid argument name starting with '--': value: %s", invalidName1), ValidationHelper.isNameValid(invalidName1));
            assertFalse(String.format("Invalid argument name starting with '-': value: %s", invalidName2), ValidationHelper.isNameValid(invalidName2));
            assertFalse(String.format("Invalid argument name starting with value: %s", invalidName3), ValidationHelper.isNameValid(invalidName3));
        }
    }
}
