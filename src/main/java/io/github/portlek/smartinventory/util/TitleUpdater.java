/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.portlek.smartinventory.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an utility class that helps to change title of pages.
 */
@SuppressWarnings({"unused"})
public final class TitleUpdater {

  private final static Class<?> CHAT_MESSAGE_CLASS;

  private final static Class<?> CONTAINERS_CLASS;

  private final static Class<?> CONTAINER_CLASS;

  private final static Class<?> CRAFT_PLAYER_CLASS;

  private final static Class<?> ENTITY_PLAYER_CLASS;

  private final static Class<?> I_CHAT_BASE_COMPONENT_CLASS;

  private final static Class<?> PACKET_PLAY_OUT_OPEN_WINDOW_CLASS;

  private static Field activeContainerField;

  private static Constructor<?> chatMessageConstructor;

  private static Method getBukkitView;

  private static Method getHandle;

  private static Constructor<?> packetPlayOutOpenWindowConstructor;

  private static Method updateInventory;

  private static Field windowIdField;

  static {
    CRAFT_PLAYER_CLASS = ReflectionUtils.getCraftClass("entity.CraftPlayer");
    CHAT_MESSAGE_CLASS = ReflectionUtils.getNMSClass("ChatMessage");
    PACKET_PLAY_OUT_OPEN_WINDOW_CLASS = ReflectionUtils.getNMSClass("PacketPlayOutOpenWindow");
    I_CHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("IChatBaseComponent");
    CONTAINERS_CLASS = TitleUpdater.useContainers() ? ReflectionUtils.getNMSClass("Containers") : null;
    ENTITY_PLAYER_CLASS = ReflectionUtils.getNMSClass("EntityPlayer");
    CONTAINER_CLASS = ReflectionUtils.getNMSClass("Container");
    try {
      TitleUpdater.getHandle = TitleUpdater.CRAFT_PLAYER_CLASS.getMethod("getHandle");
      TitleUpdater.getBukkitView = TitleUpdater.CONTAINER_CLASS.getMethod("getBukkitView");
      TitleUpdater.updateInventory = TitleUpdater.ENTITY_PLAYER_CLASS.getMethod("updateInventory", TitleUpdater.CONTAINER_CLASS);
      TitleUpdater.chatMessageConstructor = TitleUpdater.CHAT_MESSAGE_CLASS.getConstructor(String.class, Object[].class);
      TitleUpdater.packetPlayOutOpenWindowConstructor =
        TitleUpdater.useContainers() ?
          TitleUpdater.PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, TitleUpdater.CONTAINERS_CLASS, TitleUpdater.I_CHAT_BASE_COMPONENT_CLASS) :
          TitleUpdater.PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, String.class, TitleUpdater.I_CHAT_BASE_COMPONENT_CLASS, int.class);
      TitleUpdater.activeContainerField = TitleUpdater.ENTITY_PLAYER_CLASS.getField("activeContainer");
      TitleUpdater.windowIdField = TitleUpdater.CONTAINER_CLASS.getField("windowId");
    } catch (final NoSuchMethodException | NoSuchFieldException exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Update the player inventory, so you can change the title.
   *
   * @param player whose inventory will be updated.
   * @param newTitle the new title for the inventory.
   */
  public static void updateInventory(@NotNull final Player player, String newTitle) {
    Objects.requireNonNull(player, "Cannot update inventory to null player");
    try {
      final Object craftPlayer = TitleUpdater.CRAFT_PLAYER_CLASS.cast(player);
      final Object entityPlayer = TitleUpdater.getHandle.invoke(craftPlayer);
      if (newTitle != null && newTitle.length() > 32) {
        newTitle = newTitle.substring(0, 32);
      }
      final Object title = TitleUpdater.chatMessageConstructor.newInstance(newTitle != null ? newTitle : "", new Object[]{});
      final Object activeContainer = TitleUpdater.activeContainerField.get(entityPlayer);
      final Integer windowId = (Integer) TitleUpdater.windowIdField.get(activeContainer);
      final Object bukkitView = TitleUpdater.getBukkitView.invoke(activeContainer);
      if (!(bukkitView instanceof InventoryView)) {
        return;
      }
      final InventoryView view = (InventoryView) bukkitView;
      final InventoryType type = view.getTopInventory().getType();
      if ((type == InventoryType.WORKBENCH || type == InventoryType.ANVIL) && !TitleUpdater.useContainers()) {
        return;
      }
      if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE || type == InventoryType.PLAYER) {
        return;
      }
      final int size = view.getTopInventory().getSize();
      final Containers container = Containers.getType(type, size);
      if (container == null) {
        return;
      }
      if (container.getContainerVersion() > TitleUpdater.getVersion() && TitleUpdater.useContainers()) {
        Bukkit.getLogger().warning("This container doesn't work on your current version.");
        return;
      }
      final Object object;
      if (!TitleUpdater.useContainers() && container == Containers.GENERIC_3X3) {
        object = "minecraft:" + type.name().toLowerCase();
      } else {
        object = container.getObject();
      }
      final Object packet =
        TitleUpdater.useContainers() ?
          TitleUpdater.packetPlayOutOpenWindowConstructor.newInstance(windowId, object, title) :
          TitleUpdater.packetPlayOutOpenWindowConstructor.newInstance(windowId, object, title, size);
      ReflectionUtils.sendPacket(player, packet);
      TitleUpdater.updateInventory.invoke(entityPlayer, activeContainer);
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Get the current version of the server.
   *
   * @return version of the server.
   */
  private static int getVersion() {
    return Integer.parseInt(ReflectionUtils.VERSION.split("_")[1]);
  }

  /**
   * Containers were added in 1.14, a String were used in previous versions.
   *
   * @return whether or not to use containers.
   */
  private static boolean useContainers() {
    return TitleUpdater.getVersion() > 13;
  }

  /**
   * An enum class for the necessaries containers.
   */
  public enum Containers {
    GENERIC_9X1(14, "minecraft:chest", "CHEST"),
    GENERIC_9X2(14, "minecraft:chest", "CHEST"),
    GENERIC_9X3(14, "minecraft:chest", "CHEST", "ENDER_CHEST", "BARREL"),
    GENERIC_9X4(14, "minecraft:chest", "CHEST"),
    GENERIC_9X5(14, "minecraft:chest", "CHEST"),
    GENERIC_9X6(14, "minecraft:chest", "CHEST"),
    GENERIC_3X3(14, null, "DISPENSER", "DROPPER"),
    ANVIL(14, "minecraft:anvil", "ANVIL"),
    BEACON(14, "minecraft:beacon", "BEACON"),
    BREWING_STAND(14, "minecraft:brewing_stand", "BREWING"),
    ENCHANTMENT(14, "minecraft:enchanting_table", "ENCHANTING"),
    FURNACE(14, "minecraft:furnace", "FURNACE"),
    HOPPER(14, "minecraft:hopper", "HOPPER"),
    MERCHANT(14, "minecraft:villager", "MERCHANT"),
    SHULKER_BOX(14, "minecraft:blue_shulker_box", "SHULKER_BOX"),
    BLAST_FURNACE(14, null, "BLAST_FURNACE"),
    CRAFTING(14, null, "WORKBENCH"),
    GRINDSTONE(14, null, "GRINDSTONE"),
    LECTERN(14, null, "LECTERN"),
    LOOM(14, null, "LOOM"),
    SMOKER(14, null, "SMOKER"),
    CARTOGRAPHY_TABLE(14, null, "CARTOGRAPHY"),
    STONECUTTER(14, null, "STONECUTTER"),
    SMITHING(16, null, "SMITHING");

    private final int containerVersion;

    private final String[] inventoryTypesNames;

    private final String minecraftName;

    Containers(final int containerVersion, final String minecraftName, final String... inventoryTypesNames) {
      this.containerVersion = containerVersion;
      this.minecraftName = minecraftName;
      this.inventoryTypesNames = inventoryTypesNames;
    }

    /**
     * Get the container based on the current open inventory of the player.
     *
     * @param type type of inventory.
     * @param size size of the inventory.
     *
     * @return the container.
     */
    @Nullable
    public static Containers getType(final InventoryType type, final int size) {
      if (type == InventoryType.CHEST) {
        return Containers.valueOf("GENERIC_9X" + size / 9);
      }
      for (final Containers container : Containers.values()) {
        for (final String bukkitName : container.getInventoryTypesNames()) {
          if (bukkitName.equalsIgnoreCase(type.toString())) {
            return container;
          }
        }
      }
      return null;
    }

    /**
     * Get the version in which the inventory container was added.
     *
     * @return the version.
     */
    public int getContainerVersion() {
      return this.containerVersion;
    }

    /**
     * Get inventory types names of the inventory.
     *
     * @return bukkit names.
     */
    public String[] getInventoryTypesNames() {
      return this.inventoryTypesNames.clone();
    }

    /**
     * Get the name of the inventory from Minecraft for older versions.
     *
     * @return name of the inventory.
     */
    public String getMinecraftName() {
      return this.minecraftName;
    }

    /**
     * Get the object from the container enum.
     *
     * @return a Containers object if 1.14, otherwise, a String.
     */
    @Nullable
    public Object getObject() {
      try {
        if (!TitleUpdater.useContainers()) {
          return this.getMinecraftName();
        }
        final String name = TitleUpdater.getVersion() == 14 && this == Containers.CARTOGRAPHY_TABLE ? "CARTOGRAPHY" : this.name();
        final Field field = TitleUpdater.CONTAINERS_CLASS.getField(name);
        return field.get(null);
      } catch (final Exception exception) {
        exception.printStackTrace();
      }
      return null;
    }
  }
}