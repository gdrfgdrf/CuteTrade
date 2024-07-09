package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.common.TradeRequest
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.manager.TradeRequestManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.trade.TradeContext
import io.github.gdrfgdrf.cutetrade.trade.TradeItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

fun Player.findServerEntity(server: MinecraftServer): ServerPlayerEntity? {
    return server.playerManager.getPlayer(this.name)
}

fun String.findServerEntity(context: PacketContext<*>): ServerPlayerEntity? {
    return context.sender?.server?.playerManager?.getPlayer(this)
}

fun ServerPlayerEntity.getTradeRequest(redPlayerEntity: ServerPlayerEntity): TradeRequest? {
    val requests = TradeRequestManager.getRequests(redPlayerEntity)
    return requests?.stream()
        ?.filter { tradeRequest ->
            tradeRequest.bluePlayerEntity == this@getTradeRequest
        }
        ?.findAny()
        ?.orElse(null)
}

fun ServerPlayerEntity.removeTradeRequest(redPlayerEntity: ServerPlayerEntity) {
    val tradeRequest = getTradeRequest(redPlayerEntity)
    tradeRequest?.let {
        TradeRequestManager.removeRequest(redPlayerEntity, tradeRequest)
    }
}

fun ServerPlayerEntity.currentTradeInventory(): TradeItemStack? {
    val currentTrade = currentTrade() ?: return null
    return if (isRed()) {
        currentTrade.redTradeItemStack
    } else {
        currentTrade.blueTradeItemStack
    }
}

fun ServerPlayerEntity.isTrading(): Boolean {
    return TradeManager.trades.contains(this)
}

fun ServerPlayerEntity.currentTrade(): TradeContext? {
    return TradeManager.trades[this];
}

fun ServerPlayerEntity.checkInTrade(): Boolean {
    val currentTrade = currentTrade()
    if (currentTrade == null) {
        "no_transaction_in_progress".toCommandMessage()
            .send(this)
        return false
    }
    return true
}

fun ServerPlayerEntity.hasTradeRequestWith(redPlayerEntity: ServerPlayerEntity): Boolean {
    return getTradeRequest(redPlayerEntity) != null
}

fun ServerPlayerEntity.isRed(): Boolean {
    val currentTrade = currentTrade()
    return currentTrade?.redPlayer?.name?.string == this.name.string
}