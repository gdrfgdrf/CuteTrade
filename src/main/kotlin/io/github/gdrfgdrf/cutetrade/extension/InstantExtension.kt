package io.github.gdrfgdrf.cutetrade.extension

import com.google.protobuf.Timestamp
import java.time.Instant

fun Instant.toTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setNanos(this.nano)
        .setSeconds(this.epochSecond)
        .build()
}