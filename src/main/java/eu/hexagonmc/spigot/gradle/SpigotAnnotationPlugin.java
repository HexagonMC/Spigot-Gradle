/**
 *
 * Copyright (C) 2017 - 2018  HexagonMc <https://github.com/HexagonMC>
Copyright (C) 2017 - 2018  Zartec <zartec@mccluster.eu>
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

import com.google.common.base.Joiner;
import eu.hexagonmc.spigot.annotation.AnnotationProcessor;
import eu.hexagonmc.spigot.annotation.meta.PluginYml;
import eu.hexagonmc.spigot.gradle.meta.GenerateMetadataTask;
import eu.hexagonmc.spigot.gradle.meta.MetadataPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.compile.JavaCompile;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpigotAnnotationPlugin implements Plugin<Project> {

    private static final String PLUGIN_ANNOTATION_PROCESSOR = "eu.hxmc.spigot.annotation.AnnotationProcessor";

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(MetadataPlugin.class);

        final GenerateMetadataTask generateMetadata = (GenerateMetadataTask) project.getTasks().getByName("generateMetadata");
        generateMetadata.setMergeMetadata(false);

        Task compileJava = project.getTasks().getByName("compileJava");
        compileJava.getInputs().files(generateMetadata);
        compileJava.doFirst(task -> {
            JavaCompile javac = (JavaCompile) task;
            List<String> args = javac.getOptions().getCompilerArgs();

            int pos = args.indexOf("-processor");
            if (pos >= 0) {
                args.set(pos + 1, args.get(pos + 1) + "," + PLUGIN_ANNOTATION_PROCESSOR);
            }

            Path targetSpigot = generateMetadata.getTargetSpigot();
            Path targetBungee = generateMetadata.getTargetBungee();

            Set<String> extraFilesSpigot = new HashSet<>();
            extraFilesSpigot.add(targetSpigot.toString());
            extraFilesSpigot.addAll(generateMetadata.getMetadataFilesSpigot().stream().map(Path::toString).collect(Collectors.toSet()));

            Set<String> extraFilesBungee = new HashSet<>();
            extraFilesBungee.add(targetBungee.toString());
            extraFilesBungee.addAll(generateMetadata.getMetadataFilesBungee().stream().map(Path::toString).collect(Collectors.toSet()));

            args.add("-A" + AnnotationProcessor.EXTRA_FILES_SPIGOT_OPTION + "=" + Joiner.on(';').skipNulls().join(extraFilesSpigot));
            args.add("-A" + AnnotationProcessor.EXTRA_FILES_BUNGEE_OPTION + "=" + Joiner.on(';').skipNulls().join(extraFilesBungee));
        });

        Task processResources = project.getTasks().getByName("processResources");
        CopySpec processResourcesCopySepc = (CopySpec) processResources;
        processResourcesCopySepc.exclude(PluginYml.FILENAME_SPIGOT);
        processResourcesCopySepc.exclude(PluginYml.FILENAME_BUNGEE);
    }
}
