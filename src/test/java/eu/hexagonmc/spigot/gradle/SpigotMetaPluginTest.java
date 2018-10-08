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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import eu.hexagonmc.spigot.gradle.util.ResourceFile;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SpigotMetaPluginTest {

    @Rule public final TemporaryFolder _testProjectDir = new TemporaryFolder();
    @Rule public ResourceFile _gradleProperties = new ResourceFile("/testkit-gradle.properties");
    private File _buildFile;

    @Before
    public void setup() throws IOException {
        _buildFile = _testProjectDir.newFile("build.gradle");
        TestUtil.writeFile(_testProjectDir.newFile("gradle.properties"), _gradleProperties.getContent());
        TestUtil.writeFile(_testProjectDir.newFile("settings.gradle"), Resources.toString(Resources.getResource("settings.gradle"), Charsets.UTF_8));
    }

    @Test
    public void testGenerateMetadata() throws IOException {
        TestUtil.writeFile(_buildFile, Resources.toString(Resources.getResource("meta-base.gradle"), Charsets.UTF_8));

        BuildResult result = GradleRunner.create()
                .withProjectDir(_testProjectDir.getRoot())
                .withArguments("generateMetadata")
                .withPluginClasspath()
                .build();

        assertThat(result.task(":generateMetadata").getOutcome()).isEqualTo(SUCCESS);
        File generateMetadataDir = new File(_testProjectDir.getRoot(), "build/tmp/generateMetadata");
        assertWithMessage("generateMetadata dir was not generated").that(generateMetadataDir.exists()).isTrue();
        File bungeeYml = new File(generateMetadataDir, "bungee.yml");
        assertWithMessage("bungee.yml file was not generated").that(bungeeYml.exists()).isTrue();
        List<String> lines;
        lines = Files.readAllLines(bungeeYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin");
        assertThat(lines.get(3)).endsWith("1.0-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("null");
        File spigotYml = new File(generateMetadataDir, "plugin.yml");
        assertWithMessage("plugin.yml file was not generated").that(spigotYml.exists()).isTrue();
        lines = Files.readAllLines(spigotYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin");
        assertThat(lines.get(3)).endsWith("1.0-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("null");
    }

    @Test
    public void testGenerateMetadataWithDependencies() throws IOException {
        TestUtil.writeFile(_buildFile, Resources.toString(Resources.getResource("meta-dependency.gradle"), Charsets.UTF_8));

        BuildResult result = GradleRunner.create()
                .withProjectDir(_testProjectDir.getRoot())
                .withArguments("generateMetadata")
                .withPluginClasspath()
                .build();

        assertThat(result.task(":generateMetadata").getOutcome()).isEqualTo(SUCCESS);
        File generateMetadataDir = new File(_testProjectDir.getRoot(), "build/tmp/generateMetadata");
        assertWithMessage("generateMetadata dir was not generated").that(generateMetadataDir.exists()).isTrue();
        File bungeeYml = new File(generateMetadataDir, "bungee.yml");
        assertWithMessage("bungee.yml file was not generated").that(bungeeYml.exists()).isTrue();
        List<String> lines;
        lines = Files.readAllLines(bungeeYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin1");
        assertThat(lines.get(3)).endsWith("1.0-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("null");
        assertThat(lines.get(7)).endsWith("test1");
        assertThat(lines.get(8)).endsWith("test2");
        assertThat(lines.get(9)).endsWith("test5");
        assertThat(lines.get(11)).endsWith("test3");
        assertThat(lines.get(12)).endsWith("test6");
        assertThat(lines.get(14)).endsWith("test4");
        assertThat(lines.get(15)).endsWith("test7");
        File spigotYml = new File(generateMetadataDir, "plugin.yml");
        assertWithMessage("plugin.yml file was not generated").that(spigotYml.exists()).isTrue();
        lines = Files.readAllLines(spigotYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin1");
        assertThat(lines.get(3)).endsWith("1.0-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("null");
        assertThat(lines.get(7)).endsWith("test1");
        assertThat(lines.get(8)).endsWith("test2");
        assertThat(lines.get(9)).endsWith("test5");
        assertThat(lines.get(11)).endsWith("test3");
        assertThat(lines.get(12)).endsWith("test6");
        assertThat(lines.get(14)).endsWith("test4");
        assertThat(lines.get(15)).endsWith("test7");
    }

    @Test
    public void testGenerateMetadataWithExisting() throws IOException {
        TestUtil.writeFile(_buildFile, Resources.toString(Resources.getResource("meta-base.gradle"), Charsets.UTF_8));
        File resourceDir = new File(_testProjectDir.getRoot(), "src/main/resources");
        resourceDir.mkdirs();
        TestUtil.writeFile(new File(resourceDir, "bungee.yml"), Resources.toString(Resources.getResource("bungee.yml"), Charsets.UTF_8));
        TestUtil.writeFile(new File(resourceDir, "plugin.yml"), Resources.toString(Resources.getResource("plugin.yml"), Charsets.UTF_8));

        BuildResult result = GradleRunner.create()
                .withProjectDir(_testProjectDir.getRoot())
                .withArguments("generateMetadata")
                .withPluginClasspath()
                .build();

        assertThat(result.task(":generateMetadata").getOutcome()).isEqualTo(SUCCESS);
        File generateMetadataDir = new File(_testProjectDir.getRoot(), "build/tmp/generateMetadata");
        assertWithMessage("generateMetadata dir was not generated").that(generateMetadataDir.exists()).isTrue();
        File bungeeYml = new File(generateMetadataDir, "bungee.yml");
        assertWithMessage("bungee.yml file was not generated").that(bungeeYml.exists()).isTrue();
        List<String> lines;
        lines = Files.readAllLines(bungeeYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin");
        assertThat(lines.get(3)).endsWith("1.1-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("eu.hexagonmc.Main");
        File spigotYml = new File(generateMetadataDir, "plugin.yml");
        assertWithMessage("plugin.yml file was not generated").that(spigotYml.exists()).isTrue();
        lines = Files.readAllLines(spigotYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin");
        assertThat(lines.get(3)).endsWith("1.1-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Test meta");
        assertThat(lines.get(5)).endsWith("eu.hexagonmc.Main");
    }

    @Test
    public void testGenerateMetadataDetail() throws IOException {
        TestUtil.writeFile(_buildFile, Resources.toString(Resources.getResource("meta-detail.gradle"), Charsets.UTF_8));

        BuildResult result = GradleRunner.create()
                .withProjectDir(_testProjectDir.getRoot())
                .withArguments("generateMetadata")
                .withPluginClasspath()
                .build();

        assertThat(result.task(":generateMetadata").getOutcome()).isEqualTo(SUCCESS);
        File generateMetadataDir = new File(_testProjectDir.getRoot(), "build/tmp/generateMetadata");
        assertWithMessage("generateMetadata dir was not generated").that(generateMetadataDir.exists()).isTrue();
        File bungeeYml = new File(generateMetadataDir, "bungee.yml");
        assertWithMessage("bungee.yml file was not generated").that(bungeeYml.exists()).isTrue();
        List<String> lines;
        lines = Files.readAllLines(bungeeYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin1");
        assertThat(lines.get(3)).endsWith("1.2-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Another test plugin");
        assertThat(lines.get(5)).endsWith("Zartec");
        assertThat(lines.get(6)).endsWith("eu.hexagonmc.Main");
        File spigotYml = new File(generateMetadataDir, "plugin.yml");
        assertWithMessage("plugin.yml file was not generated").that(spigotYml.exists()).isTrue();
        lines = Files.readAllLines(spigotYml.toPath(), Charsets.UTF_8);
        assertThat(lines.get(2)).endsWith("TestPlugin1");
        assertThat(lines.get(3)).endsWith("1.2-SNAPSHOT");
        assertThat(lines.get(4)).endsWith("Another test plugin");
        assertThat(lines.get(5)).endsWith("STARTUP");
        assertThat(lines.get(7)).endsWith("Zartec");
        assertThat(lines.get(8)).endsWith("ghac");
        assertThat(lines.get(9)).endsWith("https://hexagonmc.eu/");
        assertThat(lines.get(10)).endsWith("eu.hexagonmc.Main");
        assertThat(lines.get(11)).endsWith("false");
        assertThat(lines.get(12)).endsWith("TP1");
    }
}
