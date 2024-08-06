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

import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.common.extension.currentTrade
import io.github.gdrfgdrf.cutetrade.common.extension.translationScope
import io.github.gdrfgdrf.cutetrade.common.manager.ProtobufPlayerManager
import io.github.gdrfgdrf.cutetrade.common.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.common.manager.TradeRequestManager
import io.github.gdrfgdrf.cutetrade.common.pool.PlayerProxyPool
import io.github.gdrfgdrf.cutetrade.common.proxy.PlayerProxy
import io.github.gdrfgdrf.cutetrade.common.trade.TradeContext
import io.github.gdrfgdrf.cutetrade.common.trade.TradeRequest
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

fun ServerCommandSource.findPlayerProxy(): PlayerProxy? {
    if (this.player == null) {
        return null
    }
    return PlayerProxyPool.getPlayerProxy(this.player!!.name.string)
}

fun ServerCommandSource.isConsole(): Boolean {
    return this.player == null && this.hasPermissionLevel(3)
}