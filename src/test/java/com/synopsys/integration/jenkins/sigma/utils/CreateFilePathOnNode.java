package com.synopsys.integration.jenkins.sigma.utils;

import java.io.File;
import java.io.IOException;

import jenkins.security.MasterToSlaveCallable;

public class CreateFilePathOnNode extends MasterToSlaveCallable<String, IOException> {
    private final String path;

    public CreateFilePathOnNode(String path) {
        this.path = path;
    }

    @Override
    public String call() throws IOException {
        File file = new File(path);
        file.mkdirs();
        file.createNewFile();

        return file.getPath();
    }
}

