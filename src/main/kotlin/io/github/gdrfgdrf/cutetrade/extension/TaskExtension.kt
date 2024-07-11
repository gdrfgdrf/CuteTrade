package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import io.github.gdrfgdrf.cutetrade.utils.task.entry.FutureTaskEntry
import io.github.gdrfgdrf.cutetrade.utils.task.entry.TaskEntry

fun runAsyncTask(runnable: () -> Unit) {
    TaskEntry<Any>(runnable)
        .run()
}

fun <T> runSyncTask(lock: Any, supplier: () -> T?) {
    FutureTaskEntry.create(supplier)
        .customLock(lock)
        .run()
}

