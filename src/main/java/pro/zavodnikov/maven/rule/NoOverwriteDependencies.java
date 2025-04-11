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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

/**
 * No Overwrite Dependencies rule.
 */
@Named("noOverwriteDependencies")
public class NoOverwriteDependencies extends AbstractEnforcerRule {

    @Inject
    private MavenProject project;

    /**
     * Find dependency from the list that have same Group ID and Artifact ID, but
     * different Version.
     *
     * @param dependencies list of dependencies for search.
     * @param target       dependency that provide Group ID, Artifact ID and
     *                     Version.
     * @return dependency with different version of <code>null</code>.
     */
    public static Dependency find(final List<Dependency> dependencies, final Dependency target) {
        for (Dependency dep : dependencies) {
            if (Objects.equals(dep.getGroupId(), target.getGroupId())
                    && Objects.equals(dep.getArtifactId(), target.getArtifactId())
                    && !Objects.equals(dep.getVersion(), target.getVersion())) {
                return dep;
            }
        }
        return null;
    }

    /**
     * Verify that no any dependency from first list do not overwrite any dependency
     * from the second list.
     */
    public static void verifyDependencies(final List<Dependency> dependencies,
            final List<Dependency> dependenciesNoOverwrite) throws EnforcerRuleException {
        final List<String> depVersionsOverwritten = new ArrayList<>();
        for (Dependency dep : dependencies) {
            final Dependency differentVersion = find(dependenciesNoOverwrite, dep);
            if (differentVersion != null) {
                depVersionsOverwritten.add(String.format("%s:%s:%s override by version %s",
                        differentVersion.getGroupId(),
                        differentVersion.getArtifactId(),
                        differentVersion.getVersion(),
                        dep.getVersion()));
            }
        }
        if (!depVersionsOverwritten.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Following dependencies try to overwrite dependencies from parent POM:");
            sb.append("\n");
            for (String line : depVersionsOverwritten) {
                sb.append(" - ");
                sb.append(line);
                sb.append("\n");
            }
            throw new EnforcerRuleException(sb.toString());
        }
    }

    @Override
    public void execute() throws EnforcerRuleException {
        final List<Dependency> dependenciesNoOverwrite = this.project.getDependencyManagement().getDependencies();
        final List<Dependency> dependencies = this.project.getDependencies();
        verifyDependencies(dependencies, dependenciesNoOverwrite);
    }
}
