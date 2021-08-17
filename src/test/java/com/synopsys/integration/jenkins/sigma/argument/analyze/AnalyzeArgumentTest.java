package com.synopsys.integration.jenkins.sigma.argument.analyze;

import org.junit.Test;

import com.synopsys.integration.jenkins.sigma.utils.ArgumentListAssertions;

import hudson.util.ArgumentListBuilder;

public class AnalyzeArgumentTest {

    @Test
    public void testArgument() {
        String name = "name";
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        AnalyzeArgument analyzeArgument = AnalyzeArgument.of();
        analyzeArgument.appendToArgumentList(argumentListBuilder);
        ArgumentListAssertions.assertArgumentList(argumentListBuilder, AnalyzeArgument.ARGUMENT_NAME_ANALYZE);
    }
}
