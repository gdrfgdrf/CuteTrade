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

package io.github.gdrfgdrf.cutetrade.utils.task.entry

import io.github.gdrfgdrf.cutetrade.extension.logError
import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import java.util.concurrent.CountDownLatch

open class TaskEntry<T> protected constructor(
    val supplier: () -> T?,
) {
    var customLock: Any? = null
    private var syncLock: CountDownLatch = CountDownLatch(1)
    private var syncLockTimeout: Long = 0
    private var enableSync = false

    constructor(runnable: Runnable): this({
        runnable.run()
        null
    })

    open fun syncLockTimeout(syncLockTimeout: Long): TaskEntry<T> {
        this.syncLockTimeout = syncLockTimeout
        return this
    }

    open fun sync(sync: Boolean): TaskEntry<T> {
        this.enableSync = sync
        return this
    }

    open fun customLock(customLock: Any): TaskEntry<T> {
        this.customLock = customLock
        return this
    }

    open fun run() {
        TaskManager.add(this)
        if (enableSync) {
            runCatching {
                syncLock.await()
            }.onFailure {
                "An error occurred while the method was executing".logError(it)
            }
        }
    }

    open fun notifyMethodFinished() {
        if (this.enableSync) {
            syncLock.countDown()
        }
    }

    companion object {
        fun <T> create(runnable: Runnable) = TaskEntry<T>(runnable)
        fun <T> create(supplier: () -> T) = TaskEntry<T>(supplier)
    }
}