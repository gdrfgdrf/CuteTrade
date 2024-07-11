package io.github.gdrfgdrf.cutetrade.page

import net.minecraft.screen.ScreenHandlerType

object PageableRegistry {
    val PAGEABLE_SCREEN_HANDLER: ScreenHandlerType<PageableScreenHandler> =
        ScreenHandlerType.register("cutetrade:pageable_screen", ::PageableScreenHandler)
}