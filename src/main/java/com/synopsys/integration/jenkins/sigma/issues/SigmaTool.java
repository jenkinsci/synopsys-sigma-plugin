package com.synopsys.integration.jenkins.sigma.issues;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import com.synopsys.integration.jenkins.sigma.Messages;

import edu.hm.hafner.analysis.parser.JsonParser;
import hudson.Extension;
import io.jenkins.plugins.analysis.core.model.ReportScanningTool;

public class SigmaTool extends ReportScanningTool {
    private static final String TOOL_ID = "synopsys-sigma-issues-tool";
    private static final String DEFAULT_FILE_PATTERN = "**/sigma-results.json";

    @DataBoundConstructor
    public SigmaTool() {
        super();
    }

    @Override
    public JsonParser createParser() {
        return new JsonParser();
    }

    /**
     * Descriptor for this static analysis tool.
     */
    @Extension
    public static class DescriptorImpl extends ReportScanningToolDescriptor {
        public DescriptorImpl() {
            super(TOOL_ID);
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.issues_reporting_tool_displayName();
        }

        @Override
        public String getPattern() {
            return DEFAULT_FILE_PATTERN;
        }
    }
}
