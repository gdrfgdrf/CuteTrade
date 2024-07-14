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

package io.github.gdrfgdrf.cutetrade.utils.task.worker

import io.github.gdrfgdrf.cutetrade.extension.launchIO
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.extension.sleepSafety
import io.github.gdrfgdrf.cutetrade.utils.task.entry.FutureTaskEntry
import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.LinkedBlockingQueue

object TaskWorker : Runnable {
    @OptIn(DelicateCoroutinesApi::class)
    override fun run() {
        "Task worker started".logInfo()

        while (!TaskManager.isTerminated()) {
            val taskEntry = TaskManager.TASK_ENTRY_QUEUE.poll() ?: continue

            if (taskEntry.customLock == null) {
                GlobalScope.launchIO {
                    val result = taskEntry.supplier()

                    if (taskEntry is FutureTaskEntry) {
                        taskEntry.result(result)
                    } else {
                        taskEntry.notifyMethodFinished()
                    }
                }
            } else {
                val taskEntries = TaskManager.SYNCHRONIZED_TASK_ENTRY.computeIfAbsent(taskEntry.customLock!!) {
                    LinkedBlockingQueue()
                }

                taskEntries.put(taskEntry)
            }

            sleepSafety(100)
        }

        "Task worker terminated".logInfo()
    }
}