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

import io.github.gdrfgdrf.cutetrade.extension.setCustomName
import io.github.gdrfgdrf.cutetrade.extension.toScreenMessage
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class PageableInventory(val rows: Int) : SimpleInventory(rows * 9) {
    var navigator: Navigator? = null

    fun addPage() {
        val page = Page(rows)
        page.initialize()
        navigator?.pages?.add(page)
    }

    fun fullNavigationBar() {
        val pages = navigator?.pages
        val first = pages?.get(0)
        val last = pages?.get(navigator?.pages?.size!! - 1)

        if (first == last) {
            addNavigationBar(first!!, left = false, right = false)
            return
        } else {
            addNavigationBar(first!!, left = false, right = true)
            addNavigationBar(last!!, left = true, right = false)
        }

        pages.forEach {
            if (it == first || it == last) {
                return@forEach
            }

            addNavigationBar(it, left = true, right = true)
        }
    }

    private fun addNavigationBar(page: Page, left: Boolean, right: Boolean) {
        val previous = ItemStack(Items.LIME_WOOL)
        previous.setCustomName(Text.of("previous_page".toScreenMessage()))

        val next = ItemStack(Items.LIME_WOOL)
        next.setCustomName(Text.of("next_page".toScreenMessage()))

        val redPane = ItemStack(Items.RED_STAINED_GLASS_PANE)
        redPane.setCustomName(Text.of("close".toScreenMessage()))

        val whilePane = ItemStack(Items.WHITE_STAINED_GLASS_PANE)
        whilePane.setCustomName(Text.of(""))

        if (!left && !right) {
            for (i in 0 until 4) {
                page.slots[45 + i] = whilePane
            }

            page.slots[49] = redPane

            for (i in 0 until 4) {
                page.slots[49 + 1 + i] = whilePane
            }
            return
        }
        if (left && right) {
            page.slots[45] = previous

            for (i in 0 until 3) {
                page.slots[45 + 1 + i] = whilePane
            }

            page.slots[49] = redPane

            for (i in 0 until 3) {
                page.slots[49 + 1 + i] = whilePane
            }

            page.slots[53] = next
            return
        }
        if (!left) {
            for (i in 0 until 4) {
                page.slots[45 + i] = whilePane
            }

            page.slots[49] = redPane

            for (i in 0 until 3) {
                page.slots[49 + 1 + i] = whilePane
            }

            page.slots[53] = next
            return
        }
        page.slots[45] = previous

        for (i in 0 until 3) {
            page.slots[45 + 1 + i] = whilePane
        }

        page.slots[49] = redPane

        for (i in 0 until 4) {
            page.slots[49 + 1 + i] = whilePane
        }
    }
}