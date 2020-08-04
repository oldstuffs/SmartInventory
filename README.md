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

Here is the examples for Maven and Gradle:
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

## Getting Started
### Registering the library
#### Static version (Not recommending)
```java
import io.github.portlek.smartinventory.manager.BasicSmartInventory;

public final class Main extends JavaPlugin {

    public static final SmartInventory inventory = new BasicSmartInventory();

    @Override
    public void onEnable() {
        Main.inventory.init();
    }

}
```
#### D.I. version (Recommending)
```java
import io.github.portlek.smartinventory.manager.BasicSmartInventory;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        final SmartInventory inventory = new BasicSmartInventory();
        inventory.init();
        new SomeClassesThatNeedSmartInventory(inventory).foo();
        new SomeOtherClasses(inventory).foo();
    }

}
```
### Creating a Inventory Provider Class
```java
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.InventoryProvided;
import org.jetbrains.annotations.NotNull;

public final class ExampleInventoryProvided implements InventoryProvided {

    @Override
    public void init(@NotNull final InventoryContents contents) {
        // Runs when the page opens first.
    }

    @Override
    public void update(@NotNull final InventoryContents contents) {
        // Runs when the notify update method called by you.
        // SmartInventory#notifyUpdate(Player)
        // InventoryContents#notifyUpdate()
    }

    @Override
    public void tick(@NotNull final InventoryContents contents) {
        // Runs every tick.
        // You have options that;
        // -> make it async or not (default false)
        // -> set the tick's start delay (default 1L)
        // -> set the tick period (default 1L)
    }

}
```
### Creating a Page
```java
import io.github.portlek.smartinventory.InventoryProvided;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import org.jetbrains.annotations.NotNull;

public final class CreateAPage {

    @NotNull
    private final SmartInventory inventory;
    
    @NotNull
    private final InventoryProvided provided;

    public CreateAPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvided provided) {
        this.inventory = inventory;
        this.provided =provided;
    }

    public void open(@NotNull final Player player) {
        Page.build(this.inventory, this.provided)
            .open(player);
    }

}
```
