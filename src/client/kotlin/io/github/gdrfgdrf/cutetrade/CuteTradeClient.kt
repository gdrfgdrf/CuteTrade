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

import io.github.gdrfgdrf.cutetrade.common.impl.PacketByteBufProxyImpl
import io.github.gdrfgdrf.cutetrade.common.network.NetworkManager
import io.github.gdrfgdrf.cutetrade.common.network.PacketContext
import io.github.gdrfgdrf.cutetrade.common.network.interfaces.Writeable
import io.github.gdrfgdrf.cutetrade.common.operation.OperationDispatcher
import io.github.gdrfgdrf.cutetrade.common.proxy.PacketByteBufProxy
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.operation.*
import io.github.gdrfgdrf.cutetrade.page.PageableClientRegistry
import io.github.gdrfgdrf.cutetrade.screen.DevScreen
import io.github.gdrfgdrf.cutetrade.screen.TradeScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

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

		NetworkManager.initialize(object : NetworkManager.RegisterPacketInterface {
			override fun <T> register(
				packetIdentifier: Any,
				messageType: Class<out Writeable>,
				encoder: (T, PacketByteBufProxy) -> Unit,
				decoder: (PacketByteBufProxy) -> T,
				handler: (PacketContext<T>) -> Unit
			) {
				ClientPlayNetworking.registerGlobalReceiver(packetIdentifier as Identifier) { client, _, buf, _ ->
					val packetByteBufProxy = PacketByteBufProxyImpl.create(buf)
					val message: T = decoder(packetByteBufProxy)
					client.execute {
						handler(PacketContext(message))
					}
				}
			}
		})
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