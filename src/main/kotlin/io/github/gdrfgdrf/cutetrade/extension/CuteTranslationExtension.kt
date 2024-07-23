package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import io.github.gdrfgdrf.cutetranslationapi.text.CuteText
import io.github.gdrfgdrf.cutetranslationapi.text.CuteTranslation
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class TranslationProxy private constructor(private val serverPlayerEntity: ServerPlayerEntity) {
    fun toCommandTranslation(key: String): TranslationTextProxy {
        return TranslationTextProxy.of(key.toCommandTranslation(serverPlayerEntity), serverPlayerEntity)
    }
    fun toCommandText(key: String): CuteText = key.toCommandText(serverPlayerEntity)

    fun toScreenTranslation(key: String): TranslationTextProxy {
        return TranslationTextProxy.of(key.toScreenTranslation(serverPlayerEntity), serverPlayerEntity)
    }
    fun toScreenText(key: String): CuteText = key.toScreenText(serverPlayerEntity)

    fun toTradeTranslation(key: String): TranslationTextProxy {
        return TranslationTextProxy.of(key.toTradeTranslation(serverPlayerEntity), serverPlayerEntity)
    }
    fun toTradeText(key: String): CuteText = key.toTradeText(serverPlayerEntity)

    fun toInformationTranslation(key: String): TranslationTextProxy {
        return TranslationTextProxy.of(key.toInformationTranslation(serverPlayerEntity), serverPlayerEntity)
    }
    fun toInformationText(key: String): CuteText = key.toInformationText(serverPlayerEntity)

    companion object {
        fun of(commandInvoker: CommandInvoker): TranslationProxy = TranslationProxy(commandInvoker.source.player!!)
        fun of(serverPlayerEntity: ServerPlayerEntity): TranslationProxy = TranslationProxy(serverPlayerEntity)
    }

}

class TranslationTextProxy private constructor(
    private var raw: CuteTranslation,
    private val serverPlayerEntity: ServerPlayerEntity,
) {
    fun get0(): CuteText {
        return raw.get(0)
    }

    fun get(index: Int): CuteText {
        return raw.get(index)
    }

    fun append(cuteText: CuteText): TranslationTextProxy {
        raw.append(cuteText)
        return this
    }

    fun format0(vararg any: Any): TranslationTextProxy {
        raw.format0(*any)
        return this
    }

    fun build(): Text {
        return raw.build()
    }

    fun reset(): TranslationTextProxy {
        this.raw = CuteTranslation.of(raw.build().string)
        return this
    }

    fun send(customPrefix: String) {
        val prefix = CuteText.of(customPrefix)
        send(prefix)
    }

    fun send(customPrefix: CuteText? = null) {
        if (customPrefix == null) {
            val prefix = "prefix".toCommandText(serverPlayerEntity)
            raw.send(prefix, serverPlayerEntity)
            return
        }
        raw.send(customPrefix, serverPlayerEntity)
    }

    companion object {
        fun of(
            raw: CuteTranslation,
            serverPlayerEntity: ServerPlayerEntity,
        ): TranslationTextProxy = TranslationTextProxy(raw, serverPlayerEntity)
    }
}

fun <R> ServerPlayerEntity.translationScope(block: TranslationProxy.() -> R): R {
    val translationProxy = TranslationProxy.of(this)
    translationProxy.apply {
        val result = block()
        return result
    }
}

fun <R> CommandInvoker.translationScope(block: TranslationProxy.() -> R): R {
    val translationProxy = TranslationProxy.of(this)
    translationProxy.apply {
        val result = block()
        return result
    }
}

fun CuteTranslation.send(prefix: CuteText, serverPlayerEntity: ServerPlayerEntity) {
    if (serverPlayerEntity.isDisconnected) {
        return
    }
    val text = prefix.build()

    if (notBlank(text.string)) {
        this.insert(0, prefix)
        send(serverPlayerEntity)
        return
    }
    send(serverPlayerEntity)
}

fun CuteTranslation.send(prefix: String, commandInvoker: CommandInvoker) {
    if (commandInvoker.source.player == null || commandInvoker.source.player!!.isDisconnected) {
        return
    }

    if (notBlank(prefix)) {
        val prefixText = CuteText.of(prefix)
        this.insert(0, prefixText)
        send(commandInvoker.source.player!!)
        return
    }
    send(commandInvoker.source.player!!)
}

fun CuteTranslation.send(commandInvoker: CommandInvoker) {
    if (commandInvoker.source.player == null || commandInvoker.source.player!!.isDisconnected) {
        return
    }
    val prefix = "prefix".toCommandText(commandInvoker.source.player)

    send(prefix, commandInvoker.source.player!!)
}

fun CuteTranslation.format0(vararg any: Any): CuteTranslation {
    this.get(0).format(*any)
    return this
}

fun CuteTranslation.format(index: Int, vararg any: Any): CuteTranslation {
    this.get(index).format(*any)
    return this
}