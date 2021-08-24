package com.synopsys.integration.jenkins.sigma;

import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.jenkins.sigma.extension.tool.SigmaToolInstallation;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.TaskListener;

public class SigmaBuildContext {
    private final Launcher launcher;
    private final TaskListener listener;
    private final EnvVars environment;
    private final SigmaToolInstallation sigmaToolInstallation;

    public SigmaBuildContext(Launcher launcher, TaskListener listener, EnvVars environment, @Nullable SigmaToolInstallation sigmaToolInstallation) {
        this.launcher = launcher;
        this.listener = listener;
        this.environment = environment;
        this.sigmaToolInstallation = sigmaToolInstallation;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public TaskListener getListener() {
        return listener;
    }

    public EnvVars getEnvironment() {
        return environment;
    }

    public Optional<SigmaToolInstallation> getSigmaToolInstallation() {
        return Optional.ofNullable(sigmaToolInstallation);
    }
}
