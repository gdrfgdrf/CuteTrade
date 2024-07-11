package io.github.gdrfgdrf.cutetrade.manager

import cutetrade.protobuf.CommonProto.Player
import cutetrade.protobuf.StorableProto.PlayerStore
import io.github.gdrfgdrf.cutetrade.extension.runSyncTask
import io.github.gdrfgdrf.cutetrade.utils.Protobuf

object PlayerManager {
    var playerProtobuf: Protobuf<PlayerStore>? = null

    fun findPlayer(
        name: String
    ): Player? {
        return playerProtobuf?.message?.getNameToPlayerOrDefault(
            name,
            null
        )
    }

    fun recordPlayer(
        name: String
    ) = runSyncTask(playerProtobuf!!) {
        if (playerProtobuf?.message?.containsNameToPlayer(name) == true) {
            return@runSyncTask
        }

        playerProtobuf!!.rebuild { playerStore ->
            playerStore!!.toBuilder()
                .putNameToPlayer(name, Player.newBuilder()
                    .setName(name)
                    .build())
                .build()
        }
        playerProtobuf!!.save()
    }

    fun recordTrade(
        player: Player,
        tradeId: String
    ) = runSyncTask(playerProtobuf!!) {
        val newPlayer = player.toBuilder()
            .addTradeIds(tradeId)
            .build()

        playerProtobuf!!.rebuild { playerStore ->
            playerStore!!.toBuilder()
                .putNameToPlayer(newPlayer.name, newPlayer)
                .build()
        }
        playerProtobuf!!.save()
    }
}