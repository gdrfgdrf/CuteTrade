package io.github.gdrfgdrf.cutetrade.extension

import net.minecraft.entity.player.PlayerInventory

fun PlayerInventory.removeAllTags(tag: String) {
    for (i in 0 .. 41) {
        val stack = this.getStack(i)
        stack.getOrCreateNbt().remove(tag)
    }
}