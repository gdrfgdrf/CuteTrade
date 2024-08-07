package io.github.gdrfgdrf.cutetrade.extension

import net.minecraft.util.Identifier

fun createIdentifier(id: String): Identifier? {
    return Identifier.tryParse(id)
}