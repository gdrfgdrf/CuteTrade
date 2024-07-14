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

package io.github.gdrfgdrf.cutetrade.utils.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

@Suppress("removal")
object NamedThreadFactory : ThreadFactory {
    private val poolCount = AtomicInteger()
    private val count = AtomicInteger()
    private var group: ThreadGroup? = null

    init {
        poolCount.incrementAndGet()
        val securityManager = System.getSecurityManager()
        group = if (securityManager != null) securityManager.threadGroup else Thread.currentThread().threadGroup
    }

    override fun newThread(r: Runnable): Thread {
        val result = Thread(group, r)
        result.name = "Pool-" + poolCount.incrementAndGet() + " Thread-" + count.incrementAndGet()
        return result
    }
}