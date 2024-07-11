package io.github.gdrfgdrf.cutetrade.page

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
            for (i in 0 until 9) {
                page.slots[45 + i] = whilePane
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