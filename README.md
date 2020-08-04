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
public final class ExampleInventoryProvided implements InventoryProvided {

    @Override
    public void init(@NotNull final InventoryContents contents) {
        // Runs when the page opens first.
        // An icon that which is empty(air).
        final Icon empty = Icon.empty();
        // An icon that has not any effect.
        final Icon noeffect = Icon.from(new ItemStack(Material.DIAMOND));
        // A simple static icon that player can't click it.
        final Icon cancel = Icon.cancel(new ItemStack(Material.DIAMOND));
        final Icon click = Icon.click(new ItemStack(Material.DIAMOND), clickEvent -> {
            // Runs when the player click the icon.
        }, clickEvent -> {
            // It's array so, optional.
            // If the predicate is returning true, the consumer that above will run.
            return true;
        });
        final Icon drag = Icon.drag(new ItemStack(Material.DIAMOND), dragEvent -> {
            // Runs when the player drag the icon.
        });
        final Icon anIcon = Icon.from(new ItemStack(Material.DIAMOND))
            .whenClick(clickEvent -> {
                // Runs when player clicks the icon.
            }, clickEvent -> {
                // If this predicate returns false, the consumer won't run.
                return false;
            })
            .whenDrag(dragEvent -> {
                // Runs when player drags the icon.
            })
            .whenInteract(dragEvent -> {
                // Runs when player interact to the icon.
            })
            .canSee(contents -> {
                // If it's returning false, player will see the fallback icon on the page.
                return false;
            })
            .fallback(new ItemStack(Material.AIR))
            .canUse(contents -> {
                // If it's returning false, player can't use the icon on the page.
                return false;
            });
        // Adding an icon into the inventory.
        contents.add(anIcon);
        // Adds to the certain slot.
        contents.set(SlotPos.of(0, 4), anIcon);
        // A pagination example.
        final Pagination pagination = contents.pagination();
        final Icon[] icons = new Icon[22];
        for(final int index = 0; index < icons.length; i++) {
            icons[index] = Icon.cancel(new ItemStack(Material.CHORUS_FRUIT, i));
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(7);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));
        final Icon previousArrow = Icon.click(new ItemStack(Material.ARROW), clickEvent -> 
            contents.page().open(player, pagination.previous().getPage()));
        final Icon nextArrow = Icon.click(new ItemStack(Material.ARROW), clickEvent ->
            contents.page().open(player, pagination.next().getPage()));
        contents.set(2, 3, previousArrow);
        contents.set(2, 5, nextArrow);
        // And other tons of methods will help you to make a awesome pages :)
    }

    @Override
    public void update(@NotNull final InventoryContents contents) {
        // Runs when the notify update method called by you.
        // SmartInventory#notifyUpdate(Player)
        // -> Finds the player's page, if it's open, runs the update method.
        // InventoryContents#notifyUpdate()
        // -> Runs the update method of this class.
    }

    @Override
    public void tick(@NotNull final InventoryContents contents) {
        // Runs every tick.
        // You have options that;
        // -> make it async or not (default is false)
        // -> set the tick's start delay (default is 1L)
        // -> set the tick period (default is 1L)
    }

}
```
### Creating a Page
```java
public final class CreateAPage {

    @NotNull
    private final SmartInventory inventory;
    
    @NotNull
    private final InventoryProvided provided;

    public CreateAPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvided provided) {
        this.inventory = inventory;
        this.provided =provided;
    }

    public void openASimplePage(@NotNull final Player player) {
        Page.build(this.inventory, this.provided)
            .title("Title")
            .row(3)
            .open(player);
    }

    public void open(@NotNull final Page parentPage, @NotNull final Player player) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("test-key", player.getName());
        Page.build(this.inventory, this.provided)
            // Runs the update method as async. (default is false)
            .async(true)
            // If it's returning false, player's page will close and open immediately. (default is true)
            // Closing a page cannot be canceled. It just closes and opens again method.
            .canClose(true)
            .canClose(closeEvent -> true)
            // Set the page's column. (default is 9)
            // There is no any different page type, so it must be 9 for now.
            .column(9)
            // Set the page's parent page.(default is empty)
            // contents.page().parent().ifPresent(page -> ...)
            .parent(parentPage)
            // Set the page's row size. (default is 1)
            // The row's range is 1 to 6
            .row(3)
            // Set the page's start delay of the tick method. (default is 1L)
            .startDelay(10L)
            // Set the page's period time of the tick method. (default is 1L)
            .tick(1L)
            // Set the page's title. (default is Smart Inventory)
            .title("Title")
            // Runs after the page opened. If predicates cannot passed, the consumer won't run.
            .whenOpen(openEvent -> {
                openEvent.contents().player().sendMessage("The page opened.");
                openEvent.contents().player().sendMessage("This message will send to Player.");
            },
            // These predicates are optional. It's Predicate<OpenEvent>... (array)
            openEvent -> {
                return openEvent.contents().player().getName().equals("Player");
            }, openEvent -> {
                return openEvent.contents().player().hasPermission("test.perm");
            })
            // Runs after the page closed. If predicates cannot passed, the consumer won't run.
            .whenClose(closeEvent -> {
                openEvent.contents().player().sendMessage("The page closed.");
                closeEvent.contents().player().sendMessage("This message will send to Player.");
            },
            // These predicates are optional. It's Predicate<CloseEvent>... (array)
            closeEvent -> {
                return openEvent.contents().player().getName().equals("Player");
            }, closeEvent -> {
                return openEvent.contents().player().hasPermission("test.perm");
            })
            // Opens the page for the player.
            // With properties.
            // You can get the properies with
            // Get a property that can be nullable > contents.getProperty("test-key");
            // Get a property that cannot be nullable > contents.getPropertyOrDefault("test-key-2", "fallback");
            // You can also set a property > contents.setProperty("test-key-2", "test-object");
            // .open(player, properties);
            // With properties and pagination number.
            // .open(player, 2, properties);
            // With pagination number.
            // .open(player, 2)
            // Default open method.
            .open(player);

    }

}
```
