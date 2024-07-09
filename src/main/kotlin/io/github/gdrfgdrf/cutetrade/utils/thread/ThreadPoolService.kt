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