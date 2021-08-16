package com.synopsys.integration.jenkins.sigma.extension;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.jenkins.sigma.argument.ConfigFileArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.DirectoryArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.NamedAnalyzeArgument;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

//TODO come up with a better name.
public class UserProvidedArgumentData {
    private final SigmaToolInstallation sigmaToolInstallation;
    private final String policyFilePath;
    private final List<ConfigFileArgument> configFileEntries;
    private final List<NamedAnalyzeArgument> additionalAnalyzeArguments;
    private final List<DirectoryArgument> analyzeDirectories;

    public UserProvidedArgumentData(@Nullable SigmaToolInstallation sigmaToolInstallation, @Nullable String policyFilePath, List<ConfigFileArgument> configFileEntries,
        List<NamedAnalyzeArgument> additionalAnalyzeArguments, List<DirectoryArgument> analyzeDirectories) {
        this.sigmaToolInstallation = sigmaToolInstallation;
        this.policyFilePath = policyFilePath;
        this.configFileEntries = configFileEntries;
        this.additionalAnalyzeArguments = additionalAnalyzeArguments;
        this.analyzeDirectories = analyzeDirectories;
    }

    public Optional<SigmaToolInstallation> getSigmaToolInstallation() {
        return Optional.ofNullable(sigmaToolInstallation);
    }

    public List<NamedAnalyzeArgument> getAdditionalAnalyzeArguments() {
        return additionalAnalyzeArguments;
    }

    public List<ConfigFileArgument> getConfigFileEntries() {
        return configFileEntries;
    }

    public Optional<String> getPolicyFilePath() {
        return Optional.ofNullable(policyFilePath);
    }

    public List<DirectoryArgument> getAnalyzeDirectories() {
        return analyzeDirectories;
    }

    public static class Builder {

    }
}
