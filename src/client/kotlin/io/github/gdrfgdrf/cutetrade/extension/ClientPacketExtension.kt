package io.github.gdrfgdrf.cutetrade.extension

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

fun Identifier.sendPacket(
    writer: (PacketByteBuf) -> Unit
) {
    val byteBuf = PacketByteBufs.create()
    writer(byteBuf)
    ClientPlayNetworking.send(this, byteBuf)
}