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
import io.github.gdrfgdrf.cutetrade.common.enums.TraderState
import io.github.gdrfgdrf.cutetrade.extension.createIdentifier
import io.github.gdrfgdrf.cutetrade.base.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.base.screen.handler.TradeScreenHandler
import io.github.gdrfgdrf.cutetrade.base.trade.ClientTradeContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.atan

@Environment(EnvType.CLIENT)
class TradeScreen(
    screenHandler: ScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
): HandledScreen<ScreenHandler>(
        screenHandler,
        playerInventory,
        title
    ) {

    private lateinit var tradeScreenHandler: TradeScreenHandler

    private var tradeContext: ClientTradeContext? = null
    private var own: AbstractClientPlayerEntity? = null
    private var other: AbstractClientPlayerEntity? = null

    private var mouseX = 0f
    private var mouseY = 0f

    private val ownState: ButtonWidget? = ButtonWidget.builder(Text.of("⨉")) {
        if (it.message.string == "√") {
            tradeContext?.sendTraderStateToServer(TraderState.UNCHECKED)
        } else {
            tradeContext?.sendTraderStateToServer(TraderState.CHECKED)
        }
    }.size(16, 16).build()

    override fun init() {
        tradeScreenHandler = this.screenHandler as TradeScreenHandler

        tradeContext = ClientTradeManager.currentTrade
        client = MinecraftClient.getInstance()
        own = client!!.player

        val ownName = client!!.player!!.name.string
        val otherSideName = if (ownName.equals(tradeContext!!.redName)) {
            tradeContext!!.blueName
        } else {
            tradeContext!!.redName
        }

        other = client!!.world!!.players!!.stream().filter {
            return@filter otherSideName == it.name.string
        }.findAny().orElse(null)

        this.x = (this.width - BACKGROUND_WIDTH) / 2
        this.y = ((this.height - BACKGROUND_HEIGHT) / 2)
        titleX = ((BACKGROUND_HEIGHT - textRenderer.getWidth(title)) / 2)

        ownState!!.setPosition((width - ownState.width) / 2 - 74, (height - ownState.height) / 2 - 39)
        addDrawableChild(ownState)

        val textWidget = TextWidget(title, client!!.textRenderer)
        textWidget.setTextColor(0x555555)
        textWidget.setPosition((width - textWidget.width) / 2, height - textWidget.height)
        addDrawable(textWidget)
    }

    override fun render(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        renderBackground(context, mouseX, mouseY, delta)

        super.render(context, mouseX, mouseY, delta)

        drawMouseoverTooltip(context, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()

        if (tradeContext!!.ownState == TraderState.CHECKED) {
            ownState!!.message = Text.of("√")
        } else {
            ownState!!.message = Text.of("⨉")
        }

        val otherState = if (tradeContext!!.otherState == TraderState.CHECKED) {
            Text.of("√")
        } else {
            Text.of("⨉")
        }

        context!!.drawText(
            client!!.textRenderer,
            otherState,
            (width - textRenderer.getWidth(otherState.asOrderedText())) / 2 + 73,
            (height - 9) / 2 - 39,
            0xFFFFFF,
            true
        )
    }

    override fun drawForeground(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int
    ) {

    }

    override fun drawBackground(
        context: DrawContext?,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        context!!.drawTexture(
            TRADE_PNG,
            width / 2 - 50,
            20,
            1F,
            1F,
            100,
            30,
            100,
            30
        )
        context.drawTexture(
            TEXTURE,
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
            context,
            this.x + 32,
            this.y + 110,
            (this.x + 32).toFloat() - this.mouseX,
            (this.y + 110 - 50).toFloat() - this.mouseY,
            client!!.player!!
        )
        if (other != null) {
            drawEntity(
                context,
                this.x + 32 + 225,
                this.y + 110,
                (this.x + 257).toFloat() - this.mouseX,
                (this.y + 110 - 50).toFloat() - this.mouseY,
                other!!
            )
        }
    }

    // Copy from net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(net.minecraft.client.gui.DrawContext, int, int, int, float, float, net.minecraft.entity.LivingEntity)
    private fun drawEntity(
        context: DrawContext,
        x: Int,
        y: Int,
        mouseX: Float,
        mouseY: Float,
        entity: LivingEntity
    ) {
        val f = atan((mouseX / 40.0f).toDouble()).toFloat()
        val g = atan((mouseY / 40.0f).toDouble()).toFloat()
        val quaternionf = Quaternionf().rotateZ(3.1415927f)
        val quaternionf2 = Quaternionf().rotateX(g * 20.0f * 0.017453292f)
        quaternionf.mul(quaternionf2)
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
        drawEntity(context, x, y, quaternionf, quaternionf2, entity)
        entity.bodyYaw = h
        entity.yaw = i
        entity.pitch = j
        entity.prevHeadYaw = k
        entity.headYaw = l
    }

    // Copy from net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(net.minecraft.client.gui.DrawContext, int, int, int, org.joml.Quaternionf, org.joml.Quaternionf, net.minecraft.entity.LivingEntity)
    @Suppress("DEPRECATION")
    private fun drawEntity(
        context: DrawContext,
        x: Int,
        y: Int,
        quaternionf: Quaternionf?,
        quaternionf2: Quaternionf?,
        entity: LivingEntity
    ) {
        context.matrices.push()
        context.matrices.translate(x.toDouble(), y.toDouble(), 50.0)
        context.matrices.multiplyPositionMatrix(Matrix4f().scaling(
            30F,
            30F,
            -30F
        ))
        context.matrices.multiply(quaternionf)
        DiffuseLighting.method_34742()
        val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
        if (quaternionf2 != null) {
            quaternionf2.conjugate()
            entityRenderDispatcher.rotation = quaternionf2
        }

        entityRenderDispatcher.setRenderShadows(false)
        RenderSystem.runAsFancy {
            entityRenderDispatcher.render(
                entity,
                0.0,
                0.0,
                0.0,
                0.0f,
                1.0f,
                context.matrices,
                context.vertexConsumers,
                15728880
            )
        }
        context.draw()
        entityRenderDispatcher.setRenderShadows(true)
        context.matrices.pop()
        DiffuseLighting.enableGuiDepthLighting()
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        return false
    }

    override fun close() {
        client!!.player!!.networkHandler?.sendChatCommand("trade-public end-trade")
    }

    companion object {
        private const val BACKGROUND_WIDTH = 290
        private const val BACKGROUND_HEIGHT = 166

        val TRADE_PNG = createIdentifier("cutetrade", "textures/trade_3d.png")
        val TEXTURE = createIdentifier("cutetrade", "textures/trade_inventory.png")
    }

}