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

package io.github.gdrfgdrf.cutetrade.screen

import com.mojang.blaze3d.systems.RenderSystem
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import kotlin.math.atan

class DevScreen(
    handler: ScreenHandler,
    inventory: PlayerInventory,
    title: Text,
) : HandledScreen<ScreenHandler>(
    handler,
    inventory,
    title
) {
    private var mouseX = 0f
    private var mouseY = 0f

    override fun init() {
        this.x = (this.width - BACKGROUND_WIDTH) / 2
        this.y = ((this.height - BACKGROUND_HEIGHT) / 2)
        titleX = ((BACKGROUND_HEIGHT - textRenderer.getWidth(title)) / 2)
    }

    override fun onMouseClick(slot: Slot?, slotId: Int, button: Int, actionType: SlotActionType?) {
        "onMouseClick ${slot?.index} | $slotId | $button | $actionType".logInfo()
        super.onMouseClick(slot, slotId, button, actionType)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        "mouseClicked $mouseX | $mouseY | $button".logInfo()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(context: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
    }

    override fun drawForeground(context: MatrixStack?, mouseX: Int, mouseY: Int) {

    }

    override fun drawBackground(context: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        DrawableHelper.drawTexture(
            context,
            x,
            y,
            0F,
            0F,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT
        )
        drawEntity(
            this.x + 32,
            this.y + 110,
            (this.x + 32).toFloat() - this.mouseX,
            (this.y + 110 - 50).toFloat() - this.mouseY,
            client!!.player!!
        )
        drawEntity(
            this.x + 32 + 225,
            this.y + 110,
            (this.x + 257).toFloat() - this.mouseX,
            (this.y + 110 - 50).toFloat() - this.mouseY,
            client!!.player!!
        )
    }

    @Suppress("DEPRECATION")
    private fun drawEntity(x: Int, y: Int, mouseX: Float, mouseY: Float, entity: LivingEntity) {
        val f = atan((mouseX / 40.0f).toDouble()).toFloat()
        val g = atan((mouseY / 40.0f).toDouble()).toFloat()
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.translate(x.toFloat(), y.toFloat(), 1050.0f)
        matrixStack.scale(1.0f, 1.0f, -1.0f)
        RenderSystem.applyModelViewMatrix()
        val matrixStack2 = MatrixStack()
        matrixStack2.translate(0.0f, 0.0f, 1000.0f)
        matrixStack2.scale(30F, 30F, 30F)
        val quaternionf = Quaternionf().rotateZ(3.1415927f)
        val quaternionf2 = Quaternionf().rotateX(g * 20.0f * 0.017453292f)
        quaternionf.mul(quaternionf2)
        matrixStack2.multiply(quaternionf)
        val h = entity.bodyYaw
        val i = entity.yaw
        val j = entity.pitch
        val k = entity.prevHeadYaw
        val l = entity.headYaw
        entity.bodyYaw = 180.0f + f * 20.0f
        entity.yaw = 180.0f + f * 40.0f
        entity.pitch = -g * 20.0f
        entity.headYaw = entity.yaw
        entity.prevHeadYaw = entity.yaw
        DiffuseLighting.method_34742()
        val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
        quaternionf2.conjugate()
        entityRenderDispatcher.rotation = quaternionf2
        entityRenderDispatcher.setRenderShadows(false)
        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        RenderSystem.runAsFancy {
            entityRenderDispatcher.render(
                entity,
                0.0,
                0.0,
                0.0,
                0.0f,
                1.0f,
                matrixStack2,
                immediate,
                15728880
            )
        }
        immediate.draw()
        entityRenderDispatcher.setRenderShadows(true)
        entity.bodyYaw = h
        entity.yaw = i
        entity.pitch = j
        entity.prevHeadYaw = k
        entity.headYaw = l
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        return false
    }

    companion object {
        private val TEXTURE: Identifier = Identifier("cutetrade", "textures/trade_inventory.png")
        private const val BACKGROUND_WIDTH = 290
        private const val BACKGROUND_HEIGHT = 166
    }
}