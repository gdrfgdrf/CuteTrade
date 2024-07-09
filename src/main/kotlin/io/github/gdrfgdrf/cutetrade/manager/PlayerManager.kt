package io.github.gdrfgdrf.cutetrade.manager

import cutetrade.protobuf.CommonProto.Player
import cutetrade.protobuf.StorableProto.PlayerStore
import io.github.gdrfgdrf.cutetrade.utils.Protobuf

object PlayerManager {
    var playerProtobuf: Protobuf<PlayerStore>? = null

    fun findPlayer(
        name: String
    ): Player? {
        return playerProtobuf?.message?.getNameToPlayerOrDefault(name, null)
    }

    fun recordPlayer(
        name: String
    ) {
        if (playerProtobuf?.message?.containsNameToPlayer(name) == true) {
            return
        }

        playerProtobuf?.rebuild { playerStore ->
            playerStore?.toBuilder()
                ?.putNameToPlayer(name, Player.newBuilder()
                    .setName(name)
                    .build())
                ?.build()!!
        }
        playerProtobuf?.save()
    }


}