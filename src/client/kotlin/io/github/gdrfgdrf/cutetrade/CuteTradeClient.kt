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

package io.github.gdrfgdrf.cutetrade

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.NetworkManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.network.packet.S2COperationPacket
import io.github.gdrfgdrf.cutetrade.operation.*
import io.github.gdrfgdrf.cutetrade.page.PageableClientRegistry
import io.github.gdrfgdrf.cutetrade.screen.DevScreen
import io.github.gdrfgdrf.cutetrade.screen.TradeScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.screen.ingame.HandledScreens

object CuteTradeClient : ClientModInitializer {
	override fun onInitializeClient() {
		"Client loading phase".logInfo()

		prepareEventListeners()
		preparePacketReceiver()
		prepareOperators()

		HandledScreens.register(CuteTrade.TRADE_SCREEN_HANDLER, ::TradeScreen)
		HandledScreens.register(CuteTrade.DEV_SCREEN_HANDLER, ::DevScreen)
		PageableClientRegistry.register()
	}

	private fun prepareEventListeners() {
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
			ClientTradeManager.endTrade()
		}
	}

	private fun preparePacketReceiver() {
		"Registering network channel for client".logInfo()

		NetworkManager.initialize()
		ClientPlayNetworking.registerGlobalReceiver(S2COperationPacket.ID) { payload, context ->
			val client = context.client()
			client.execute {
				S2COperationPacket.handle(PacketContext(payload))
			}
		}
	}

	private fun prepareOperators() {
		val operators = listOf(
			InitializeTradeOperator,
			OpenTradeScreenOperator,
			CloseTradeScreenOperator,
			UpdateTraderStateOperator,
			TradeStartOperator,
			TradeEndOperator,
			DevOperator
		)
		operators.forEach {
			"Add ${it.getName()} to operator list".logInfo()
			OperationDispatcher.add(it)
		}
	}
}