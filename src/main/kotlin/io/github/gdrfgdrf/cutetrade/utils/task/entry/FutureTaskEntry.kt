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

import io.github.gdrfgdrf.cutetrade.utils.SyncFuture
import io.github.gdrfgdrf.cutetrade.utils.task.TaskManager
import java.util.concurrent.TimeUnit

class FutureTaskEntry<T> private constructor(
    supplier: () -> T,
): TaskEntry<T>(
    supplier
) {
    private val syncFuture = SyncFuture<T>()

    override fun syncLockTimeout(syncLockTimeout: Long): TaskEntry<T> {
        throw UnsupportedOperationException()
    }

    override fun sync(sync: Boolean): TaskEntry<T> {
        throw UnsupportedOperationException()
    }

    override fun notifyMethodFinished() {
        throw UnsupportedOperationException()
    }

    @Suppress("unchecked_cast")
    internal fun result(result: Any?) {
        syncFuture.result(result as T)
    }

    fun get(): T? {
        return syncFuture.getSafety()
    }

    fun get(timeout: Long, unit: TimeUnit): T? {
        return syncFuture.getSafety(timeout, unit)
    }

    override fun customLock(customLock: Any): FutureTaskEntry<T> {
        super.customLock(customLock)
        return this
    }

    override fun run() {
        TaskManager.add(this)
    }

    companion object {
        fun <T> create(supplier: () -> T): FutureTaskEntry<T> = FutureTaskEntry(supplier)
    }
}