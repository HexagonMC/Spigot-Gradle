# Spigot-Gradle

Gradle plugin for Spigot- and BungeeCord-Plugins

[![Build Status](https://travis-ci.org/HexagonMC/Spigot-Gradle.svg?branch=master)](https://travis-ci.org/HexagonMC/Spigot-Gradle)
[ ![Download](https://api.bintray.com/packages/hexagonmc/Spigot/Spigot-Gradle/images/download.svg) ](https://bintray.com/hexagonmc/Spigot/Spigot-Gradle/_latestVersion)
[![codecov](https://codecov.io/gh/HexagonMC/Spigot-Gradle/branch/master/graph/badge.svg)](https://codecov.io/gh/HexagonMC/Spigot-Gradle)
[![Maven Central](https://img.shields.io/maven-central/v/eu.hexagonmc/spigot-gradle.svg)](https://repo1.maven.org/maven2/eu/hexagonmc/spigot-gradle/)

This plugin automatically generates the `plugin.yml` and `bungee.yml` for you gradle project.

The project is hosted on `jCenter` and `Maven Central`.

## Usage

To use it simply add the plugin to your project as described below.
After this your project values like `name`, `version`, `description` and `ext.url or ext.website` are inherited by the `plugin.yml` and `bungee.yml`. You can also specify the configuration blocks `spigot` and `bungee` inside your build script to overwrite the default values.

Any other values can be defined in the resource files `plugin.yml` and `bungee.yml` (in `src/main/resources`) like before or in the configuration blocks.

The main class can be defined in the resource files, the configuration block or via [Spigot-Annotations](https://github.com/HexagonMC/Spigot-Annotations/blob/master/README.md)

Properties defined via annotation will override config and resource properties. Config properties will overwrite resource properties.

For example:

#### Spigot

```gradle
spigot {
    main "eu.hexagonmc.Main"
    name "TestPlugin"
    version "1.0.0-SNAPSHOT"
    description "A test plugin"
    author "Zartec"
    author "ghac"
    load "STARTUP"
    website "https://hexagonmc.eu/"
    database false
    prefix "TP-Logger"
    dependencies {
        Vault
        Vault {
            type "DEPEND"
        }
        Vault {
            type "SOFTDEPEND"
        }
        Vault {
            type "LOADBEFORE"
        }
    }
    dependency "Vault"
    dependency "Vault", "SOFTDEPEND"
    dependency("Vault") {
        type "LOADBEFORE"
    }
    commands {
        test1
        test2 {
            description "A test command"
            alias "test2.1"
            alias "test2.2"
            permission "command.test2"
            usage "/<command>"
        }
    }
    command "test3"
    command("test4") {
        description "A test command"
        alias "test4.1"
        alias "test4.2"
        permission "command.test4"
        usage "/<command>"
    }
    permissions {
        "command.test1" {}
        "command.test2" {
            description "A test permission"
            value "OP"
            childs {
                "command.test2.1"
                "command.test2.2" {
                    value false
                }
            }
            child "command.test2.3"
            child "command.test2.4", false
            child("command.test2.5") {
                value false
            }
        }
    }
    permission "command.test3"
    permission("command.test4") {
        description "A test permission"
            value "OP"
            childs {
                "command.test4.1"
                "command.test4.2" {
                    value false
                }
            }
            child "command.test4.3"
            child "command.test4.4", false
            child("command.test4.5") {
                value false
            }
    }
}
```

#### BungeeCord

```gradle
bungee {
    main "eu.hexagonmc.Main"
    name "TestPlugin"
    version "1.0.0-SNAPSHOT"
    description "A test plugin"
    author "Zartec" // Only one author possible
    dependencies {
        Vault
        Vault {
            type "DEPEND"
        }
        Vault {
            type "SOFTDEPEND"
        }
        Vault {
            type "LOADBEFORE"
        }
    }
    dependency "Vault"
    dependency "Vault", "SOFTDEPEND"
    dependency("Vault") {
        type "LOADBEFORE"
    }
}
```

### Via buildscript block

Add the buildscript part to your `build.gradle`.

```gradle
...
buildscript {
    ...
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
        or
        jCenter()
        or
        mavenCentral()
    }
    ...
    dependencies {
        classpath group: 'eu.hexagonmc', name: 'spigot-gradle', version: '1.0'
    }
}
...
```

Apply the plugin in your `build.gradle`.

```gradle
...
apply plugin: "eu.hexagonmc.gradle.spigot"
...
```

### Via plugins block

Apply the plugin in your `build.gradle`.

```gradle
...
plugins {
    id "eu.hexagonmc.gradle.spigot" version "1.0"
}
...
```