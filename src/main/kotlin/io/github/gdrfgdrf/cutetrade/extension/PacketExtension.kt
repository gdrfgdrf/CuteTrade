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