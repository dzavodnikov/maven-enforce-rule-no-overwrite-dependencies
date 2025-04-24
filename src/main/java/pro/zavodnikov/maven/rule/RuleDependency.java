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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Container for dependencies.
 */
public class RuleDependency {

    private final String groupId;

    private final String artifactId;

    private final String classifier;

    private final String type;

    private final String version;

    private final String scope;

    public RuleDependency(
            final String groupId,
            final String artifactId,
            final String classifier,
            final String type,
            final String version,
            final String scope) {
        this.groupId = Check.notNull(groupId, "GroupId should not be null");
        this.artifactId = Check.notNull(artifactId, "ArtifactId should not be null");
        this.classifier = Check.defaultValue(classifier, "");
        this.type = Check.defaultValue(type, "");
        this.version = Check.defaultValue(version, "");

        this.scope = Check.defaultValue(scope, "compile");
    }

    public RuleDependency(final org.apache.maven.model.Dependency dependency) {
        this(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getClassifier(),
                dependency.getType(),
                dependency.getVersion(),
                dependency.getScope());
    }

    public RuleDependency(final org.eclipse.aether.artifact.Artifact artifact, final String scope) {
        this(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier(),
                artifact.getExtension(),
                artifact.getVersion(),
                scope);
    }

    public RuleDependency(final org.eclipse.aether.graph.Dependency dependency) {
        this(dependency.getArtifact(), dependency.getScope());
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public String getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }

    public String getScope() {
        return this.scope;
    }

    public org.eclipse.aether.artifact.Artifact getGraphArtifact() {
        return new org.eclipse.aether.artifact.DefaultArtifact(
                getGroupId(),
                getArtifactId(),
                getClassifier(),
                getType(),
                getVersion());
    }

    public org.eclipse.aether.graph.Dependency getGraphDependency() {
        return new org.eclipse.aether.graph.Dependency(getGraphArtifact(), getScope());
    }

    private String[] getFields() {
        return new String[] {
                getGroupId(),
                getArtifactId(),
                getClassifier(),
                getType(),
                getVersion(),
                getScope(),
        };
    }

    @Override
    public String toString() {
        return String.join(":", getFields());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFields());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof RuleDependency) {
            final RuleDependency other = (RuleDependency) obj;
            final String[] localFields = getFields();
            final String[] otherFields = other.getFields();
            for (int i = 0; i < localFields.length; ++i) {
                if (!Objects.equals(localFields[i], otherFields[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static List<RuleDependency> convert(final Collection<org.apache.maven.model.Dependency> collection) {
        return collection.stream().map(RuleDependency::new).collect(Collectors.toList());
    }
}
