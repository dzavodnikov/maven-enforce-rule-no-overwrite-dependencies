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

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

/**
 * Class that have useful methods for testing Enforcer Plugin Rules.
 */
public abstract class AbstractRuleTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() {
        // It is not a plug-in, su we are no need to initialize it.
    }

    /**
     * Read POM file from resources.
     *
     * @param filename name a file at <code>src/test/resources</code>.
     * @return model.
     */
    protected Model readPom(final String projectPomFileName) {
        final File pom = getTestFile("src/test/resources", projectPomFileName);
        assertTrue(pom.exists());

        try {
            final ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
            final ModelBuildingRequest buildRequest = new DefaultModelBuildingRequest();
            buildRequest.setPomFile(pom);

            final ModelBuildingResult result = modelBuilder.build(buildRequest);
            final Model effectiveModel = result.getEffectiveModel();
            return effectiveModel;
        } catch (ModelBuildingException e) {
            throw new RuntimeException(e);
        }
    }

    protected MavenProject readMavenProject(final String projectPomFileName) {
        final Model pom = readPom(projectPomFileName);
        assertNotNull(pom);
        final MavenProject project = new MavenProject(pom);
        return project;
    }

    /**
     * Initialize repository that will be used to resolve dependencies.
     */
    protected void initTestRepository(final MavenSession session, File repositoryLocation) {
        try {
            if (repositoryLocation == null) { // Use default repository.
                final File userHome = new File(System.getProperty("user.home"));
                assertTrue(userHome.exists());
                repositoryLocation = new File(userHome, ".m2/repository/");
            }
            assertTrue("Test repository is not exists", repositoryLocation.exists());

            final RepositorySystemSession systemSession = session.getRepositorySession();
            assertNotNull(systemSession);

            final LocalRepository localRepo = new LocalRepository(repositoryLocation);
            final SimpleLocalRepositoryManagerFactory repoManagerFactory = new SimpleLocalRepositoryManagerFactory();
            final LocalRepositoryManager localRepoManager = repoManagerFactory.newInstance(systemSession, localRepo);
            ((DefaultRepositorySystemSession) systemSession).setLocalRepositoryManager(localRepoManager);
        } catch (NoLocalRepositoryManagerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize default (<code>${HOME}/.m2/repository</code>) repository that will
     * be used to resolve dependencies.
     */
    protected void initTestRepository(final MavenSession session) {
        initTestRepository(session, null);
    }
}
