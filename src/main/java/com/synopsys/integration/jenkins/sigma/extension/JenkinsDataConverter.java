package com.synopsys.integration.jenkins.sigma.extension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.synopsys.integration.jenkins.sigma.argument.ConfigFileArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.DirectoryArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.FlagArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.NameValueArgument;
import com.synopsys.integration.jenkins.sigma.argument.analyze.NamedAnalyzeArgument;
import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;
import com.synopsys.integration.jenkins.sigma.extension.workflow.ConfigFileEntry;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeArgumentEntry;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeArgumentType;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeDirectoryEntry;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeFlagArgumentEntry;
import com.synopsys.integration.jenkins.sigma.extension.workflow.analyze.AnalyzeNameValueArgumentEntry;

public class JenkinsDataConverter {
    private final SigmaToolInstallation sigmaToolInstallation;
    private final String policyFilePath;
    private final List<ConfigFileEntry> configFileEntries;

    private final List<AnalyzeArgumentEntry> additionalAnalyzeArguments;
    private final List<AnalyzeDirectoryEntry> analyzeDirectories;

    public JenkinsDataConverter(@Nullable SigmaToolInstallation sigmaToolInstallation, @Nullable String policyFilePath, @Nullable List<ConfigFileEntry> configFileEntries,
        @Nullable List<AnalyzeArgumentEntry> additionalAnalyzeArguments, @Nullable List<AnalyzeDirectoryEntry> analyzeDirectories) {
        this.sigmaToolInstallation = sigmaToolInstallation;
        this.policyFilePath = policyFilePath;
        this.configFileEntries = configFileEntries;
        this.additionalAnalyzeArguments = additionalAnalyzeArguments;
        this.analyzeDirectories = analyzeDirectories;
    }

    public UserProvidedArgumentData convertData() {
        List<ConfigFileArgument> configFileArguments = convertConfigFileArguments(configFileEntries);
        List<DirectoryArgument> directoryArguments = convertDirectoryArguments(analyzeDirectories);
        List<NamedAnalyzeArgument> namedAnalyzeArguments = convertAdditionalAnalyzeArguments(additionalAnalyzeArguments);
        return new UserProvidedArgumentData(sigmaToolInstallation, policyFilePath, configFileArguments, namedAnalyzeArguments, directoryArguments);
    }

    private List<ConfigFileArgument> convertConfigFileArguments(List<ConfigFileEntry> configFileEntries) {
        if (configFileEntries == null) {
            return Collections.emptyList();
        }
        return configFileEntries.stream()
            .map(ConfigFileEntry::getConfigFilePath)
            .map(ConfigFileArgument::of)
            .collect(Collectors.toList());
    }

    private List<DirectoryArgument> convertDirectoryArguments(List<AnalyzeDirectoryEntry> analyzeDirectoryEntries) {
        if (analyzeDirectoryEntries == null) {
            return Collections.emptyList();
        }
        return analyzeDirectoryEntries.stream()
            .map(AnalyzeDirectoryEntry::getSubDirectoryPath)
            .map(DirectoryArgument::of)
            .collect(Collectors.toList());
    }

    private List<NamedAnalyzeArgument> convertAdditionalAnalyzeArguments(List<AnalyzeArgumentEntry> analyzeArgumentEntries) {
        if (analyzeArgumentEntries == null) {
            return Collections.emptyList();
        }
        List<NamedAnalyzeArgument> namedAnalyzeArguments = new LinkedList<>();
        for (AnalyzeArgumentEntry entry : analyzeArgumentEntries) {
            AnalyzeArgumentType type = entry.getType();
            if (AnalyzeArgumentType.FLAG == type) {
                namedAnalyzeArguments.add(convertFlagArgument((AnalyzeFlagArgumentEntry) entry));
            } else if (AnalyzeArgumentType.NAME_VALUE_PAIR == type) {
                namedAnalyzeArguments.add(convertNameValueArgument((AnalyzeNameValueArgumentEntry) entry));
            }
        }
        return namedAnalyzeArguments;
    }

    private FlagArgument convertFlagArgument(AnalyzeFlagArgumentEntry argumentEntry) {
        return FlagArgument.of(argumentEntry.getName());
    }

    private NameValueArgument convertNameValueArgument(AnalyzeNameValueArgumentEntry argumentEntry) {
        return NameValueArgument.of(argumentEntry.getName(), argumentEntry.getValue());
    }
}
