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

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class PageableScreen(
    screenHandler: PageableScreenHandler,
    playerInventory: PlayerInventory,
    title: Text,
): HandledScreen<PageableScreenHandler>(
    screenHandler,
    playerInventory,
    title
) {
    private var rows: Int? = -1

    init {
        rows = screenHandler.inventory!!.rows
        this.backgroundHeight = 114 + this.rows!! * 18
        this.playerInventoryTitleY = this.backgroundHeight - 94
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        this.drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun drawBackground(context: DrawContext?, delta: Float, mouseX: Int, mouseY: Int) {
        val i = (this.width - this.backgroundWidth) / 2
        val j = (this.height - this.backgroundHeight) / 2
        context!!.drawTexture(
            TEXTURE, i, j, 0, 0, this.backgroundWidth,
            rows!! * 18 + 17
        )
        context.drawTexture(
            TEXTURE,
            i,
            j + rows!! * 18 + 17,
            0,
            126,
            this.backgroundWidth,
            96
        )
    }

    companion object {
        val TEXTURE = Identifier("textures/gui/container/generic_54.png")
    }
}