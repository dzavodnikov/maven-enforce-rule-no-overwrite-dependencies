/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Dmitry Zavodnikov
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

import java.util.List;

import org.apache.maven.project.DependencyResolutionResult;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

public class MockDependencyResolutionResult implements DependencyResolutionResult {

    private final DependencyNode depNode;

    public MockDependencyResolutionResult(final DependencyNode depNode) {
        this.depNode = depNode;
    }

    @Override
    public DependencyNode getDependencyGraph() {
        return this.depNode;
    }

    @Override
    public List<Dependency> getDependencies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDependencies'");
    }

    @Override
    public List<Dependency> getResolvedDependencies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResolvedDependencies'");
    }

    @Override
    public List<Dependency> getUnresolvedDependencies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUnresolvedDependencies'");
    }

    @Override
    public List<Exception> getCollectionErrors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCollectionErrors'");
    }

    @Override
    public List<Exception> getResolutionErrors(Dependency dependency) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResolutionErrors'");
    }
}
