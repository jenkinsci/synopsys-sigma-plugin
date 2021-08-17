package com.synopsys.integration.jenkins.sigma.mocks;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.console.ConsoleNote;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.remoting.Channel;

public class TestBuildListener implements BuildListener {
    @NonNull
    @Override
    public PrintStream getLogger() {
        return null;
    }

    @Override
    public void started(final List<Cause> causes) {
        BuildListener.super.started(causes);
    }

    @Override
    public void finished(final Result result) {
        BuildListener.super.finished(result);
    }

    @NonNull
    @Override
    public Charset getCharset() {
        return BuildListener.super.getCharset();
    }

    @Override
    public PrintWriter _error(final String prefix, final String msg) {
        return BuildListener.super._error(prefix, msg);
    }

    @Override
    public void annotate(final ConsoleNote ann) throws IOException {
        BuildListener.super.annotate(ann);
    }

    @Override
    public void hyperlink(final String url, final String text) throws IOException {
        BuildListener.super.hyperlink(url, text);
    }

    @NonNull
    @Override
    public PrintWriter error(final String msg) {
        return BuildListener.super.error(msg);
    }

    @NonNull
    @Override
    public PrintWriter error(final String format, final Object... args) {
        return BuildListener.super.error(format, args);
    }

    @NonNull
    @Override
    public PrintWriter fatalError(final String msg) {
        return BuildListener.super.fatalError(msg);
    }

    @NonNull
    @Override
    public PrintWriter fatalError(final String format, final Object... args) {
        return BuildListener.super.fatalError(format, args);
    }

    @Nonnull
    @Override
    public Channel getChannelForSerialization() throws NotSerializableException {
        return BuildListener.super.getChannelForSerialization();
    }
}
