package io.github.gdrfgdrf.cutetrade.extension

import com.google.protobuf.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun Timestamp.toInstant(): Instant {
    return Instant.ofEpochSecond(this.seconds)
}

fun Instant.toTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setNanos(this.nano)
        .setSeconds(this.epochSecond)
        .build()
}

fun Instant.formattedDate(): String {
    val date = Date.from(this)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(date)
}