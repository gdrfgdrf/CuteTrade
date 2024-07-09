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