package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.common.Constants
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

@Environment(EnvType.SERVER)
fun Identifier.sendPacket(
    serverPlayerEntity: ServerPlayerEntity,
    writer: (PacketByteBuf) -> Unit
) {
    val byteBuf = PacketByteBufs.create()
    writer(byteBuf)
    ServerPlayNetworking.send(serverPlayerEntity, this, byteBuf)
}

fun sendOperationPacket(
    serverPlayerEntity: ServerPlayerEntity,
    writer: (PacketByteBuf) -> Unit
) {
    if (!serverPlayerEntity.isDisconnected) {
        Constants.S2C_OPERATION.sendPacket(serverPlayerEntity, writer)
    }
}