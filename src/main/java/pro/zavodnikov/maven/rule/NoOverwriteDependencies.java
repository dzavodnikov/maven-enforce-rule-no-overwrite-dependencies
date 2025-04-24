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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

/**
 * No Overwrite Dependencies rule.
 */
@Named("noOverwriteDependencies")
public class NoOverwriteDependencies extends AbstractEnforcerRule {

    @Inject
    private MavenProject project;

    @Inject
    private MavenSession session;

    @Inject
    private RepositorySystem repositorySystem;

    private void debugPrint(final String message, final Collection<RuleDependency> deps) {
        getLog().debug(message);

        for (RuleDependency d : deps) {
            getLog().debug("- " + d.toString());
        }
    }

    private boolean isSameArtifact(final RuleDependency projDep, final RuleDependency depManDep) {
        return Objects.equals(projDep.getGroupId(), depManDep.getGroupId())
                && Objects.equals(projDep.getArtifactId(), depManDep.getArtifactId());
    }

    private boolean isDifferentVersions(final RuleDependency projDep, final RuleDependency depManDep) {
        if (projDep.getVersion() == null) { // Do not overwrite the version.
            return false;
        }
        return isSameArtifact(projDep, depManDep)
                && !Objects.equals(projDep.getVersion(), depManDep.getVersion());
    }

    private boolean isDifferentScopes(final RuleDependency projDep, final RuleDependency depManDep) {
        if (projDep.getScope() == null) { // Do not overwrite the scope.
            return false;
        }
        return isSameArtifact(projDep, depManDep)
                && !Objects.equals(projDep.getScope(), depManDep.getScope());
    }

    /*
     * See:
     * https://github.com/apache/maven-dependency-plugin/blob/maven-dependency-
     * plugin-3.8.1/src/main/java/org/apache/maven/plugins/dependency/utils/
     * ResolverUtil.java#L76
     */
    private List<RuleDependency> collectDependencies(final RuleDependency root) {
        try {
            final CollectRequest request = new CollectRequest(root.getGraphDependency(),
                    this.session.getCurrentProject().getRemoteProjectRepositories());
            final CollectResult result = this.repositorySystem.collectDependencies(
                    this.session.getRepositorySession(), request);

            final PreorderNodeListGenerator nodeListGenerator = new PreorderNodeListGenerator();
            result.getRoot().accept(nodeListGenerator);
            return nodeListGenerator.getDependencies(true).stream()
                    .map(RuleDependency::new)
                    .filter(d -> !root.equals(d))
                    .collect(Collectors.toList());
        } catch (DependencyCollectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute() throws EnforcerRuleException {
        final DependencyManagement depMan = this.project.getDependencyManagement();
        if (depMan == null) {
            return;
        }

        final List<RuleDependency> projDeps = RuleDependency.convert(this.project.getDependencies());

        final List<RuleDependency> depManDeps = new ArrayList<>();

        final Queue<RuleDependency> toProcess = new ArrayDeque<>(RuleDependency.convert(depMan.getDependencies()));
        while (!toProcess.isEmpty()) {
            final RuleDependency current = toProcess.poll();
            if (depManDeps.contains(current)) {
                continue;
            }

            depManDeps.add(current);

            final List<RuleDependency> transitiveDeps = collectDependencies(current);
            if (getLog().isDebugEnabled()) {
                debugPrint("Transitive dependencies of " + current.toString() + ":", transitiveDeps);
            }
            toProcess.addAll(transitiveDeps);
        }

        if (getLog().isDebugEnabled()) {
            debugPrint("Project dependencies:", projDeps);
            debugPrint("Dependencies Management:", depManDeps);
        }

        final List<String> overrideErrors = new ArrayList<>();
        for (RuleDependency projDep : projDeps) {
            for (RuleDependency depManDep : depManDeps) {
                if (isDifferentVersions(projDep, depManDep)) {
                    final String errorLine = String.format("%s:%s:%s override by version %s",
                            depManDep.getGroupId(), depManDep.getArtifactId(), depManDep.getVersion(),
                            projDep.getVersion());
                    overrideErrors.add(errorLine);
                }

                if (isDifferentScopes(projDep, depManDep)) {
                    final String errorLine = String.format("%s:%s:%s with scope %s override by scope %s",
                            depManDep.getGroupId(), depManDep.getArtifactId(), depManDep.getVersion(),
                            depManDep.getScope(),
                            projDep.getScope());
                    overrideErrors.add(errorLine);
                }
            }
        }

        if (!overrideErrors.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Following dependencies try to overwrite dependencies from parent POM:");
            sb.append("\n");
            for (String line : overrideErrors) {
                sb.append(" - ");
                sb.append(line);
                sb.append("\n");
            }
            throw new EnforcerRuleException(sb.toString());
        }
    }
}
