package io.github.gdrfgdrf.cutetrade.screen

import com.mojang.blaze3d.systems.RenderSystem
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.atan


class DevScreen(
    handler: ScreenHandler,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<ScreenHandler>(
    handler,
    inventory,
    title
) {
    private val TEXTURE: Identifier = Identifier("cutetrade", "textures/trade_inventory.png")
    private var mouseX = 0f
    private var mouseY = 0f

    override fun init() {
        this.x = (this.width - DevScreen.backgroundWidth) / 2
        this.y = ((this.height - DevScreen.backgroundHeight) / 2)
        titleX = ((DevScreen.backgroundHeight - textRenderer.getWidth(title)) / 2);
    }

    override fun onMouseClick(slot: Slot?, slotId: Int, button: Int, actionType: SlotActionType?) {
        "${slot?.id} | $slotId | $actionType".logInfo()
        super.onMouseClick(slot, slotId, button, actionType)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        "$button".logInfo()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        "$keyCode".logInfo()
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
    }

    override fun drawForeground(context: DrawContext?, mouseX: Int, mouseY: Int) {

    }

    override fun drawBackground(context: DrawContext?, delta: Float, mouseX: Int, mouseY: Int) {
        context!!.drawTexture(
            TEXTURE,
            x,
            y,
            0F,
            0F,
            DevScreen.backgroundWidth,
            DevScreen.backgroundHeight,
            DevScreen.backgroundWidth,
            DevScreen.backgroundHeight
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
        drawEntity(
            context,
            this.x + 32 + 225,
            this.y + 110,
            30,
            (this.x + 257).toFloat() - this.mouseX,
            (this.y + 110 - 50).toFloat() - this.mouseY,
            client!!.player!!
        )
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
        context.matrices.multiplyPositionMatrix(Matrix4f().scaling(size.toFloat(), size.toFloat(), (-size).toFloat()))
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

    companion object {
        private val backgroundWidth = 290
        private val backgroundHeight = 166
    }
}