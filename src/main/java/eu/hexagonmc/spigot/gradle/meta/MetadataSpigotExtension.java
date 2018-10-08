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
package eu.hexagonmc.spigot.gradle.meta;

import eu.hexagonmc.spigot.annotation.meta.LoadOn;
import eu.hexagonmc.spigot.annotation.meta.PermissionDefault;
import eu.hexagonmc.spigot.annotation.meta.PluginCommand;
import eu.hexagonmc.spigot.annotation.meta.PluginMetadata;
import eu.hexagonmc.spigot.annotation.meta.PluginPermission;
import eu.hexagonmc.spigot.gradle.SpigotGradle;
import groovy.lang.Closure;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MetadataSpigotExtension extends MetadataExtension {

    public static final String EXTENSION_NAME = "spigot";

    private final Project _project;
    private Object _load;
    private final List<Object> _authors = new ArrayList<>();
    private Object _website;
    private Object _database;
    private Object _prefix;
    private final NamedDomainObjectContainer<Command> _commands;
    private final NamedDomainObjectContainer<Permission> _permissions;

    /**
     * Creates a new extension for the given project.
     *
     * @param project The project
     */
    public MetadataSpigotExtension(Project project) {
        super(project);
        _project = project;
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
        _commands = project.container(Command.class, Command::new);
        _permissions = project.container(Permission.class, name -> new Permission(project, name));
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
        return _authors.stream().map(SpigotGradle::resolveString).collect(Collectors.toList());
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
     * Sets the commands of this plugin. Resolved by {@link Closure}.
     *
     * @param closure The {@link Closure} with commands
     */
    public void commands(Closure<?> closure) {
        _commands.configure(closure);
    }

    /**
     * Gets the commands set for this plugin.
     *
     * @return The commands set
     */
    public Set<Command> commands() {
        return _commands;
    }

    /**
     * Adds a command to this plugin.
     *
     * @param name The name of the command
     */
    public void command(String name) {
        _commands.maybeCreate(name);
    }

    /**
     * Adds a command to this plugin. Resolved by {@link Closure}.
     *
     * @param name The name of the command
     * @param closure The {@link Closure} of the command
     */
    public void command(String name, Closure<?> closure) {
        _project.configure(_commands.maybeCreate(name), closure);
    }

    /**
     * Sets the permissions of this plugin. Resolved by {@link Closure}.
     *
     * @param closure The {@link Closure} with permissions
     */
    public void permissions(Closure<?> closure) {
        _permissions.configure(closure);
    }

    /**
     * Gets the permissions set for this plugin.
     *
     * @return The permissions set
     */
    public Set<Permission> permissions() {
        return _permissions;
    }

    /**
     * Adds a permission to this plugin.
     *
     * @param name The name of the permission
     */
    public void permission(String name) {
        _permissions.maybeCreate(name);
    }

    /**
     * Adds a permission to this plugin. Resolved by {@link Closure}.
     *
     * @param name The name of the permission
     * @param closure The {@link Closure} of the permission
     */
    public void permission(String name, Closure<?> closure) {
        _project.configure(_permissions.maybeCreate(name), closure);
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
        _commands.forEach(command -> {
            meta.addCommand(command.build());
        });
        _permissions.forEach(permission -> {
            meta.addPermission(permission.build());
        });
    }

    public static class Command {

        private final Object _name;
        private Object _description;
        private final List<Object> _aliases = new ArrayList<>();
        private Object _permission;
        private Object _usage;

        /**
         * Creates a new plugin command with the given name.
         *
         * @param name The name
         */
        Command(String name) {
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
         * Sets the description of this command.
         *
         * @param description The description to set
         */
        public void description(Object description) {
            _description = description;
        }

        /**
         * Gets the description set for this command.
         *
         * @return The description set
         */
        public String description() {
            return SpigotGradle.resolveString(_description);
        }

        /**
         * Adds an alias to this command.
         *
         * @param alias The alias to add
         */
        public void alias(Object alias) {
            _aliases.add(alias);
        }

        /**
         * Gets the usage set for this command.
         *
         * @return The usage set
         */
        public List<String> aliases() {
            return _aliases.stream().map(SpigotGradle::resolveString).collect(Collectors.toList());
        }

        /**
         * Sets the permission of this command.
         *
         * @param permission The permission to set
         */
        public void permission(Object permission) {
            _permission = permission;
        }

        /**
         * Gets the permission set for this command.
         *
         * @return The permission set
         */
        public String permission() {
            return SpigotGradle.resolveString(_permission);
        }

        /**
         * Sets the usage of this command.
         *
         * @param usage The usage to set
         */
        public void usage(Object usage) {
            _usage = usage;
        }

        /**
         * Gets the usage set for this command.
         *
         * @return The usage set
         */
        public String usage() {
            return SpigotGradle.resolveString(_usage);
        }

        /**
         * Builds a {@link PluginCommand} of this extension.
         *
         * @return The {@link PluginCommand}
         */
        public PluginCommand build() {
            PluginCommand command = new PluginCommand(name());
            command.setDescription(description());
            aliases().forEach(command::addAlias);
            command.setPermission(permission());
            command.setUsage(usage());
            return command;
        }
    }

    public static class Permission {

        private final Project _project;
        private final Object _name;
        private Object _description;
        private Object _value = PermissionDefault.TRUE;
        private final NamedDomainObjectContainer<Child> _childs;

        /**
         * Creates a new plugin permission with the given name.
         *
         * @param name The name
         */
        Permission(Project project, String name) {
            _project = project;
            _name = name;
            _childs = project.container(Child.class, Child::new);
        }

        /**
         * Gets the name set for this permission.
         *
         * @return The name set
         */
        public String name() {
            return SpigotGradle.resolveString(_name);
        }

        /**
         * Gets the name set for this permission.
         *
         * @return The name set
         */
        public String getName() {
            return SpigotGradle.resolveString(_name);
        }

        /**
         * Sets the description of this permission.
         *
         * @param description The description to set
         */
        public void description(Object description) {
            _description = description;
        }

        /**
         * Gets the description set for this permission.
         *
         * @return The description set
         */
        public String description() {
            return SpigotGradle.resolveString(_description);
        }

        /**
         * Sets the default value of this permission.
         *
         * @param value The default value to set
         */
        public void value(Object value) {
            _value = value;
        }

        /**
         * Gets the default value set for this permission.
         *
         * @return The default value set
         */
        public PermissionDefault value() {
            return SpigotGradle.resolveDefault(_value);
        }

        /**
         * Sets the childs of this permission. Resolved by {@link Closure}.
         *
         * @param closure The {@link Closure} with childs
         */
        public void childs(Closure<?> closure) {
            _childs.configure(closure);
        }

        /**
         * Gets the childs set for this permission.
         *
         * @return The childs set
         */
        public Set<Child> childs() {
            return _childs;
        }

        /**
         * Adds a child to this permission.
         *
         * @param name The name of the child
         */
        public void child(String name) {
            _childs.maybeCreate(name);
        }

        /**
         * Adds a child to this permission.
         *
         * @param name The name of the child
         * @param value The value of the child
         */
        public void child(String name, Boolean value) {
            _childs.maybeCreate(name).value(value);
        }

        /**
         * Adds a child to this permission. Resolved by {@link Closure}.
         *
         * @param name The name of the child
         * @param closure The {@link Closure} of the child
         */
        public void child(String name, Closure<?> closure) {
            _project.configure(_childs.maybeCreate(name), closure);
        }

        /**
         * Builds a {@link PluginPermission} of this extension.
         *
         * @return The {@link PluginPermission}
         */
        public PluginPermission build() {
            PluginPermission permission = new PluginPermission(name());
            permission.setDefault(value());
            permission.setDescription(description());
            _childs.forEach(child -> {
                permission.addChild(child.name(), child.value());
            });
            return permission;
        }

        public static class Child {

            private final Object _name;
            private Object _value = true;

            /**
             * Creates a new plugin permission child with the given name.
             *
             * @param name The name
             */
            Child(String name) {
                _name = name;
            }

            /**
             * Gets the name set for this permission child.
             *
             * @return The name set
             */
            public String name() {
                return SpigotGradle.resolveString(_name);
            }

            /**
             * Gets the name set for this permission child.
             *
             * @return The name set
             */
            public String getName() {
                return SpigotGradle.resolveString(_name);
            }

            /**
             * Sets the default value of this permission child.
             *
             * @param value The default value to set
             */
            public void value(Object value) {
                _value = value;
            }

            /**
             * Gets the default value set for this permission child.
             *
             * @return The default value set
             */
            public Boolean value() {
                return SpigotGradle.resolveBoolean(_value);
            }
        }
    }
}
