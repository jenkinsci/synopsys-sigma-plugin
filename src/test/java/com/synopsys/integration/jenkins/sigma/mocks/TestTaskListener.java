package com.synopsys.integration.jenkins.sigma.mocks;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.console.ConsoleNote;
import hudson.model.TaskListener;
import hudson.remoting.Channel;

public class TestTaskListener implements TaskListener {

    @NonNull
    @Override
    public PrintStream getLogger() {
        return null;
    }

    @NonNull
    @Override
    public Charset getCharset() {
        return TaskListener.super.getCharset();
    }

    @Override
    public PrintWriter _error(final String prefix, final String msg) {
        return TaskListener.super._error(prefix, msg);
    }

    @Override
    public void annotate(final ConsoleNote ann) throws IOException {
        TaskListener.super.annotate(ann);
    }

    @Override
    public void hyperlink(final String url, final String text) throws IOException {
        TaskListener.super.hyperlink(url, text);
    }

    @NonNull
    @Override
    public PrintWriter error(final String msg) {
        return TaskListener.super.error(msg);
    }

    @NonNull
    @Override
    public PrintWriter error(final String format, final Object... args) {
        return TaskListener.super.error(format, args);
    }

    @NonNull
    @Override
    public PrintWriter fatalError(final String msg) {
        return TaskListener.super.fatalError(msg);
    }

    @NonNull
    @Override
    public PrintWriter fatalError(final String format, final Object... args) {
        return TaskListener.super.fatalError(format, args);
    }

    @Nonnull
    @Override
    public Channel getChannelForSerialization() throws NotSerializableException {
        return TaskListener.super.getChannelForSerialization();
    }
}
