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

import eu.hexagonmc.spigot.annotation.meta.PluginMetadata;
import eu.hexagonmc.spigot.annotation.meta.PluginYml;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.specs.Specs;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GenerateMetadataTask extends DefaultTask {

    private boolean _mergeMetadata = true;

    private Supplier<PluginMetadata> _providerSpigot = () -> null;
    private Supplier<PluginMetadata> _providerBungee = () -> null;

    private Path _targetSpigot;
    private Path _targetBungee;

    private List<Path> _metadataFilesSpigot = new ArrayList<>();
    private List<Path> _metadataFilesBungee = new ArrayList<>();

    /**
     * Constructor sets task never up to date.
     */
    public GenerateMetadataTask() {
        getOutputs().upToDateWhen(Specs.satisfyNone());
    }

    /**
     * Enables or disables the merging of available {@link PluginMetadata}
     * sources.
     *
     * @param state True to enable false otherwise
     */
    public void setMergeMetadata(boolean state) {
        _mergeMetadata = state;
    }

    /**
     * Sets an supplier that returns existing spigot {@link PluginMetadata}.
     *
     * @param supplier The supplier
     */
    public void setProviderSpigot(Supplier<PluginMetadata> supplier) {
        _providerSpigot = supplier;
    }

    /**
     * Returns the supplied {@link PluginMetadata} from the supplier set by
     * {@link #setProviderSpigot(Supplier)}.
     *
     * @return The {@link PluginMetadata}
     */
    private PluginMetadata getMetadataSpigot() {
        return _providerSpigot.get();
    }

    /**
     * Sets an supplier that returns existing bungee {@link PluginMetadata}.
     *
     * @param supplier The supplier
     */
    public void setProviderBungee(Supplier<PluginMetadata> supplier) {
        _providerBungee = supplier;
    }

    /**
     * Returns the supplied {@link PluginMetadata} from the supplier set by
     * {@link #setProviderBungee(Supplier)}.
     *
     * @return The {@link PluginMetadata}
     */
    private PluginMetadata getMetadataBungee() {
        return _providerBungee.get();
    }

    /**
     * Gets the {@link Path} for the generated spigot {@link PluginMetadata}.
     *
     * @return The {@link Path}
     */
    @Internal
    public Path getTargetSpigot() {
        if (_targetSpigot == null) {
            return _targetSpigot = getTemporaryDir().toPath().resolve(PluginYml.FILENAME_SPIGOT);
        }
        return _targetSpigot;
    }

    /**
     * Gets the spigot output file.
     *
     * @return The output file
     */
    @OutputFile
    public File getOuputFileSpigot() {
        return getTargetSpigot().toFile();
    }

    /**
     * Gets the {@link Path} for the generated bungee {@link PluginMetadata}.
     *
     * @return The {@link Path}
     */
    @Internal
    public Path getTargetBungee() {
        if (_targetBungee == null) {
            return _targetBungee = getTemporaryDir().toPath().resolve(PluginYml.FILENAME_BUNGEE);
        }
        return _targetBungee;
    }

    /**
     * Gets the bungee output file.
     *
     * @return The output file
     */
    @OutputFile
    public File getOuputFileBungee() {
        return getTargetBungee().toFile();
    }

    /**
     * Gets the {@link Path}'s to the spigot {@link PluginMetadata} resolved by
     * {@link #findExtraMetadataFiles(SourceSet)}.
     *
     * @return The {@link Path}'s
     */
    @Internal
    public List<Path> getMetadataFilesSpigot() {
        return _metadataFilesSpigot;
    }

    /**
     * Gets the {@link Path}'s to the bungee {@link PluginMetadata} resolved by
     * {@link #findExtraMetadataFiles(SourceSet)}.
     *
     * @return The {@link Path}'s
     */
    @Internal
    public List<Path> getMetadataFilesBungee() {
        return _metadataFilesBungee;
    }

    /**
     * {@inheritDoc}.
     */
    @TaskAction
    void generateMetadata() throws IOException {
        JavaPluginConvention java = getProject().getConvention().getPlugin(JavaPluginConvention.class);
        findExtraMetadataFiles(java.getSourceSets().getByName("main"));

        PluginMetadata metaDataSpigot = getMetadataSpigot();
        if (_mergeMetadata) {
            for (Path path : _metadataFilesSpigot) {
                PluginMetadata metaData = PluginYml.read(path);
                if (metaDataSpigot == null) {
                    metaDataSpigot = metaData;
                } else {
                    metaDataSpigot.accept(metaData);
                }
            }
        }
        PluginYml.write(getTargetSpigot(), metaDataSpigot);

        PluginMetadata metaDataBungee = getMetadataBungee();
        if (_mergeMetadata) {
            for (Path path : _metadataFilesBungee) {
                PluginMetadata metaData = PluginYml.read(path);
                if (metaDataBungee == null) {
                    metaDataBungee = metaData;
                } else {
                    metaDataBungee.accept(metaData);
                }
            }
        }
        PluginYml.write(getTargetBungee(), metaDataBungee);
    }

    /**
     * Searches in projects resources for existing plugin.yml und bungee.yml
     * files.
     *
     * @param sourceSet The {@link SourceSet} to search in
     */
    private void findExtraMetadataFiles(SourceSet sourceSet) {
        FileTree files;
        files = sourceSet.getResources()
                .matching(filterable -> filterable.include(PluginYml.FILENAME_SPIGOT));
        for (File file : files.getFiles()) {
            _metadataFilesSpigot.add(file.toPath().toAbsolutePath());
        }
        files = sourceSet.getResources()
                .matching(filterable -> filterable.include(PluginYml.FILENAME_BUNGEE));
        for (File file : files.getFiles()) {
            _metadataFilesBungee.add(file.toPath().toAbsolutePath());
        }
    }
}
