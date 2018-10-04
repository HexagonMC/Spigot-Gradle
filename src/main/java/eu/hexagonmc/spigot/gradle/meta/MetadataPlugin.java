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
package eu.hexagonmc.spigot.gradle.meta;

import eu.hexagonmc.spigot.annotation.meta.PluginMetadata;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.CopySpec;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

public class MetadataPlugin implements Plugin<Project> {

    /**
     * {@inheritDoc}.
     */
    @Override
    public void apply(Project project) {
        project.getPlugins().apply("java-base");

        ExtensionContainer extensions = project.getExtensions();
        final MetadataSpigotExtension spigotExtension;
        if (extensions.findByType(MetadataSpigotExtension.class) == null) {
            spigotExtension = extensions.create(MetadataSpigotExtension.EXTENSION_NAME, MetadataSpigotExtension.class, project);
        } else {
            spigotExtension = extensions.findByType(MetadataSpigotExtension.class);
        }

        final MetadataBungeeExtension bungeeExtension;
        if (extensions.findByType(MetadataBungeeExtension.class) == null) {
            bungeeExtension = extensions.create(MetadataBungeeExtension.EXTENSION_NAME, MetadataBungeeExtension.class, project);
        } else {
            bungeeExtension = extensions.findByType(MetadataBungeeExtension.class);
        }

        TaskContainer tasks = project.getTasks();
        GenerateMetadataTask genMeta = tasks.create("generateMetadata", GenerateMetadataTask.class);

        genMeta.setProviderSpigot(() -> {
            PluginMetadata meta = new PluginMetadata(spigotExtension.name());
            spigotExtension.accept(meta);
            return meta;
        });

        genMeta.setProviderBungee(() -> {
            PluginMetadata meta = new PluginMetadata(bungeeExtension.name());
            bungeeExtension.accept(meta);
            return meta;
        });

        Task processResources = tasks.getByName("processResources");
        CopySpec processResourcesCopySepc = (CopySpec) processResources;
        processResourcesCopySepc.from(genMeta);
    }
}
