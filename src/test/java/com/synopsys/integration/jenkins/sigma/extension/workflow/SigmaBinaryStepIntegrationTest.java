package com.synopsys.integration.jenkins.sigma.extension.workflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SingleFileSCM;

import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;
import com.synopsys.integration.jenkins.sigma.utils.SigmaTestUtil;

import hudson.model.FreeStyleProject;

public class SigmaBinaryStepIntegrationTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    public SigmaTestUtil sigmaTestUtil = new SigmaTestUtil();

    @Before
    public void loadProps() throws IOException {
        sigmaTestUtil.loadProperties();
    }

    @Test
    public void testSimpleBuildStepSucceeds() throws Exception {
        sigmaTestUtil.addInstallation(() -> jenkinsRule.jenkins.getDescriptorByType(SigmaToolInstallation.DescriptorImpl.class));
        FreeStyleProject project = jenkinsRule.createFreeStyleProject("Test Project");
        project.setScm(new SingleFileSCM("JenkinsSigmaTestClass.java", "public class JenkinsSigmaTestClass {}"));
        SigmaBinaryStep step = new SigmaBinaryStep();
        step.setSigmaToolName(SigmaTestUtil.TEST_TOOL_NAME);
        assertNotNull(step.getDescriptor().getInstallations());
        project.getBuildersList().add(step);
        assertTrue(step.getDescriptor().isApplicable(FreeStyleProject.class));
        jenkinsRule.buildAndAssertSuccess(project);

    }
}
