package com.synopsys.integration.jenkins.sigma.extension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.jenkins.sigma.SigmaBuildContext;
import com.synopsys.integration.jenkins.sigma.argument.AppendableArgument;
import com.synopsys.integration.jenkins.sigma.argument.PolicyFileArgument;
import com.synopsys.integration.jenkins.sigma.argument.WorkingDirectoryArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.AnalyzeArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.FormatArgument;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.util.ArgumentListBuilder;

public class CommandLineArgumentBuilder {
    private SigmaBuildContext sigmaBuildContext;
    private UserProvidedArgumentData userProvidedArgumentData;

    public CommandLineArgumentBuilder(SigmaBuildContext sigmaBuildContext, UserProvidedArgumentData userProvidedArgumentData) {
        this.sigmaBuildContext = sigmaBuildContext;
        this.userProvidedArgumentData = userProvidedArgumentData;
    }

    public ArgumentListBuilder buildArgumentList() throws IOException, InterruptedException {
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        // creat argument objects from user input
        // validate the input and throw abort exception if invalid.
        addSigmaExecutableToCommand(argumentListBuilder);
        addCommandLineArguments(argumentListBuilder, userProvidedArgumentData.getConfigFileEntries());
        Optional<String> policyFilePath = userProvidedArgumentData.getPolicyFilePath();
        if (policyFilePath.isPresent()) {
            addCommandLineArguments(argumentListBuilder, PolicyFileArgument.of(policyFilePath.get()));
        }
        addCommandLineArguments(argumentListBuilder, WorkingDirectoryArgument.of());
        addCommandLineArguments(argumentListBuilder, AnalyzeArgument.of());
        addCommandLineArguments(argumentListBuilder, FormatArgument.of());
        addCommandLineArguments(argumentListBuilder, userProvidedArgumentData.getAdditionalAnalyzeArguments());
        addCommandLineArguments(argumentListBuilder, userProvidedArgumentData.getAnalyzeDirectories());

        return argumentListBuilder;
    }

    private <T extends AppendableArgument> ArgumentListBuilder addCommandLineArguments(ArgumentListBuilder argumentListBuilder, T argumentItem) {
        return addCommandLineArguments(argumentListBuilder, Collections.singletonList(argumentItem));
    }

    private <T extends AppendableArgument> ArgumentListBuilder addCommandLineArguments(ArgumentListBuilder argumentListBuilder, List<T> argumentListItems) {
        if (argumentListItems != null) {
            for (AppendableArgument appendableArgument : argumentListItems) {
                appendableArgument.appendToArgumentList(argumentListBuilder);
            }
        }
        return argumentListBuilder;
    }

    private void addSigmaExecutableToCommand(ArgumentListBuilder commandLineBuilder) throws IOException, InterruptedException {
        Optional<SigmaToolInstallation> sigmaTool = userProvidedArgumentData.getSigmaToolInstallation();
        Launcher launcher = sigmaBuildContext.getLauncher();
        if (sigmaTool.isPresent()) {
            Optional<Node> nodeOptional = sigmaBuildContext.getNode();
            if (nodeOptional.isPresent()) {
                Node node = nodeOptional.get();
                BuildListener listener = sigmaBuildContext.getListener();
                EnvVars environment = sigmaBuildContext.getEnvironment();
                SigmaToolInstallation installation = sigmaTool.get().forNode(node, listener);
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
