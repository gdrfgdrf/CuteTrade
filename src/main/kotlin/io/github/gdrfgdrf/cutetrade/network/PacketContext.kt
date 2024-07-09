package io.github.gdrfgdrf.cutetrade.network

import net.minecraft.entity.player.PlayerEntity

class PacketContext<T> constructor(val sender: PlayerEntity?, val message: T) {
    constructor(message: T): this(null, message)
}