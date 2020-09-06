<img src="logo/logo.svg" width="92px"/>

[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)
[![rultor](https://www.rultor.com/b/yegor256/rultor)](https://www.rultor.com/p/portlek/SmartInventory)

[![Build Status](https://travis-ci.com/portlek/SmartInventory.svg?branch=master)](https://travis-ci.com/portlek/SmartInventory)
![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/smartui-common?label=version)

## How to use
```xml
<!-- For Bukkit and containers -->
<!-- Includes all conteiner inventory types.-->
<dependency>
    <groupId>io.github.portlek</groupId>
    <artifactId>smartui-bukkit-container</artifactId>
    <version>${version}</version>
</dependency>
```
```groovy
// For Bukkit and containers
// Includes all conteiner inventory types.
implementation("io.github.portlek:smartui-bukkit-container:${version}")
```
## Getting Started
### Registering the library
#### Static version (Not recommending)
#### D.I. version (Recommending)
### Creating a Inventory Provider Class
### Creating a Page
## Useful libraries with SmartInventory
### Simple Bukkit item builder library with builder pattern.
[ItemBuilder](https://github.com/portlek/BukkitItemBuilder)
### You can get inputs from players via chat.
[Input](https://github.com/portlek/input)
