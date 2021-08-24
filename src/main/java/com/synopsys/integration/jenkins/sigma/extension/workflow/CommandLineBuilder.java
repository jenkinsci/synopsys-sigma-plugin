package com.synopsys.integration.jenkins.sigma.extension.workflow;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.util.ArgumentListBuilder;

public class CommandLineBuilder {
    public static final String DEFAULT_COMMAND_LINE = "analyze --format jenkins";
    public static final String COMMAND_TOKEN_IGNORE_POLICIES = "--ignore-policies";
    private static final String COMMAND_TOKEN_ANALYZE = "analyze";
    private SigmaBuildContext sigmaBuildContext;
    private SigmaToolInstallation sigmaToolInstallation;
    private boolean ignorePolicies;
    private String commandLineOverride;

    public CommandLineBuilder(SigmaBuildContext sigmaBuildContext, @Nullable SigmaToolInstallation sigmaToolInstallation, boolean ignorePolicies, @Nullable String commandLineOverride) {
        this.sigmaBuildContext = sigmaBuildContext;
        this.sigmaToolInstallation = sigmaToolInstallation;
        this.ignorePolicies = ignorePolicies;
        this.commandLineOverride = commandLineOverride;
    }

    public ArgumentListBuilder buildArgumentList() throws IOException, InterruptedException {
        String currentCommandLine = DEFAULT_COMMAND_LINE;
        PrintStream logger = sigmaBuildContext.getListener().getLogger();
        if (StringUtils.isBlank(commandLineOverride)) {
            logger.println("Using default command line.");
        } else {
            logger.println("Using command line defined in the build step.");
            currentCommandLine = commandLineOverride;
        }
        currentCommandLine = handleIgnorePoliciesOption(logger, currentCommandLine);
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        addSigmaExecutableToCommand(sigmaBuildContext, argumentListBuilder);
        argumentListBuilder.addTokenized(currentCommandLine);
        return argumentListBuilder;
    }

    private String handleIgnorePoliciesOption(PrintStream logger, String currentCommandLine) {
        String updatedCommandLine = currentCommandLine;

        if (ignorePolicies && !currentCommandLine.contains(COMMAND_TOKEN_IGNORE_POLICIES)) {
            int analyzeIndex = currentCommandLine.indexOf(COMMAND_TOKEN_ANALYZE);
            if (analyzeIndex >= 0) {
                logger.println(String.format("Adding %s to command line", COMMAND_TOKEN_IGNORE_POLICIES));
                int insertIndex = analyzeIndex + COMMAND_TOKEN_ANALYZE.length() + 1;
                StringBuilder commandBuilder = new StringBuilder(currentCommandLine);
                // add the 'ignore-policies' command argument with space before and after it.
                commandBuilder.insert(insertIndex, String.format(" %s ", COMMAND_TOKEN_IGNORE_POLICIES));
                updatedCommandLine = commandBuilder.toString();
            } else {
                logger.println(String.format("The analyze sub-command was not found.  Cannot add the %s command line option", COMMAND_TOKEN_IGNORE_POLICIES));
            }
        }

        return updatedCommandLine;
    }

    private void addSigmaExecutableToCommand(SigmaBuildContext sigmaBuildContext, ArgumentListBuilder commandLineBuilder) throws IOException, InterruptedException {
        Launcher launcher = sigmaBuildContext.getLauncher();
        if (sigmaToolInstallation != null) {
            Optional<Node> nodeOptional = sigmaBuildContext.getNode();
            if (nodeOptional.isPresent()) {
                Node node = nodeOptional.get();
                BuildListener listener = sigmaBuildContext.getListener();
                EnvVars environment = sigmaBuildContext.getEnvironment();
                SigmaToolInstallation installation = sigmaToolInstallation.forNode(node, listener);
                installation = installation.forEnvironment(environment);
                sigmaBuildContext.getListener().getLogger().println(String.format("Sigma tool installation found. %s", installation.getHome()));
                commandLineBuilder.add(installation.getExecutablePath(launcher));
            }
        } else {
            // sigma tool installation not defined try to run sigma with a simple command.  It will work if sigma is on executable on the path
            if (sigmaBuildContext.getLauncher().isUnix()) {
                commandLineBuilder.add(SigmaToolInstallation.UNIX_SIGMA_COMMAND);
            } else {
                commandLineBuilder.add(SigmaToolInstallation.WINDOWS_SIGMA_COMMAND);
            }
        }
    }
}
