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