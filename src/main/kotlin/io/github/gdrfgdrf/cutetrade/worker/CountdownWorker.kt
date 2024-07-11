package io.github.gdrfgdrf.cutetrade.worker

import io.github.gdrfgdrf.cutetrade.extension.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object CountdownWorker {
    private var stop = false
    private val tasks = ConcurrentHashMap<CountdownTask, Long>()

    fun start() {
        "Starting countdown worker".logInfo()

        stop = false
        Worker.start()
    }

    fun reset() {
        stop = true
        tasks.clear()

        "Reset countdown worker".logInfo()
    }

    fun add(task: CountdownTask) {
        tasks[task] = System.currentTimeMillis()
    }

    object Worker : Runnable {
        @OptIn(DelicateCoroutinesApi::class)
        override fun run() {
            while (!stop) {
                runCatching {
                    val now = System.currentTimeMillis()

                    tasks.forEach { (task, startTime) ->
                        if (task.end) {
                            tasks.remove(task)
                            return@forEach
                        }

                        val timeout = TimeUnit.MILLISECONDS.convert(task.timeout, task.timeUnit)

                        if (now - startTime >= timeout) {
                            tasks.remove(task)
                            GlobalScope.launchIO {
                                task.endRun()
                            }
                        }
                    }

                }.onFailure {
                    "Error on countdown worker".logError(it)
                }

                sleepSafety(100)
            }
            stop = false

            "Countdown worker has been terminated".logInfo()
        }
    }

    class CountdownTask(
        val timeout: Long,
        val timeUnit: TimeUnit,
        val endRun: () -> Unit
    ) {
        var end: Boolean = false

        companion object {
            fun create(
                timeout: Long,
                timeUnit: TimeUnit,
                endRun: () -> Unit
            ) = CountdownTask(timeout, timeUnit, endRun)
        }
    }

}