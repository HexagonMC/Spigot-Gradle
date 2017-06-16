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

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.meta.PluginDependency;
import eu.hexagonmc.spigot.annotation.meta.PluginMetadata;
import eu.hexagonmc.spigot.gradle.SpigotGradle;
import groovy.lang.Closure;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import java.util.Set;
import java.util.function.Consumer;

public class MetadataExtension implements Consumer<PluginMetadata> {

    private final Project _project;
    private Object _main;
    private Object _name;
    private Object _version;
    private Object _description;

    private final NamedDomainObjectContainer<Dependency> _dependencies;

    /**
     * Creates a new extension for the given project.
     * 
     * @param project The project
     */
    MetadataExtension(Project project) {
        _project = project;
        _name = project.getName();
        _version = new Closure<Object>(project) {

            private static final long serialVersionUID = 1L;

            @Override
            public Object call() {
                return project.getVersion();
            }
        };
        _description = new Closure<Object>(project) {

            private static final long serialVersionUID = 7890495330828090621L;

            @Override
            public Object call() {
                return project.getDescription();
            }
        };
        _dependencies = project.container(Dependency.class, Dependency::new);
    }

    /**
     * Sets the main class of this plugin.
     * 
     * @param main The main class to set
     */
    public void main(Object main) {
        _main = main;
    }

    /**
     * Gets the main class set for this plugin.
     * 
     * @return The main class set
     */
    public String main() {
        return SpigotGradle.resolveString(_main);
    }

    /**
     * Sets the name of this plugin.
     * 
     * @param name The name to set
     */
    public void name(Object name) {
        _name = name;
    }

    /**
     * Gets the name set for this plugin.
     * 
     * @return The name set
     */
    public String name() {
        return SpigotGradle.resolveString(_name);
    }

    /**
     * Gets the name set for this plugin.
     * 
     * @return The name set
     */
    public String getName() {
        return SpigotGradle.resolveString(_name);
    }

    /**
     * Sets the version of this plugin.
     * 
     * @param version The version to set
     */
    public void version(Object version) {
        _version = version;
    }

    /**
     * Gets the version set for this plugin.
     * 
     * @return The version set
     */
    public String version() {
        return SpigotGradle.resolveString(_version);
    }

    /**
     * Sets the description of this plugin.
     * 
     * @param description The description to set
     */
    public void description(Object description) {
        _description = description;
    }

    /**
     * Gets the description set for this plugin.
     * 
     * @return The description set
     */
    public String description() {
        return SpigotGradle.resolveString(_description);
    }

    /**
     * Sets the dependencies of this plugin. Resolved by {@link Closure}.
     * 
     * @param closure The {@link Closure} with dependencies
     */
    public void dependencies(Closure<?> closure) {
        _dependencies.configure(closure);
    }

    /**
     * Gets the dependencies set for this plugin.
     * 
     * @return The dependencies set
     */
    public Set<Dependency> dependencies() {
        return _dependencies;
    }

    /**
     * Adds a dependency to this plugin.
     * 
     * @param name The name of the dependency
     */
    public void dependency(String name) {
        _dependencies.maybeCreate(name);
    }

    /**
     * Adds a dependency to this plugin.
     * 
     * @param name The name of the dependency
     * @param type The type of the dependency
     */
    public void dependency(String name, String type) {
        _dependencies.maybeCreate(name).type(type);
    }

    /**
     * Adds a dependency to this plugin. Resolved by {@link Closure}.
     * 
     * @param name The name of the dependency
     * @param closure The {@link Closure} of the dependency
     */
    public void dependency(String name, Closure<?> closure) {
        _project.configure(_dependencies.maybeCreate(name), closure);
    }

    /**
     * Applies this extension to an {@link PluginMetadata}.
     */
    @Override
    public void accept(PluginMetadata meta) {
        String main = main();
        if (main != null) {
            meta.setMain(main());
        }
        meta.setName(name());
        meta.setVersion(version());
        meta.setDescription(description());
        _dependencies.forEach(dep -> {
            meta.addDependency(dep.build());
        });
    }

    public static class Dependency {

        private final Object _name;
        private Object _type = DependencyType.DEPEND;

        /**
         * Creates a new plugin dependency with the given name.
         * 
         * @param name The name
         */
        Dependency(String name) {
            _name = name;
        }

        /**
         * Gets the name set for this dependency.
         * 
         * @return The name set
         */
        public String name() {
            return SpigotGradle.resolveString(_name);
        }

        /**
         * Gets the name set for this dependency.
         * 
         * @return The name set
         */
        public String getName() {
            return SpigotGradle.resolveString(_name);
        }

        /**
         * Sets the type of this dependency.
         * 
         * @param type The type to set
         */
        public void type(Object type) {
            _type = type;
        }

        /**
         * Gets the type set for this dependency.
         * 
         * @return The type set
         */
        public DependencyType type() {
            return SpigotGradle.resolveType(_type);
        }

        /**
         * Builds a {@link PluginDependency} of this extension.
         * 
         * @return The {@link PluginDependency}
         */
        public PluginDependency build() {
            PluginDependency dep = new PluginDependency(name());
            dep.setType(type());
            return dep;
        }
    }
}
