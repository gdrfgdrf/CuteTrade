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
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.screen.handler.TradeScreenHandler
import io.github.gdrfgdrf.cutetrade.trade.ClientTradeContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import kotlin.math.atan


@Environment(EnvType.CLIENT)
class TradeScreen(
    screenHandler: ScreenHandler,
    playerInventory: PlayerInventory,
    title: Text,
): HandledScreen<ScreenHandler>(
        screenHandler,
        playerInventory,
        title
    ) {

    private var titleWidth: Int = 0

    private lateinit var tradeScreenHandler: TradeScreenHandler

    private var tradeContext: ClientTradeContext? = null
    private var own: AbstractClientPlayerEntity? = null
    private var other: AbstractClientPlayerEntity? = null

    private var mouseX = 0f
    private var mouseY = 0f

    private val ownState: ButtonWidget = ButtonWidget.builder(Text.of("⨉")) {
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

        tradeContext?.clientTradeScreenContext?.tradeScreen = this

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

        ownState.x = (width - ownState.width) / 2 - 74
        ownState.y = (height - ownState.height) / 2 - 39
        addDrawableChild(ownState)

        titleWidth = textRenderer.getWidth(title.asOrderedText())
    }

    override fun render(
        context: MatrixStack?,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
    ) {
        renderBackground(context)

        super.render(context, mouseX, mouseY, delta)

        drawMouseoverTooltip(context, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()

        if (tradeContext!!.ownState == TraderState.CHECKED) {
            ownState.message = Text.of("√")
        } else {
            ownState.message = Text.of("⨉")
        }

        val otherState = if (tradeContext!!.otherState == TraderState.CHECKED) {
            Text.of("√")
        } else {
            Text.of("⨉")
        }

        textRenderer.draw(
            context,
            otherState,
            ((width - textRenderer.getWidth(otherState.asOrderedText())) / 2 + 73).toFloat(),
            ((height - 9) / 2 - 39).toFloat(),
            0xFFFFFF
        )

        textRenderer.draw(
            context,
            title,
            ((width - titleWidth) / 2).toFloat(),
            (height - 9).toFloat(),
            0x555555
        )
    }

    override fun drawForeground(
        context: MatrixStack?,
        mouseX: Int,
        mouseY: Int,
    ) {

    }

    override fun drawBackground(
        context: MatrixStack?,
        delta: Float,
        mouseX: Int,
        mouseY: Int,
    ) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TRADE_PNG)

        DrawableHelper.drawTexture(
            context,
            width / 2 - 50,
            20,
            1F,
            1F,
            100,
            30,
            100,
            30
        )

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
        if (other != null) {
            drawEntity(
                this.x + 32 + 225,
                this.y + 110,
                (this.x + 257).toFloat() - this.mouseX,
                (this.y + 110 - 50).toFloat() - this.mouseY,
                other!!
            )
        }
    }

    // Copy from net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(net.minecraft.client.gui.DrawContext, int, int, int, float, float, net.minecraft.entity.LivingEntity)
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
            entityRenderDispatcher.render<Entity>(
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

    override fun close() {
        client!!.player!!.networkHandler?.sendChatCommand("trade-public end-trade")
    }

    fun close1() {
        super.close()
    }

    companion object {
        private const val BACKGROUND_WIDTH = 290
        private const val BACKGROUND_HEIGHT = 166

        val TRADE_PNG = Identifier("cutetrade", "textures/trade_3d.png")
        val TEXTURE: Identifier = Identifier("cutetrade", "textures/trade_inventory.png")
    }

}