<img src="logo/logo.svg" width="92px"/>

[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

![master](https://github.com/portlek/SmartInventory/workflows/build/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/SmartInventory?label=version)](https://repo1.maven.org/maven2/io/github/portlek/SmartInventory/)

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

## Getting Started

### Registering the library

```java
final class Main extends JavaPlugin {

  private final SmartInventory inventory = new BasicSmartInventory(this);

  @Override
  public void onEnable() {
    this.inventory.init();
    new SomeClassesThatNeedSmartInventory(this.inventory).foo();
    new SomeOtherClasses(this.inventory).foo();
  }
}
```

### Creating a Inventory Provider Class

```java
final class ExampleInventoryProvider implements InventoryProvider {

  @Override
  public void init(@NotNull final InventoryContents contents) {
    // Runs when the page opens first.
    // An icon that which is empty(air).
    final Icon empty = Icon.EMPTY;
    // An icon that has not any effect.
    final Icon noEffect = Icon.from(new ItemStack(Material.DIAMOND));
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
      .canSee(cont -> {
        // If it's returning false, player will see the fallback icon on the page.
        return false;
      })
      .fallback(new ItemStack(Material.AIR))
      .canUse(cont -> {
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
    for (int index = 0; index < icons.length; index++) {
      icons[index] = Icon.cancel(new ItemStack(Material.CHORUS_FRUIT, index));
    }
    pagination.setIcons(icons);
    pagination.setIconsPerPage(7);
    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));
    final Page page = contents.page();
    final Player player = contents.player();
    final Icon previousArrow = Icon.click(new ItemStack(Material.ARROW), clickEvent ->
      page.open(player, pagination.previous().getPage()));
    final Icon nextArrow = Icon.click(new ItemStack(Material.ARROW), clickEvent ->
      page.open(player, pagination.next().getPage()));
    contents.set(2, 3, previousArrow);
    contents.set(2, 5, nextArrow);
    // And other tons of methods will help you to make a awesome pages :)
  }

  @Override
  public void tick(@NotNull final InventoryContents contents) {
    // Runs every tick.
    // You have options that;
    // -> make it async or not (default is false)
    // -> set the tick's start delay (default is 1L)
    // -> set the tick period (default is 1L)
  }

  @Override
  public void update(@NotNull final InventoryContents contents) {
    // Runs when the notify update method called by you.
    // SmartInventory#notifyUpdate(Player)
    // -> Finds the player's page, if it's open, runs the update method.
    // InventoryContents#notifyUpdate()
    // -> Runs the update method of this class.
  }
}
```

### Creating a Page

```java
final class CreateAPage {

  @NotNull
  final SmartInventory inventory;

  @NotNull
  final InventoryProvider provider;

  CreateAPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvider provider) {
    this.inventory = inventory;
    this.provider = provider;
  }

  void open(@NotNull final Page parentPage, @NotNull final Player player) {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("test-key", player.getName());
    Page.build(this.inventory, this.provider)
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
      }, Arrays.asList(
        openEvent ->
          openEvent.contents().player().getName().equals("Player"),
        openEvent ->
          openEvent.contents().player().hasPermission("test.perm")
      ))
      // Runs after the page closed. If predicates cannot passed, the consumer won't run.
      .whenClose(closeEvent -> {
        closeEvent.contents().player().sendMessage("The page closed.");
        closeEvent.contents().player().sendMessage("This message will send to Player.");
      }, Arrays.asList(
        closeEvent ->
          closeEvent.contents().player().getName().equals("Player"),
        closeEvent ->
          closeEvent.contents().player().hasPermission("test.perm")
      ))
      // Opens the page for the player.
      // With properties.
      // You can get the properties with
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

  void openAnEmptyPage(@NotNull final Player player) {
    Page.build(this.inventory, this.provider)
      .title("Title")
      .row(3)
      .open(player);
  }
}
```

## Useful libraries with SmartInventory

### Simple Bukkit item builder library with builder pattern.

[BukkitItemBuilder](https://github.com/portlek/BukkitItemBuilder)

### You can get inputs from players via chat.

[Input](https://github.com/portlek/input)
