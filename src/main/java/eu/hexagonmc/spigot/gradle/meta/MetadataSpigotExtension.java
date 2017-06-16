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

import eu.hexagonmc.spigot.annotation.meta.LoadOn;
import eu.hexagonmc.spigot.annotation.meta.PluginMetadata;
import eu.hexagonmc.spigot.gradle.SpigotGradle;
import groovy.lang.Closure;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataSpigotExtension extends MetadataExtension {

    public static final String EXTENSION_NAME = "spigot";

    private Object _load;
    private final List<Object> _authors = new ArrayList<>();
    private Object _website;
    private Object _database;
    private Object _prefix;

    /**
     * Creates a new extension for the given project.
     * 
     * @param project The project
     */
    public MetadataSpigotExtension(Project project) {
        super(project);
        _website = new Closure<Object>(project) {

            private static final long serialVersionUID = 1860033356463917877L;

            @Override
            public Object call() {
                Object website = project.findProperty("url");
                if (website == null) {
                    website = project.findProperty("website");
                }
                return website;
            }
        };
    }

    /**
     * Sets the load stage for this plugin.
     * 
     * @param load The load stage to set
     * @see LoadOn
     */
    public void load(Object load) {
        _load = load;
    }

    /**
     * Gets the load stage set for this plugin.
     * 
     * @return The load stage set
     * @see LoadOn
     */
    public LoadOn load() {
        return SpigotGradle.resolveLoad(_load);
    }

    /**
     * Adds an author to this plugin.
     * 
     * @param author The author to add
     */
    public void author(Object author) {
        _authors.add(author);
    }

    /**
     * Gets the authors of this plugin.
     * 
     * @return The authors
     * @see List
     */
    public List<String> authors() {
        return _authors.stream().map(a -> SpigotGradle.resolveString(a)).collect(Collectors.toList());
    }

    /**
     * Sets the website for this plugin.
     * 
     * @param website The website to set
     */
    public void website(Object website) {
        _website = website;
    }

    /**
     * Gets the website set for this plugin.
     * 
     * @return The website set
     */
    public String website() {
        return SpigotGradle.resolveString(_website);
    }

    /**
     * Sets if this plugin uses database.
     * 
     * @param database True if database is used false otherwise
     */
    public void database(Object database) {
        _database = database;
    }

    /**
     * Gets if this plugin uses database.
     * 
     * @return True if database is used false otherwise
     */
    public Boolean database() {
        return SpigotGradle.resolveBoolean(_database);
    }

    /**
     * Sets the logging prefix for this plugin.
     * 
     * @param prefix The logging prefix to set
     */
    public void prefix(Object prefix) {
        _prefix = prefix;
    }

    /**
     * Gets the logging prefix set for this plugin.
     * 
     * @return The logging prefix set
     */
    public String prefix() {
        return SpigotGradle.resolveString(_prefix);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void accept(PluginMetadata meta) {
        super.accept(meta);
        meta.setLoadOn(load());
        meta.getAuthors().clear();
        authors().forEach(meta::addAuthor);
        meta.setWebsite(website());
        meta.setDatabase(database());
        meta.setPrefix(prefix());
    }
}
