<img src="logo/logo.svg" width="92px"/>

[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/) [![rultor](https://www.rultor.com/b/yegor256/rultor)](https://www.rultor.com/p/portlek/SmartInventory)

[![Build Status](https://travis-ci.com/portlek/SmartInventory.svg?branch=master)](https://travis-ci.com/portlek/SmartInventory) ![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/SmartInventory?label=version)

## How to use
```xml
<dependency>
    <groupId>io.github.portlek</groupId>
    <artifactId>SmartInventory</artifactId>
    <version>${version}</version>
</dependency>
```
```groovy
implementation("io.github.portlek:SmartInventory:${version}")
```
**Do not forget to relocate `io.github.portlek.smartinventory` package into your package.**
Here is the examples for maven and gradle:
<details>
<summary>Maven</summary>

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <configuration>
        <!-- Other settings -->
        <relocations>
            <relocation>
                <pattern>io.github.portlek.smartinventory</pattern>
                <!-- Replace this -->
                <shadedPattern>your.package.path.to.relocate</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
</details>
<details>
<summary>Gradle</summary>

```groovy
plugins {
    id "com.github.johnrengelman.shadow" version "6.0.0"
}

shadowJar {
    relocate('io.github.portlek.smartinventory', "your.package.path.to.relocate")
    // other stuffs.
}
```
</details>
