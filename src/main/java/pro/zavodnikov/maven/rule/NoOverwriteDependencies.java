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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;

/**
 * No Overwrite Dependencies rule.
 */
@Named("noOverwriteDependencies")
public class NoOverwriteDependencies extends AbstractEnforcerRule {

    /**
     * Simple param. This rule fails if the value is true.
     */
    private boolean shouldIfail = false;

    /**
     * Rule parameter as list of items.
     */
    private List<String> listParameters;

    // Inject needed Maven components

    @Inject
    private MavenProject project;

    @Inject
    private MavenSession session;

    @Inject
    private RuntimeInformation runtimeInformation;

    @Override
    public void execute() throws EnforcerRuleException {
        getLog().info("Retrieved Target Folder: " + this.project.getBuild().getDirectory());
        getLog().info("Retrieved ArtifactId: " + this.project.getArtifactId());
        getLog().info("Retrieved Project: " + this.project);
        getLog().info("Retrieved Maven version: " + this.runtimeInformation.getMavenVersion());
        getLog().info("Retrieved Session: " + this.session);
        getLog().warnOrError("Parameter shouldIfail: " + this.shouldIfail);
        getLog().info(() -> "Parameter listParameters: " + this.listParameters);

        if (this.shouldIfail) {
            throw new EnforcerRuleException("Failing because my param said so.");
        }
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or
     * conditions change that would cause the result to be different. Multiple
     * cached results are stored based on their id.
     *
     * The easiest way to do this is to return a hash computed from the values of
     * your parameters.
     *
     * If your rule is not cacheable, then you don't need to override this method or
     * return null.
     */
    @Override
    public String getCacheId() {
        // No hash on boolean...only parameter so no hash is needed.
        return Boolean.toString(this.shouldIfail);
    }

    /**
     * A good practice is provided toString method for Enforcer Rule.
     *
     * Output is used in verbose Maven logs, can help during investigate problems.
     *
     * @return rule description
     */
    @Override
    public String toString() {
        return String.format("NoOverwriteDependencies[shouldIfail=%b]", this.shouldIfail);
    }
}
