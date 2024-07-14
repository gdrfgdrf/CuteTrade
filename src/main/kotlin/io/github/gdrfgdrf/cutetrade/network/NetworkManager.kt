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

package io.github.gdrfgdrf.cutetrade.network

import io.github.gdrfgdrf.cutetrade.common.Constants
import io.github.gdrfgdrf.cutetrade.network.packet.C2SOperationPacket
import io.github.gdrfgdrf.cutetrade.network.packet.S2COperationPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object NetworkManager {
    fun initialize(
        registerPacketInterface: RegisterPacketInterface,
    ) {
        registerPacketInterface.register(
            Constants.S2C_OPERATION,
            S2COperationPacket::class.java,
            S2COperationPacket::write,
            S2COperationPacket::read,
            S2COperationPacket::handle
        )

        registerPacketInterface.register(
            Constants.C2S_OPERATION,
            C2SOperationPacket::class.java,
            C2SOperationPacket::write,
            C2SOperationPacket::read,
            C2SOperationPacket::handle
        )
    }

    interface RegisterPacketInterface {
        fun <T> register(
            packetIdentifier: Identifier,
            messageType: Class<T>,
            encoder: (T, PacketByteBuf) -> Unit,
            decoder: (PacketByteBuf) -> T,
            handler: (PacketContext<T>) -> Unit
        )
    }
}