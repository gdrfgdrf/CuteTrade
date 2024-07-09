package io.github.gdrfgdrf.cutetrade

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.NetworkManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.*
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
				packetIdentifier: Identifier,
				messageType: Class<T>,
				encoder: (T, PacketByteBuf) -> Unit,
				decoder: (PacketByteBuf) -> T,
				handler: (PacketContext<T>) -> Unit
			) {
				ClientPlayNetworking.registerGlobalReceiver(packetIdentifier) { client, _, buf, _ ->
					val message: T = decoder(buf)
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