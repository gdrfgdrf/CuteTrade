package io.github.gdrfgdrf.cutetrade.utils.task.worker

import io.github.gdrfgdrf.cutetrade.extension.launchIO
import io.github.gdrfgdrf.cutetrade.extension.logError
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.extension.sleepSafety
import io.github.gdrfgdrf.cutetrade.utils.task.entry.FutureTaskEntry
import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

object SyncTaskWorker : Runnable {
    @OptIn(DelicateCoroutinesApi::class)
    override fun run() {
        "Synchronized task worker started".logInfo()

        while (!TaskManager.isTerminated()) {
            TaskManager.SYNCHRONIZED_TASK_ENTRY.forEach { (lock, taskEntries) ->
                TaskManager.SYNCHRONIZED_TASK_ENTRY.remove(lock)

                GlobalScope.launchIO {
                    var nextRound = true

                    while (nextRound) {
                        runCatching {
                            val taskEntry = taskEntries.take()

                            if (lock is String) {
                                synchronized(lock.intern()) {
                                    val result = taskEntry.supplier()

                                    if (taskEntry is FutureTaskEntry) {
                                        taskEntry.result(result)
                                    } else {
                                        taskEntry.notifyMethodFinished()
                                    }
                                }
                            } else {
                                synchronized(lock) {
                                    val result = taskEntry.supplier()

                                    if (taskEntry is FutureTaskEntry) {
                                        taskEntry.result(result)
                                    } else {
                                        taskEntry.notifyMethodFinished()
                                    }
                                }
                            }
                        }.onFailure {
                            "InterruptedException when taking out task from linked blocking queue".logError(it)
                        }

                        if (taskEntries.isEmpty()) {
                            nextRound = false
                        }
                    }
                }
            }

            sleepSafety(100)
        }

        "Synchronized task worker terminated".logInfo()
    }
}