package io.github.gdrfgdrf.cutetrade.common

import net.minecraft.util.Identifier

object Constants {
    const val PLAYER_STORE_FILE_NAME: String = "cute_trade_player_record"
    const val TRADE_STORE_FILE_NAME: String = "cute_trade_trade_record"

    val S2C_OPERATION: Identifier = Identifier.of("cutetrade_networking", "s2c_operation")!!
    val C2S_OPERATION: Identifier = Identifier.of("cutetrade_networking", "c2s_operation")!!
}