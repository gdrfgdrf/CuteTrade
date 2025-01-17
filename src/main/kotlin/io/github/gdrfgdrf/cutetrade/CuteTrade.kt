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

import com.google.protobuf.Message
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import cutetrade.protobuf.StorableProto.PlayerStore
import cutetrade.protobuf.StorableProto.TradeStore
import io.github.gdrfgdrf.cutetrade.command.*
import io.github.gdrfgdrf.cutetrade.command.admin.HelpAdminCommand
import io.github.gdrfgdrf.cutetrade.command.admin.HistoryAdminCommand
import io.github.gdrfgdrf.cutetrade.common.Constants
import io.github.gdrfgdrf.cutetrade.extension.currentTrade
import io.github.gdrfgdrf.cutetrade.extension.logError
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.network.NetworkManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.network.packet.C2SOperationPacket
import io.github.gdrfgdrf.cutetrade.operation.OperationDispatcher
import io.github.gdrfgdrf.cutetrade.operation.server.ClientInitializedOperator
import io.github.gdrfgdrf.cutetrade.operation.server.UpdateTraderStateOperator
import io.github.gdrfgdrf.cutetrade.page.PageableRegistry
import io.github.gdrfgdrf.cutetrade.screen.handler.TradeScreenHandler
import io.github.gdrfgdrf.cutetrade.utils.FriendlyText
import io.github.gdrfgdrf.cutetrade.utils.Protobuf
import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import io.github.gdrfgdrf.cutetrade.utils.thread.ThreadPoolService
import io.github.gdrfgdrf.cutetrade.worker.CountdownWorker
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object CuteTrade : ModInitializer {
	val TRADE_SCREEN_HANDLER: ScreenHandlerType<TradeScreenHandler> =
		ScreenHandlerType.register("cutetrade:cutetrade_trade_screen", ::TradeScreenHandler)
	val DEV_SCREEN_HANDLER: ScreenHandlerType<TradeScreenHandler> =
		ScreenHandlerType.register("cutetrade:cutetrade_dev_screen", ::TradeScreenHandler)
	var SERVER: MinecraftServer? = null

	init {
		PageableRegistry
	}

	val log: Logger = LoggerFactory.getLogger("CuteTrade")

	override fun onInitialize() {
		"Start loading CuteTrade".logInfo()

		runCatching {
			val envType = FabricLoader.getInstance().environmentType
			if (envType == EnvType.SERVER) {
				"Server loading phase".logInfo()

				preparePacketReceiver()
				prepareEventListener()

				PlayerManager.playerProtobuf = prepareProtobufFile(
					File(Constants.PLAYER_STORE_FILE_NAME),
					PlayerStore.newBuilder()::build,
					PlayerStore::parseFrom
				)
				TradeManager.tradeProtobuf = prepareProtobufFile(
					File(Constants.TRADE_STORE_FILE_NAME),
					TradeStore.newBuilder()::build,
					TradeStore::parseFrom
				)
			}

			prepareCommands()

			FriendlyText.prefix = "prefix".toCommandMessage()

			val operators = arrayOf(
				ClientInitializedOperator,
				UpdateTraderStateOperator
			)
			operators.forEach {
				OperationDispatcher.add(it)
			}
		}.onFailure {
			"Unable to initialize CuteTrade".logError(it)
			throw IllegalStateException(it)
		}
	}

	private fun preparePacketReceiver() {
		"Registering network channel".logInfo()

		NetworkManager.initialize()
		ServerPlayNetworking.registerGlobalReceiver(C2SOperationPacket.ID) { payload, context ->
			val player = context.player()
			val server = player.server
			server.execute {
				val packetContext = PacketContext(payload)
				packetContext.sender = player

				C2SOperationPacket.handle(packetContext)
			}
		}
	}

	private fun prepareEventListener() {
		ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
			PlayerManager.recordPlayer(handler.player.name.string)
		}
		ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
			handler.player.currentTrade()?.terminate()
		}

		ServerLifecycleEvents.SERVER_STARTING.register { server ->
			SERVER = server
			CountdownWorker.start()
			TaskManager.start()
		}
		ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
			ThreadPoolService.terminate()
			CountdownWorker.reset()
			TaskManager.terminate()
		}
	}

	private fun prepareCommands() {
		val allCommands = listOf(
			RequestTradeCommand,
			AcceptTradeResultCommand,
			DeclineTradeResultCommand,
			EndTradeCommand,
			HelpCommand,
			TutorialCommand,
			HistoryCommand
//			DevCommand
		)

		val allAdminCommands = listOf(
			HistoryAdminCommand,
			HelpAdminCommand
		)

		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
			val common = LiteralArgumentBuilder.literal<ServerCommandSource>("trade-public")
			val admin = LiteralArgumentBuilder.literal<ServerCommandSource>("trade-admin")
				.requires {
					it.player?.hasPermissionLevel(3) == true
				}

			allCommands.forEach { command ->
				"Registering command ${command::class.simpleName}".logInfo()
				command.register(common)
			}

			allAdminCommands.forEach { command ->
				"Registering admin command ${command::class.simpleName}".logInfo()
				command.register(admin)
			}

			dispatcher.register(
				common
			)
			dispatcher.register(
				admin
			)
		}
	}

	private fun <T : Message> prepareProtobufFile(
		protobufFile: File,
		buildFunction: () -> T,
		parseFunction: (ByteArray) -> T,
	): Protobuf<T> {
		"Preparing protobuf file: $protobufFile".logInfo()

		if (!protobufFile.exists()) {
			protobufFile.createNewFile()
			val protobuf = Protobuf<T>()
			protobuf.message = buildFunction()
			protobuf.storeFile = protobufFile
			protobuf.save()

			"Prepared protobuf file: $protobufFile".logInfo()
			return protobuf
		}
		val protobuf = Protobuf.get(protobufFile, parseFunction)
		"Prepared protobuf file: $protobufFile".logInfo()

		return protobuf!!
	}
}