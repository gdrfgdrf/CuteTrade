package io.github.gdrfgdrf.cutetrade.page

class Navigator(private val inventory: PageableInventory) {
    private var currentIndex: Int = 0
    val pages: ArrayList<Page> = ArrayList()

    fun currentIndex(): Int = currentIndex

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