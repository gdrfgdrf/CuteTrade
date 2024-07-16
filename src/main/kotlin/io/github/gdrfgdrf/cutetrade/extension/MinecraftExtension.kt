package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.CuteTrade
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier

fun registryManager(): DynamicRegistryManager? {
    val envType = FabricLoader.getInstance().environmentType
    if (envType == EnvType.SERVER) {
        return CuteTrade.SERVER?.registryManager
    }
    return null
}

fun createIdentifier(id: String): Identifier? {
    return Identifier.tryParse(id)
}

fun createIdentifier(namespace: String, id: String): Identifier? {
    return Identifier.tryParse(namespace, id)
}