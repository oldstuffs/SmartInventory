/*
 * MIT License
 *
 * Copyright (c) 2020 Hasan Demirtaş
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

package io.github.portlek.smartinventory.page;

import io.github.portlek.observer.Source;
import io.github.portlek.observer.source.BasicSource;
import io.github.portlek.smartinventory.Handle;
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.InventoryOpener;
import io.github.portlek.smartinventory.InventoryProvider;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartHolder;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.content.BasicInventoryContents;
import io.github.portlek.smartinventory.event.PgCloseEvent;
import io.github.portlek.smartinventory.event.PgInitEvent;
import io.github.portlek.smartinventory.event.PgUpdateEvent;
import io.github.portlek.smartinventory.event.abs.CloseEvent;
import io.github.portlek.smartinventory.event.abs.PageEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an implementation for {@link Page}.
 */
public final class BasicPage implements Page {

  /**
   * the handles.
   */
  private final Collection<Handle<? extends PageEvent>> handles = new ArrayList<>();

  /**
   * the inventory manager.
   */
  @NotNull
  private final SmartInventory inventory;

  /**
   * the observer's source.
   */
  private final Source<InventoryContents> source = new BasicSource<>();

  /**
   * the inventory type.
   *
   * @todo #1:5m Add a method to change the type of the inventory.
   */
  @NotNull
  private final InventoryType type = InventoryType.CHEST;

  /**
   * the async.
   */
  private boolean async = false;

  /**
   * the can close.
   */
  @NotNull
  private Predicate<CloseEvent> canClose = event -> true;

  /**
   * the column.
   */
  private int column = 9;

  /**
   * the id.
   */
  @NotNull
  private String id = "none";

  /**
   * the parent.
   */
  @Nullable
  private Page parent;

  /**
   * the provider.
   */
  @NotNull
  private InventoryProvider provider;

  /**
   * the row.
   */
  private int row = 1;

  /**
   * the start delay time.
   */
  private long startDelay = 1L;

  /**
   * the tick time.
   */
  private long tick = 1L;

  /**
   * the tick enable.
   */
  private boolean tickEnable = true;

  /**
   * the title.
   */
  @NotNull
  private String title = "Smart Inventory";

  /**
   * ctor.
   *
   * @param inventory the inventory.
   * @param provider the provider.
   */
  public BasicPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvider provider) {
    this.inventory = inventory;
    this.provider = provider;
  }

  /**
   * ctor.
   *
   * @param inventory the inventory.
   */
  public BasicPage(@NotNull final SmartInventory inventory) {
    this(inventory, InventoryProvider.EMPTY);
  }

  @Override
  public <T extends PageEvent> void accept(@NotNull final T event) {
    this.handles.stream()
      .filter(handle -> handle.type().isAssignableFrom(event.getClass()))
      .map(handle -> (Handle<T>) handle)
      .forEach(handle -> handle.accept(event));
  }

  @Override
  public boolean async() {
    return this.async;
  }

  @NotNull
  @Override
  public Page async(final boolean async) {
    this.async = async;
    return this;
  }

  @Override
  public boolean canClose(@NotNull final CloseEvent event) {
    return this.canClose.test(event);
  }

  @NotNull
  @Override
  public Page canClose(@NotNull final Predicate<CloseEvent> predicate) {
    this.canClose = predicate;
    return this;
  }

  @Override
  public void close(@NotNull final Player player) {
    final InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
    if (!(holder instanceof SmartHolder)) {
      return;
    }
    final SmartHolder smartHolder = (SmartHolder) holder;
    this.accept(new PgCloseEvent(smartHolder.getContents()));
    this.inventory().stopTick(player.getUniqueId());
    this.source.unsubscribe(this.provider());
    player.closeInventory();
  }

  @Override
  public int column() {
    return this.column;
  }

  @NotNull
  @Override
  public Page column(final int column) {
    this.column = column;
    return this;
  }

  @NotNull
  @Override
  public <T extends PageEvent> Page handle(@NotNull final Handle<T> handle) {
    this.handles.add(handle);
    return this;
  }

  @NotNull
  @Override
  public Page id(@NotNull final String id) {
    this.id = id;
    return this;
  }

  @NotNull
  @Override
  public String id() {
    return this.id;
  }

  @NotNull
  @Override
  public SmartInventory inventory() {
    return this.inventory;
  }

  @Override
  public void notifyUpdate(@NotNull final InventoryContents contents) {
    this.accept(new PgUpdateEvent(contents));
    this.source.notifyTargets(contents);
  }

  @NotNull
  @Override
  public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties,
                        final boolean close) {
    if (close) {
      this.close(player);
    }
    final InventoryOpener opener = this.inventory().findOpener(this.type).orElseThrow(() ->
      new IllegalStateException("No opener found for the inventory type " + this.type.name()));
    this.source.subscribe(this.provider());
    final InventoryContents contents = new BasicInventoryContents(this, player);
    contents.pagination().page(page);
    properties.forEach(contents::setProperty);
    this.accept(new PgInitEvent(contents));
    this.provider().init(contents);
    final Inventory opened = opener.open(contents);
    if (this.tickEnable()) {
      this.inventory().tick(player.getUniqueId(), this);
    }
    return opened;
  }

  @NotNull
  @Override
  public Optional<Page> parent() {
    return Optional.ofNullable(this.parent);
  }

  @NotNull
  @Override
  public Page parent(@NotNull final Page parent) {
    this.parent = parent;
    return this;
  }

  @NotNull
  @Override
  public InventoryProvider provider() {
    return this.provider;
  }

  @NotNull
  @Override
  public Page provider(@NotNull final InventoryProvider provider) {
    this.provider = provider;
    return this;
  }

  @Override
  public int row() {
    return this.row;
  }

  @NotNull
  @Override
  public Page row(final int row) {
    this.row = row;
    return this;
  }

  @Override
  public long startDelay() {
    return this.startDelay;
  }

  @NotNull
  @Override
  public Page startDelay(final long startDelay) {
    this.startDelay = startDelay;
    return this;
  }

  @Override
  public long tick() {
    return this.tick;
  }

  @NotNull
  @Override
  public Page tick(final long tick) {
    this.tick = tick;
    return this;
  }

  @Override
  public boolean tickEnable() {
    return this.tickEnable;
  }

  @NotNull
  @Override
  public Page tickEnable(final boolean tickEnable) {
    this.tickEnable = tickEnable;
    return this;
  }

  @NotNull
  @Override
  public String title() {
    return this.title;
  }

  @NotNull
  @Override
  public Page title(@NotNull final String title) {
    this.title = title;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.handles, this.inventory, this.source, this.type, this.async, this.canClose, this.column,
      this.id, this.parent, this.provider, this.row, this.startDelay, this.tick, this.tickEnable, this.title);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final BasicPage page = (BasicPage) obj;
    return this.async == page.async &&
      this.column == page.column &&
      this.row == page.row &&
      this.startDelay == page.startDelay &&
      this.tick == page.tick &&
      this.tickEnable == page.tickEnable &&
      Objects.equals(this.handles, page.handles) &&
      this.inventory.equals(page.inventory) &&
      Objects.equals(this.source, page.source) &&
      this.type == page.type &&
      this.canClose.equals(page.canClose) &&
      this.id.equals(page.id) &&
      Objects.equals(this.parent, page.parent) &&
      this.provider.equals(page.provider) &&
      this.title.equals(page.title);
  }
}
