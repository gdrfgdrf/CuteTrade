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

import io.github.gdrfgdrf.cutetrade.common.impl.PacketByteBufProxyImpl
import io.github.gdrfgdrf.cutetrade.common.proxy.PacketByteBufProxy
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

fun Identifier.sendPacket(
    writer: (PacketByteBufProxy) -> Unit
) {
    val byteBuf = PacketByteBufs.create()
    val packetByteBufProxy = PacketByteBufProxyImpl.create(byteBuf)
    writer(packetByteBufProxy)
    ClientPlayNetworking.send(this, byteBuf)
}