package io.github.gdrfgdrf.cutetrade.screen

import com.mojang.blaze3d.systems.RenderSystem
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.screen.handler.TradeScreenHandler
import io.github.gdrfgdrf.cutetrade.trade.ClientTradeContext
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
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.atan

@Environment(EnvType.CLIENT)
class TradeScreen(
    screenHandler: ScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) :
    HandledScreen<ScreenHandler>(
        screenHandler,
        playerInventory,
        title
    ) {

    private lateinit var tradeScreenHandler: TradeScreenHandler

    private var tradeContext: ClientTradeContext? = null
    private var own: AbstractClientPlayerEntity? = null
    private var other: AbstractClientPlayerEntity? = null

    private val TRADE_PNG = Identifier("cutetrade", "textures/trade_3d.png")
    private val TEXTURE: Identifier = Identifier("cutetrade", "textures/trade_inventory.png")

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

        this.x = (this.width - TradeScreen.backgroundWidth) / 2
        this.y = ((this.height - TradeScreen.backgroundHeight) / 2)
        titleX = ((TradeScreen.backgroundHeight - textRenderer.getWidth(title)) / 2);

        ownState!!.setPosition((width - ownState.width) / 2 - 74, (height - ownState.height) / 2 - 39)
        addDrawableChild(ownState)

        val textWidget = TextWidget(title, client!!.textRenderer)
        textWidget.setTextColor(0x555555)
        textWidget.setPosition((width - textWidget.width) / 2, height - textWidget.height)
        addDrawable(textWidget)
    }

//    override fun onMouseClick(slot: Slot?, slotId: Int, button: Int, actionType: SlotActionType) {
//        if (slot?.inventory != tradeScreenHandler.inventory) {
//            val index = slot?.index
//            if (index == null || index < 0) {
//                return
//            }
//
//            tradeContext?.addTradeItem(index)
//        } else {
//            val index = slot.index
//            if (index < 0) {
//                return
//            }
//
//            tradeContext?.removeTradeItem(index)
//        }
//    }

//    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
//        if (button == 1) {
//            return true
//        }
//        return super.mouseClicked(mouseX, mouseY, button)
//    }
//
//    override fun mouseDragged(
//        mouseX: Double,
//        mouseY: Double,
//        button: Int,
//        deltaX: Double,
//        deltaY: Double
//    ): Boolean = true
//
//    override fun mouseReleased(
//        mouseX: Double,
//        mouseY: Double,
//        button: Int
//    ): Boolean = true
//
//    override fun mouseScrolled(
//        mouseX: Double,
//        mouseY: Double,
//        amount: Double
//    ): Boolean = true
//
//    override fun keyPressed(
//        keyCode: Int,
//        scanCode: Int,
//        modifiers: Int
//    ): Boolean {
//        if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
//            return true
//        }
//        return super.keyPressed(keyCode, scanCode, modifiers)
//    }
//
//    override fun keyReleased(
//        keyCode: Int,
//        scanCode: Int,
//        modifiers: Int
//    ): Boolean  {
//        if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
//            return true
//        }
//        return super.keyReleased(keyCode, scanCode, modifiers)
//    }

    override fun render(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        renderBackground(context)

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
            TradeScreen.backgroundWidth,
            TradeScreen.backgroundHeight,
            TradeScreen.backgroundWidth,
            TradeScreen.backgroundHeight
        )
        drawEntity(
            context,
            this.x + 32,
            this.y + 110,
            30,
            (this.x + 32).toFloat() - this.mouseX,
            (this.y + 110 - 50).toFloat() - this.mouseY,
            client!!.player!!
        )
        if (other != null) {
            drawEntity(
                context,
                this.x + 32 + 225,
                this.y + 110,
                30,
                (this.x + 257).toFloat() - this.mouseX,
                (this.y + 110 - 50).toFloat() - this.mouseY,
                other!!
            )
        }
    }

    fun drawEntity(
        context: DrawContext,
        x: Int,
        y: Int,
        size: Int,
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
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity)
        entity.bodyYaw = h
        entity.yaw = i
        entity.pitch = j
        entity.prevHeadYaw = k
        entity.headYaw = l
    }

    fun drawEntity(
        context: DrawContext,
        x: Int,
        y: Int,
        size: Int,
        quaternionf: Quaternionf?,
        quaternionf2: Quaternionf?,
        entity: LivingEntity
    ) {
        context.matrices.push()
        context.matrices.translate(x.toDouble(), y.toDouble(), 50.0)
        context.matrices.multiplyPositionMatrix(Matrix4f().scaling(
            size.toFloat(),
            size.toFloat(),
            (-size).toFloat()
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

    fun close1() {
        super.close()
    }

    companion object {
        private val backgroundWidth = 290
        private val backgroundHeight = 166
    }

}