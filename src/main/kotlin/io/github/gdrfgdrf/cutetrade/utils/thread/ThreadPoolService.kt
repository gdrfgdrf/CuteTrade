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

package io.github.gdrfgdrf.cutetrade.utils.thread

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy
import java.util.concurrent.TimeUnit

object ThreadPoolService {
    private val EXECUTOR_SERVICE = ThreadPoolExecutor(
        20,
        100,
        0L,
        TimeUnit.MILLISECONDS,
        ArrayBlockingQueue(1024),
        NamedThreadFactory,
        AbortPolicy()
    )

    fun newTask(runnable: Runnable) {
        EXECUTOR_SERVICE.execute(runnable)
    }

    fun terminate() {
        EXECUTOR_SERVICE.shutdownNow()
        "Execute service terminated".logInfo()
    }

}