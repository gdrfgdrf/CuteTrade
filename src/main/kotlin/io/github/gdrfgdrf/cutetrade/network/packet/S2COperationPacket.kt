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

package io.github.gdrfgdrf.cutetrade.network.packet

import io.github.gdrfgdrf.cutetrade.common.extension.getProxyFactory
import io.github.gdrfgdrf.cutetrade.common.impl.ItemStackProxyImpl
import io.github.gdrfgdrf.cutetrade.common.network.interfaces.PacketAdapter
import io.github.gdrfgdrf.cutetrade.common.proxy.ItemStackProxy
import io.github.gdrfgdrf.cutetrade.common.proxy.PacketByteBufProxy
import io.github.gdrfgdrf.cutetrade.common.Constants
import io.github.gdrfgdrf.cutetrade.extension.registryManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.OperationDispatcher
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload

class S2COperationPacket : CustomPayload, PacketAdapter {
    val operatorName: String
    var stringArgsLength: Int = -1
    var stringArgs: Array<String?>? = null
        set(value) {
            field = value
            stringArgsLength = stringArgs?.size!!
        }

    var intArgsLength: Int = -1
    var intArgs: Array<Int?>? = null
        set(value) {
            field = value
            intArgsLength = intArgs?.size!!
        }

    var itemStackArgsLength: Int = -1
    var itemStackArgs: Array<ItemStack?>? = null
        set(value) {
            field = value
            itemStackArgsLength = itemStackArgs?.size!!
        }

    constructor(operatorName: String) {
        this.operatorName = operatorName
    }

    constructor(byteBuf: PacketByteBuf) {
        this.operatorName = byteBuf.readString()
        this.stringArgsLength = byteBuf.readInt()
        this.intArgsLength = byteBuf.readInt()
        this.itemStackArgsLength = byteBuf.readInt()

        if (stringArgsLength > 0) {
            stringArgs = arrayOfNulls(stringArgsLength)
            for (i in 0 until stringArgsLength) {
                stringArgs!![i] = byteBuf.readString()
            }
        }
        if (intArgsLength > 0) {
            intArgs = arrayOfNulls(intArgsLength)
            for (i in 0 until intArgsLength) {
                intArgs!![i] = byteBuf.readInt()
            }
        }
        if (itemStackArgsLength > 0) {
            itemStackArgs = arrayOfNulls(itemStackArgsLength)
            for (i in 0 until itemStackArgsLength) {
                val nbtCompound = byteBuf.readNbt()
                itemStackArgs!![i] = ItemStack.fromNbtOrEmpty(registryManager(), nbtCompound)
            }
        }
    }

    override fun write(byteBuf: PacketByteBufProxy) {
        byteBuf.writeString(operatorName)
        byteBuf.writeInt(stringArgsLength)
        byteBuf.writeInt(intArgsLength)
        byteBuf.writeInt(itemStackArgsLength)

        if (stringArgsLength > 0) {
            stringArgs!!.forEach {
                it ?: return@forEach
                byteBuf.writeString(it)
            }
        }
        if (intArgsLength > 0) {
            intArgs!!.forEach {
                it ?: return@forEach
                byteBuf.writeInt(it)
            }
        }
        if (itemStackArgsLength > 0) {
            val factory = getProxyFactory()

            itemStackArgs!!.forEach {
                val nbtCompound = it?.encode(registryManager()) ?: return@forEach

                val nbtProxy = factory.newNbt()
                nbtProxy.nbt = nbtCompound

                byteBuf.writeNbt(nbtProxy)
            }
        }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> {
        return ID
    }

    companion object {
        val CODEC = CustomPayload.codecOf(S2COperationPacket::write, ::S2COperationPacket)
        val ID = CustomPayload.Id<S2COperationPacket>(Constants.S2C_OPERATION)

        @Environment(EnvType.CLIENT)
        fun handle(context: PacketContext<S2COperationPacket>) {
            val packet = context.message
            val operatorName = packet.operatorName
            val stringArgsLength = packet.stringArgsLength
            val intArgsLength = packet.intArgsLength
            val itemStackArgsLength = packet.itemStackArgsLength

            if (stringArgsLength > 0 && intArgsLength > 0 && itemStackArgsLength > 0) {
                OperationDispatcher.dispatch(operatorName, context, arrayOf(packet.stringArgs, packet.intArgs, packet.itemStackArgs))
                return
            }
            if (stringArgsLength > 0 && intArgsLength > 0 && itemStackArgsLength <= 0) {
                OperationDispatcher.dispatch(operatorName, context, arrayOf(packet.stringArgs, packet.intArgs))
                return
            }
            if (stringArgsLength > 0 && intArgsLength <= 0 && itemStackArgsLength > 0) {
                OperationDispatcher.dispatch(operatorName, context, arrayOf(packet.stringArgs, packet.itemStackArgs))
                return
            }
            if (stringArgsLength > 0 && intArgsLength <= 0 && itemStackArgsLength <= 0) {
                OperationDispatcher.dispatch(operatorName, context, packet.stringArgs)
                return
            }

            if (stringArgsLength <= 0 && intArgsLength > 0 && itemStackArgsLength > 0) {
                OperationDispatcher.dispatch(operatorName, context, arrayOf(packet.intArgs, packet.itemStackArgs))
                return
            }
            if (stringArgsLength <= 0 && intArgsLength > 0 && itemStackArgsLength <= 0) {
                OperationDispatcher.dispatch(operatorName, context, packet.intArgs)
            }
            if (stringArgsLength <= 0 && intArgsLength <= 0 && itemStackArgsLength > 0) {
                OperationDispatcher.dispatch(operatorName, context, packet.itemStackArgs)
            }
            if (stringArgsLength <= 0 && intArgsLength <= 0 && itemStackArgsLength <= 0) {
                OperationDispatcher.dispatch(operatorName, context, null)
            }
        }
    }

    override fun getOperatorName(): String = operatorName

    override fun getStringArgs(): Array<String?>? = stringArgs

    override fun setStringArgs(args: Array<String?>?) {
        this.stringArgs = args
    }

    override fun getIntArgs(): Array<Int?>? = intArgs

    override fun setIntArgs(args: Array<Int?>?) {
        this.intArgs = args
    }

    override fun getItemStackArgs(): Array<ItemStackProxy?>? {
        if (itemStackArgs == null) {
            return null
        }

        val result = arrayOfNulls<ItemStackProxy>(itemStackArgsLength)
        itemStackArgs!!.forEachIndexed { index, itemStack ->
            itemStack ?: return@forEachIndexed
            val impl = ItemStackProxyImpl.create(itemStack)
            result[index] = impl
        }

        return result
    }

    override fun setItemStackArgs(args: Array<ItemStackProxy?>?) {
        if (args == null) {
            return
        }

        val result = arrayOfNulls<ItemStack>(args.size)
        args.forEachIndexed { index, itemStackProxy ->
            itemStackProxy ?: return@forEachIndexed
            result[index] = itemStackProxy.itemStack as ItemStack
        }

        this.itemStackArgs = result
    }
}