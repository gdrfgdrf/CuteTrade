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

import io.github.gdrfgdrf.cutetrade.base.executor.GlobalVariable
import io.github.gdrfgdrf.cutetrade.base.executor.Registry
import io.github.gdrfgdrf.cutetrade.common.impl.Functions
import io.github.gdrfgdrf.cutetrade.common.network.PacketContext
import io.github.gdrfgdrf.cutetrade.common.pool.PlayerProxyPool
import io.github.gdrfgdrf.cutetrade.base.extension.logError
import io.github.gdrfgdrf.cutetrade.base.extension.logInfo
import io.github.gdrfgdrf.cutetrade.base.executor.Listeners
import io.github.gdrfgdrf.cutetrade.network.NetworkManager
import io.github.gdrfgdrf.cutetrade.network.packet.C2SOperationPacket
import io.github.gdrfgdrf.cutetrade.base.pageable.PageableRegistry
import io.github.gdrfgdrf.cutetrade.base.screen.handler.TradeScreenHandler
import io.github.gdrfgdrf.cutetranslationapi.external.ExternalPlayerTranslationProvider
import io.github.gdrfgdrf.cutetranslationapi.external.ExternalTranslationProvider
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CuteTrade : ModInitializer {
	var SERVER: MinecraftServer? = null

	init {
		GlobalVariable
		PageableRegistry
	}

	val log: Logger = LoggerFactory.getLogger("CuteTrade")

	override fun onInitialize() {
		"Start loading CuteTrade".logInfo()

		runCatching {
			Functions.initialize()

			if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
				preparePacketReceiver()
			}

			prepareEventListener()
			prepareCommands()

			Registry.registerOperators()
		}.onFailure {
			"Unable to initialize CuteTrade".logError(it)
			throw IllegalStateException(it)
		}
	}

	private fun preparePacketReceiver() {
		"Registering network channel".logInfo()

		NetworkManager.initialize()
		ServerPlayNetworking.registerGlobalReceiver(C2SOperationPacket.ID) { payload, context ->
			val player = PlayerProxyPool.getPlayerProxy(context.player().name.string)
			val server = context.player().server
			server.execute {
				val packetContext = PacketContext(player, payload)
				C2SOperationPacket.handle(packetContext)
			}
		}
	}

	private fun prepareEventListener() {
		ServerLivingEntityEvents.ALLOW_DEATH.register { entity, _, _ ->
			Listeners.allowDeath(entity)
		}
		ServerPlayerEvents.AFTER_RESPAWN.register { oldPlayerEntity, newPlayerEntity, _ ->
			Listeners.afterRespawn(oldPlayerEntity, newPlayerEntity)
		}

		ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
			Listeners.join(handler)
		}
		ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
			Listeners.disconnect(handler)
		}

		ServerLifecycleEvents.SERVER_STARTING.register { server ->
			SERVER = server
			Listeners.serverStarting()
		}
		ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
			Listeners.serverStopping()
		}
	}

	private fun prepareCommands() {
		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
			Registry.registerCommand(dispatcher)
		}
	}
}