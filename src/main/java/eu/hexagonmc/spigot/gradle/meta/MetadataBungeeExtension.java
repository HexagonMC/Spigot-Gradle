/**
 *
 * Copyright (C) 2017  HexagonMc <https://github.com/HexagonMC>
 * Copyright (C) 2017  Zartec <zartec@mccluster.eu>
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
import eu.hexagonmc.spigot.gradle.SpigotGradle;
import org.gradle.api.Project;

public class MetadataBungeeExtension extends MetadataExtension {

    public static final String EXTENSION_NAME = "bungee";

    private Object _author;

    /**
     * Creates a new extension for the given project.
     * 
     * @param project The project
     */
    public MetadataBungeeExtension(Project project) {
        super(project);
    }

    /**
     * Sets the author for this plugin.
     * 
     * @param author The author to set
     */
    public void author(Object author) {
        _author = author;
    }

    /**
     * Gets the author set for this plugin.
     * 
     * @return The author set
     */
    public String author() {
        return SpigotGradle.resolveString(_author);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void accept(PluginMetadata meta) {
        super.accept(meta);
        String author = author();
        if (author != null) {
            meta.getAuthors().clear();
            meta.addAuthor(author());
        }
    }
}
