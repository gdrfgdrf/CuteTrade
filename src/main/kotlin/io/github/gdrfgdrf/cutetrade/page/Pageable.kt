/*
 * Copyright 2024 CuteTrade's contributors
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

package io.github.gdrfgdrf.cutetrade.page

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class Pageable {
    var pageableScreenHandler: PageableScreenHandler? = null

    var inventory: PageableInventory? = null
    private var latestPageIndex: Int = 0
    private var itemCount = 0

    fun openScreen(displayName: Text, serverPlayerEntity: ServerPlayerEntity) {
        val factory = PageableScreenHandlerFactory(displayName)
        serverPlayerEntity.openHandledScreen(factory)
        pageableScreenHandler = serverPlayerEntity.currentScreenHandler as PageableScreenHandler
        inventory = pageableScreenHandler!!.inventory
    }

    fun addItemStack(itemStack: ItemStack) {
        if (itemCount == 53) {
            itemCount = 0
            latestPageIndex++
        }
        val pageSize = inventory?.navigator?.pages?.size
        if (latestPageIndex >= pageSize!!) {
            inventory?.addPage()
        }

        val page = inventory?.navigator?.pages?.get(latestPageIndex)

        page!!.slots[itemCount] = itemStack
        itemCount++
    }
}