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

package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import java.util.concurrent.ConcurrentHashMap

object OperationDispatcher {
    private val map: ConcurrentHashMap<String, Operator> = ConcurrentHashMap()

    fun add(aOperator: Operator) {
        map[aOperator.getName()] = aOperator
    }

    fun dispatch(name: String, context: PacketContext<*>, args: Array<*>?) {
        val aOperator = map[name] ?: return
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            "Running operation $name".logInfo()
        }

        aOperator.run(context, args)
    }

}