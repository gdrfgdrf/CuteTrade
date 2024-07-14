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

package io.github.gdrfgdrf.cutetrade.page

class Navigator(private val inventory: PageableInventory) {
    private var currentIndex: Int = 0
    val pages: ArrayList<Page> = ArrayList()

    fun show(index: Int) {
        currentIndex = index
        val page = pages[currentIndex]
        page.show(inventory)
    }

    fun next() {
        if (currentIndex == pages.size - 1) {
            show(currentIndex)
            return
        }
        currentIndex++
        show(currentIndex)
    }

    fun previous() {
        if (currentIndex == 0) {
            show(currentIndex)
            return
        }
        currentIndex--
        show(currentIndex)
    }
}