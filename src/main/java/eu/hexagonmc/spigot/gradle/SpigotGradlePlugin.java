/**
 *
 * Copyright (C) 2017 - 2018  HexagonMc <https://github.com/HexagonMC>
 * Copyright (C) 2017 - 2018  Zartec <zartec@mccluster.eu>
 *
 *     This file is part of Spigot-Gradle.
 *
 *     Spigot-Gradle is free software:
 *     you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Spigot-Gradle is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Spigot-Gradle.
 *     If not, see <http://www.gnu.org/licenses/>.
 */
package eu.hexagonmc.spigot.gradle;

import com.google.common.base.Strings;
import eu.hexagonmc.spigot.gradle.meta.MetadataPlugin;
import groovy.json.JsonSlurper;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.util.GradleVersion;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

public class SpigotGradlePlugin implements Plugin<Project> {

    private Project _project;
    private static boolean _once = false;

    @Override
    public void apply(Project project) {
        _project = project;
        applyPlugins();

        applyJava8();

        addRepositoryAndDependency();

        applyCustomPlugins();

        applyAfterEvaluate();
    }

    private void applyPlugins() {
        PluginContainer plugins = _project.getPlugins();
        plugins.apply(JavaPlugin.class);
        plugins.apply(EclipsePlugin.class);
        plugins.apply(IdeaPlugin.class);
    }

    private void applyJava8() {
        JavaPluginConvention pluginConvention = _project.getConvention().getPlugin(JavaPluginConvention.class);
        pluginConvention.setSourceCompatibility(JavaVersion.VERSION_1_8);
        pluginConvention.setTargetCompatibility(JavaVersion.VERSION_1_8);
    }

    private void addRepositoryAndDependency() {
        RepositoryHandler repositories = _project.getRepositories();
        repositories.add(repositories.jcenter());

        DependencyHandler dependencies = _project.getDependencies();
        dependencies.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, "eu.hexagonmc:spigot-annotations:1.2");

        boolean annotationProcessorConfigurationAvailable =
                GradleVersion.version(_project.getGradle().getGradleVersion()).compareTo(GradleVersion.version("4.6")) >= 0;
        if (annotationProcessorConfigurationAvailable) {
            dependencies.add(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME, "eu.hexagonmc:spigot-annotations:1.2");
        }
    }

    private void applyCustomPlugins() {
        PluginContainer plugins = _project.getPlugins();
        plugins.getPlugin(IdeaPlugin.class).getModel().getModule().setInheritOutputDirs(true);

        plugins.apply(MetadataPlugin.class);
        plugins.apply(SpigotAnnotationPlugin.class);
    }

    private void applyAfterEvaluate() {
        if (_once) {
            return;
        }
        _project.afterEvaluate(project -> {
            Logger logger = project.getLogger();
            Package pck = getClass().getPackage();
            int size = 98;
            logger.lifecycle("#{}#", formatCenterString("#", '#', size));
            logger.lifecycle("#{}#", formatCenterString(" ", ' ', size));
            logger.lifecycle("#{}#", formatCenterString(pck.getImplementationTitle(), ' ', size));
            logger.lifecycle("#{}#", formatCenterString("version " + pck.getImplementationVersion(), ' ', size));
            logger.lifecycle("#{}#", formatCenterString("by " + pck.getImplementationVendor(), ' ', size));
            logger.lifecycle("#{}#", formatCenterString(" ", ' ', size));
            Manifest mf = SpigotGradle.getManifest();
            if (mf != null) {
                String copyrightFormat = "Copyright (C) %d  %s <%s>";
                int year = Calendar.getInstance().get(Calendar.YEAR);
                logger.lifecycle("#{}#", formatCenterString(
                        String.format(copyrightFormat, year, pck.getImplementationVendor(), mf.getMainAttributes().getValue("Url")), ' ', size));
                JsonSlurper jsonSlurper = new JsonSlurper();
                Object json = jsonSlurper.parseText(mf.getMainAttributes().getValue("Developers"));
                if (json != null && json instanceof List) {
                    List<?> developers = (List<?>) json;
                    developers.forEach(developer -> {
                        if (developer instanceof Map) {
                            Map<?, ?> values = (Map<?, ?>) developer;
                            Object name = values.get("name");
                            Object email = values.get("email");
                            if (name instanceof String && !Strings.isNullOrEmpty((String) name) && email instanceof String
                                    && !Strings.isNullOrEmpty((String) email)) {
                                logger.lifecycle("#{}#", formatCenterString(String.format(copyrightFormat, year, name, email), ' ', size));
                            }
                        }
                    });
                }
            }
            logger.lifecycle("#{}#", formatCenterString(" ", ' ', size));
            logger.lifecycle("#{}#", formatCenterString("#", '#', size));
        });
        _once = true;
    }

    private String formatCenterString(String content, char filler, int length) {
        if (content == null) {
            content = "";
        }
        length = length - content.length();
        int right = length / 2;
        int left = length - right;
        return String.format("%1$" + left + "c%2$s%1$" + right + "c", filler, content).replace(' ', filler);
    }
}
