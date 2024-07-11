package io.github.gdrfgdrf.cutetrade.page

import io.github.gdrfgdrf.cutetrade.CuteTrade
import net.minecraft.client.gui.screen.ingame.HandledScreens

object PageableClientRegistry {
    fun register() {
        HandledScreens.register(PageableRegistry.PAGEABLE_SCREEN_HANDLER, ::PageableScreen)
    }

}