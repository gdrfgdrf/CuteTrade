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

package io.github.gdrfgdrf.cutetrade.common

object Operators {
    const val CLIENT_INITIALIZE_TRADE = "client_initialize_trade"
    const val CLIENT_OPEN_TRADE_SCREEN = "client_open_trade_screen"
    const val CLIENT_CLOSE_TRADE_SCREEN = "client_close_trade_screen"
    const val CLIENT_UPDATE_TRADER_STATE = "client_update_trader_state"

    const val CLIENT_TRADE_START = "client_trade_start"
    const val CLIENT_TRADE_END = "client_trade_end"

    const val CLIENT_DEV = "client_dev"

    const val SERVER_UPDATE_TRADER_STATE = "server_update_trader_state"
}