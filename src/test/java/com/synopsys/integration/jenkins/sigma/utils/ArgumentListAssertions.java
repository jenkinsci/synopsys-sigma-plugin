/*
 * Copyright (c) 2021 Synopsys, Inc. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.sigma.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import hudson.util.ArgumentListBuilder;

public class ArgumentListAssertions {

    public static void assertArgumentList(ArgumentListBuilder actual, String... expected) {
        List<String> expectedArgumentList = Arrays.asList(expected);
        List<String> actualArgumentList = actual.toList();
        assertEquals(expectedArgumentList, actualArgumentList);
    }
}
