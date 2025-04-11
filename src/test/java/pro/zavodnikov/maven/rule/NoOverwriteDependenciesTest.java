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

import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.internal.DefaultDependencyGraphBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests for {@link NoOverwriteDependencies}.
 */
public class NoOverwriteDependenciesTest {

    private Dependency createDependency(final String groupId, final String artifactId, final String version) {
        final Dependency newDep = new Dependency();
        newDep.setGroupId(groupId);
        newDep.setArtifactId(artifactId);
        newDep.setVersion(version);

        return newDep;
    }

    private org.apache.maven.artifact.Artifact createArtifact(final String groupId, final String artifactId,
            final String version) {
        return new org.apache.maven.artifact.DefaultArtifact(
                groupId, artifactId, version,
                "compile", "jar", null, new DefaultArtifactHandler("jar"));
    }

    private MavenProject createProject(final List<Dependency> deps, final List<Dependency> depManagement) {
        final MavenProject project = new MavenProject();
        project.setDependencies(deps);

        final org.apache.maven.artifact.Artifact art = createArtifact("com.example", "Stuff", "1.0.0");
        art.setDependencyTrail(null);
        project.setArtifact(art);
        final DependencyManagement depMan = new DependencyManagement();
        depMan.setDependencies(depManagement);
        project.getModel().setDependencyManagement(depMan);

        return project;
    }

    private NoOverwriteDependencies createRule(final MavenProject project)
            throws EnforcerRuleException {
        final PlexusContainer container = null;
        final RepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        final MavenExecutionResult result = null;
        @SuppressWarnings("deprecation")
        final MavenSession session = new MavenSession(container, repositorySession, request, result);

        final org.apache.maven.artifact.Artifact projDefaultArt = project.getArtifact();
        final org.eclipse.aether.artifact.Artifact defaultArt = new org.eclipse.aether.artifact.DefaultArtifact(
                projDefaultArt.getGroupId(), projDefaultArt.getArtifactId(), null, projDefaultArt.getVersion());
        final DefaultDependencyNode depNode = new DefaultDependencyNode(defaultArt);
        final DependencyResolutionResult depResResult = new MockDependencyResolutionResult(depNode);

        final ProjectDependenciesResolver resolver = new MockProjectDependenciesResolver(depResResult);
        final DependencyGraphBuilder depGraph = new DefaultDependencyGraphBuilder(resolver);

        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MavenProject.class).toInstance(project);
                bind(MavenSession.class).toInstance(session);
                bind(DependencyGraphBuilder.class).toInstance(depGraph);
                bind(Logger.class).toInstance(new ConsoleLogger());
            }
        }).getInstance(NoOverwriteDependencies.class);
    }

    @Test
    public void testNoOverwriteCorrect() throws EnforcerRuleException {
        final Dependency stuff = createDependency("org.company", "Stuff", "1.0.0");
        final Dependency log11 = createDependency("org.company", "Log", "1.1.0");

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
    @Disabled
    public void testNoOverwriteWrong() throws EnforcerRuleException {
        final Dependency stuff = createDependency("org.company", "Stuff", "1.0.0");
        final Dependency log11 = createDependency("org.company", "Log", "1.1.0");
        final Dependency log12 = createDependency("org.company", "Log", "1.2.0");

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
