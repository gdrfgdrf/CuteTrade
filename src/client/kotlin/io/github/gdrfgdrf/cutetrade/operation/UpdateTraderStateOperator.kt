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

package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.enums.TraderState
import io.github.gdrfgdrf.cutetrade.common.network.PacketContext
import io.github.gdrfgdrf.cutetrade.common.operation.server.Operators
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.common.operation.base.Operator

object UpdateTraderStateOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val redState = TraderState.valueOf(args!![0] as String)
        val blueState = TraderState.valueOf(args[1] as String)
        ClientTradeManager.currentTrade?.updateTraderStateFromServer(redState, blueState)
    }

    override fun getName(): String = Operators.CLIENT_UPDATE_TRADER_STATE
}