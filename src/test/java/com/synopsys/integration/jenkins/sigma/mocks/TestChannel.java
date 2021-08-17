package com.synopsys.integration.jenkins.sigma.mocks;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hudson.remoting.Callable;
import hudson.remoting.Future;
import hudson.remoting.VirtualChannel;

public class TestChannel implements VirtualChannel {
    @Override
    public <V, T extends Throwable> V call(@Nonnull final Callable<V, T> callable) throws IOException, T, InterruptedException {
        return null;
    }

    @Override
    public <V, T extends Throwable> Future<V> callAsync(@Nonnull final Callable<V, T> callable) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void join() throws InterruptedException {

    }

    @Override
    public void join(final long timeout) throws InterruptedException {

    }

    @Nullable
    @Override
    public <T> T export(@Nonnull final Class<T> type, @CheckForNull final T instance) {
        return null;
    }

    @Override
    public void syncLocalIO() throws InterruptedException {

    }
}
