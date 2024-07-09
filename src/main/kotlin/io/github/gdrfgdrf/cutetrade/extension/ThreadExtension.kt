package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.utils.thread.ThreadPoolService

fun Runnable.start() {
    ThreadPoolService.newTask(this)
}

fun sleepSafety(millis: Long) {
    runCatching {
        Thread.sleep(millis)
    }.onFailure {

    }
}