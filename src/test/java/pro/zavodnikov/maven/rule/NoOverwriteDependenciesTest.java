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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.RepositorySystem;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests for {@link NoOverwriteDependencies}.
 */
public class NoOverwriteDependenciesTest extends AbstractRuleTest {

    private void applyRuleTo(final String projectPomName) throws EnforcerRuleException {
        try {
            final MavenProject project = readMavenProject(projectPomName);
            assertNotNull(project);

            final MavenSession session = newMavenSession(project);
            assertNotNull(session);

            final RepositorySystem repositorySystem = lookup(RepositorySystem.class);
            assertNotNull(repositorySystem);

            initTestRepository(session, new File("src/test/resources/repository"));

            final NoOverwriteDependencies rule = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(MavenProject.class).toInstance(project);
                    bind(MavenSession.class).toInstance(session);
                    bind(RepositorySystem.class).toInstance(repositorySystem);
                }
            }).getInstance(NoOverwriteDependencies.class);

            final EnforcerLogger log = new MockEnforcerLogger(false, false, false, false);
            rule.setLog(log);

            rule.execute();
        } catch (ComponentLookupException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProjectNoDependencies() throws EnforcerRuleException {
        applyRuleTo("correct-no-deps.xml");
    }

    @Test
    public void testProjectWithDependencies() throws EnforcerRuleException {
        applyRuleTo("correct-with-deps.xml");
    }

    @Test
    public void testNoOverwriteWrongVersionParentLevel1() throws EnforcerRuleException {
        assertThrows(EnforcerRuleException.class, () -> applyRuleTo("wrong-version-parent1.xml"));
    }

    @Test
    public void testNoOverwriteWrongVersionParentLevel2() throws EnforcerRuleException {
        assertThrows(EnforcerRuleException.class, () -> applyRuleTo("wrong-version-parent0.xml"));
    }

    @Test
    public void testNoOverwriteWrongTransitiveVersion() throws EnforcerRuleException {
        assertThrows(EnforcerRuleException.class, () -> applyRuleTo("wrong-version-transitive.xml"));
    }
}
