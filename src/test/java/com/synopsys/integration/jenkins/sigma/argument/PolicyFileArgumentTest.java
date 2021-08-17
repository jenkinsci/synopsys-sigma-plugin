package com.synopsys.integration.jenkins.sigma.argument;

import org.junit.Test;

import com.synopsys.integration.jenkins.sigma.utils.ArgumentListAssertions;

import hudson.util.ArgumentListBuilder;

public class PolicyFileArgumentTest {

    @Test
    public void policyFileAppendTest() {
        String policyFilePath = "policyFilePath.txt";
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        PolicyFileArgument policyFileArgument = PolicyFileArgument.of(policyFilePath);
        policyFileArgument.appendToArgumentList(argumentListBuilder);

        ArgumentListAssertions.assertArgumentList(argumentListBuilder, PolicyFileArgument.ARGUMENT_NAME_POLICY, policyFilePath);
    }
}
