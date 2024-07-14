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

package io.github.gdrfgdrf.cutetrade.utils.task

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.utils.task.entry.TaskEntry
import io.github.gdrfgdrf.cutetrade.utils.task.worker.SyncTaskWorker
import io.github.gdrfgdrf.cutetrade.utils.task.worker.TaskWorker
import io.github.gdrfgdrf.cutetrade.utils.thread.ThreadPoolService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue

object TaskManager {
    val TASK_ENTRY_QUEUE = ConcurrentLinkedQueue<TaskEntry<*>>()
    val SYNCHRONIZED_TASK_ENTRY = ConcurrentHashMap<Any, LinkedBlockingQueue<TaskEntry<*>>>()
    private val TASK_WORKER = TaskWorker
    private val SYNCHRONIZED_TASK_WORKER = SyncTaskWorker

    private var terminated = false

    fun start() {
        "Starting task manager".logInfo()

        terminated = false

        ThreadPoolService.newTask(TASK_WORKER)
        ThreadPoolService.newTask(SYNCHRONIZED_TASK_WORKER)
    }

    fun isTerminated(): Boolean = terminated

    fun terminate() {
        terminated = true
        TASK_ENTRY_QUEUE.clear()
        SYNCHRONIZED_TASK_ENTRY.clear()
        "Task manager terminated".logInfo()
    }

    fun add(taskEntry: TaskEntry<*>) {
        if (terminated) {
            throw IllegalStateException("Task manager has been terminated")
        }
        TASK_ENTRY_QUEUE.offer(taskEntry)
    }




}