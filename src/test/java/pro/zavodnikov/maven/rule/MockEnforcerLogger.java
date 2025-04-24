/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025-2026 Dmitry Zavodnikov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pro.zavodnikov.maven.rule;

import java.util.function.Supplier;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;

/**
 * Mock logger. Useful for testing purposes.
 */
public class MockEnforcerLogger implements EnforcerLogger {

    private final boolean isError;
    private final boolean isWarn;
    private final boolean isInfo;
    private final boolean isDebug;

    public MockEnforcerLogger(final boolean isError, final boolean isWarn, final boolean isInfo,
            final boolean isDebug) {
        this.isError = isError;
        this.isWarn = isWarn;
        this.isInfo = isInfo;
        this.isDebug = isDebug;
    }

    private void print(final CharSequence message) {
        System.out.println(message);
    }

    private void print(final Supplier<CharSequence> messageSupplier) {
        System.out.println(messageSupplier != null ? messageSupplier.get() : null);
    }

    @Override
    public void warnOrError(final CharSequence message) {
        print(message);
    }

    @Override
    public void warnOrError(final Supplier<CharSequence> messageSupplier) {
        print(messageSupplier);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.isDebug;
    }

    @Override
    public void debug(final CharSequence message) {
        print(message);
    }

    @Override
    public void debug(final Supplier<CharSequence> messageSupplier) {
        print(messageSupplier);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isInfo;
    }

    @Override
    public void info(final CharSequence message) {
        print(message);
    }

    @Override
    public void info(final Supplier<CharSequence> messageSupplier) {
        print(messageSupplier);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.isWarn;
    }

    @Override
    public void warn(final CharSequence message) {
        print(message);
    }

    @Override
    public void warn(final Supplier<CharSequence> messageSupplier) {
        print(messageSupplier);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.isError;
    }

    @Override
    public void error(final CharSequence message) {
        print(message);
    }

    @Override
    public void error(final Supplier<CharSequence> messageSupplier) {
        print(messageSupplier);
    }
}
