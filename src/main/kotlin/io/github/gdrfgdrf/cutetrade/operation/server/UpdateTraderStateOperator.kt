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

package io.github.gdrfgdrf.cutetrade.operation.server

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import net.minecraft.server.network.ServerPlayerEntity

object UpdateTraderStateOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val traderState = TraderState.valueOf(args?.get(0) as String)

        val sender = context.sender as ServerPlayerEntity
        val currentTrade = sender.currentTrade()
        if (!sender.checkInTrade()) {
            return
        }

        if (sender.isRed()) {
            currentTrade!!.updateRedState(traderState)
        } else {
            currentTrade!!.updateBlueState(traderState)
        }
    }

    override fun getName(): String = Operators.SERVER_UPDATE_TRADER_STATE
}