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
package eu.hexagonmc.spigot.gradle;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.meta.LoadOn;
import groovy.lang.Closure;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

public class SpigotGradle {

    /**
     * Resolves the given {@link Object} to an {@link String}.
     * 
     * @param object The {@link Object} to process
     * @return The resolved {@link String}
     */
    public static String resolveString(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof Closure) {
            return resolveString(((Closure<?>) object).call());
        }
        return object.toString();
    }

    /**
     * Resolves the given {@link Object} to an {@link Boolean}.
     * 
     * @param object The {@link Object} to process
     * @return The resolved {@link Boolean}
     */
    public static Boolean resolveBoolean(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        if (object instanceof Closure) {
            return resolveBoolean(((Closure<?>) object).call());
        }
        return Boolean.valueOf(object.toString());
    }

    /**
     * Resolves the given {@link Object} to the enum {@link LoadOn}.
     * 
     * @param object The {@link Object} to process
     * @return The resolved enum {@link LoadOn}
     */
    public static LoadOn resolveLoad(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return LoadOn.valueOf((String) object);
        }
        if (object instanceof DependencyType) {
            return (LoadOn) object;
        }
        if (object instanceof Closure) {
            return resolveLoad(((Closure<?>) object).call());
        }
        return LoadOn.valueOf(object.toString());
    }

    /**
     * Resolves the given {@link Object} to the enum {@link DependencyType}.
     * 
     * @param object The {@link Object} to process
     * @return The resolved enum {@link DependencyType}
     */
    public static DependencyType resolveType(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return DependencyType.valueOf((String) object);
        }
        if (object instanceof DependencyType) {
            return (DependencyType) object;
        }
        if (object instanceof Closure) {
            return resolveType(((Closure<?>) object).call());
        }
        return DependencyType.valueOf(object.toString());
    }

    /**
     * Resolves the given {@link Object} to an {@link List}.
     * 
     * @param <T> The type of the list
     * @param object The {@link Object} to process
     * @return The resolved {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> resolveList(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof List) {
            return (List<T>) object;
        }
        if (object instanceof Closure) {
            return resolveList(((Closure<?>) object).call());
        }
        return new ArrayList<>();
    }

    /**
     * Gets the {@link Manifest} of the current jar.
     * 
     * @return The {@link Manifest}
     */
    public static Manifest getManifest() {
        try {
            Enumeration<URL> resources = SpigotGradle.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                if (manifest.getMainAttributes().containsKey(Name.IMPLEMENTATION_TITLE)
                        && manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_TITLE).equals("Spigot-Gradle")) {
                    return manifest;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
