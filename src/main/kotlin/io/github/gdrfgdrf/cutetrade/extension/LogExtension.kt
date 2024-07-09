package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.CuteTrade

fun String.logInfo() {
    CuteTrade.log.info(this)
}

fun String.logError() {
    CuteTrade.log.error(this)
}

fun String.logError(throwable: Throwable) {
    CuteTrade.log.error(this, throwable)
}