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