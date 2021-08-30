/*
 * Copyright (c) 2021 Synopsys, Inc. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.sigma.extension.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.synopsys.integration.jenkins.sigma.Messages;

public class SigmaToolTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testSigmaTool() {
        SigmaTool sigmaTool = new SigmaTool();
        assertNotNull(sigmaTool.createParser());
        assertNotNull(sigmaTool.getDescriptor());
    }

    @Test
    public void testSigmaToolDescriptor() {
        SigmaTool.DescriptorImpl sigmaTool = jenkinsRule.jenkins.getDescriptorByType(SigmaTool.DescriptorImpl.class);
        assertEquals(Messages.issues_reporting_tool_displayName(), sigmaTool.getDisplayName());
        assertEquals(SigmaTool.TOOL_ID, sigmaTool.getId());
        assertEquals(SigmaTool.DEFAULT_FILE_PATTERN, sigmaTool.getPattern());
    }
}
