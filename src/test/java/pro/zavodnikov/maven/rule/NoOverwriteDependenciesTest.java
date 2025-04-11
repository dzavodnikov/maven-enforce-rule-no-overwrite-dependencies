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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests for {@link NoOverwriteDependencies}.
 */
public class NoOverwriteDependenciesTest {

    private Dependency createDependency(final String artifactId, final String version) {
        final Dependency newDep = new Dependency();
        newDep.setGroupId("com.example");
        newDep.setArtifactId(artifactId);
        newDep.setVersion(version);

        return newDep;
    }

    private MavenProject createProject(final List<Dependency> deps, final List<Dependency> depManagement) {
        final MavenProject project = new MavenProject();
        project.setDependencies(deps);

        final DependencyManagement depMan = new DependencyManagement();
        depMan.setDependencies(depManagement);
        project.getModel().setDependencyManagement(depMan);

        return project;
    }

    private NoOverwriteDependencies createRule(final MavenProject proj) throws EnforcerRuleException {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MavenProject.class).toInstance(proj);
            }
        }).getInstance(NoOverwriteDependencies.class);
    }

    @Test
    public void testNoOverwriteCorrect() throws EnforcerRuleException {
        final Dependency stuff = createDependency("Stuff", "1.0.0");
        final Dependency log11 = createDependency("Log", "1.1.0");

        final List<Dependency> depManagement = new ArrayList<>();
        depManagement.add(stuff);
        depManagement.add(log11);

        final List<Dependency> deps = new ArrayList<>();
        deps.add(stuff);
        deps.add(log11);

        final NoOverwriteDependencies rule = createRule(createProject(deps, depManagement));
        rule.execute();
    }

    @Test
    public void testNoOverwriteWrong() throws EnforcerRuleException {
        final Dependency stuff = createDependency("Stuff", "1.0.0");
        final Dependency log11 = createDependency("Log", "1.1.0");
        final Dependency log12 = createDependency("Log", "1.2.0");

        final List<Dependency> depManagement = new ArrayList<>();
        depManagement.add(stuff);
        depManagement.add(log11);

        final List<Dependency> deps = new ArrayList<>();
        deps.add(stuff);
        deps.add(log12);

        final NoOverwriteDependencies rule = createRule(createProject(deps, depManagement));
        assertThrows(EnforcerRuleException.class, rule::execute);
    }
}
