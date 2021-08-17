package com.synopsys.integration.jenkins.sigma.argument.analyze;

import org.junit.Test;

import com.synopsys.integration.jenkins.sigma.utils.ArgumentListAssertions;

import hudson.util.ArgumentListBuilder;

public class FlagArgumentTest {

    @Test
    public void testNameOnly() {
        String name = "name";
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        FlagArgument flagArgument = FlagArgument.of(name);
        flagArgument.appendToArgumentList(argumentListBuilder);
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, String.format("--%s", name));
    }

    @Test
    public void testNameWithDashes() {
        String name = "--name";
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        FlagArgument flagArgument = FlagArgument.of(name);
        flagArgument.appendToArgumentList(argumentListBuilder);
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, name);
    }

    @Test
    public void testNameWithSingleDash() {
        String name = "-name";
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        FlagArgument flagArgument = FlagArgument.of(name);
        flagArgument.appendToArgumentList(argumentListBuilder);
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, name);
    }
}
