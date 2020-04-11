/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.minuskube.inv.content;

import org.bukkit.entity.Player;

/**
 * Inventory Provider Interface
 */
public interface InventoryProvider {

    /**
     * Will check for all the contents inside the SmartInvs and get the given player.
     *
     * @param player Get the given player
     * @param contents Get all the contents inside the SmartInvs
     */
    void init(Player player, InventoryContents contents);

    /**
     * Runs a hashmap to update the player with the given contents inside the SmartInvs.
     *
     * @param player Get given player
     * @param contents Get all the contents inside the SmartInvs
     */
    default void update(final Player player, final InventoryContents contents) {
    }

}
