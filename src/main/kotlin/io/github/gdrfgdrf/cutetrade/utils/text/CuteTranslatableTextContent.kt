package io.github.gdrfgdrf.cutetrade.utils.text

import com.google.common.collect.ImmutableList
import io.github.gdrfgdrf.cutetrade.CuteTrade
import net.minecraft.entity.Entity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Language
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

class CuteTranslatableTextContent(
    private val key: String,
    private val fallback: String?,
    private val args: Array<out Any?>,
): TextContent {
    private var languageCache: Language? = null
    private var translations: List<StringVisitable> = ImmutableList.of()

    fun updateTranslations() {
        val language = if (CuteTrade.LANGUAGE != null) {
            CuteTrade.LANGUAGE
        } else {
            Language.getInstance()
        }

        if (language != this.languageCache) {
            this.languageCache = language
            val string = if (this.fallback != null) {
                language!!.get(this.key, this.fallback)
            } else {
                language!!.get(this.key)
            }

            runCatching {
                val builder = ImmutableList.builder<StringVisitable>()
                this.forEachPart(string, builder::add)
                this.translations = builder.build()
            }.onFailure {
                this.translations = ImmutableList.of(StringVisitable.plain(string))
            }
        }
    }

    private fun forEachPart(translation: String, partsConsumer: Consumer<StringVisitable>) {
        val matcher = ARG_FORMAT.matcher(translation)

        try {
            var i = 0
            var j = 0

            while (matcher.find(j)) {
                val k = matcher.start()
                val l = matcher.end()
                if (k > j) {
                    val string = translation.substring(j, k)
                    require(string.indexOf(37.toChar()) == -1)

                    partsConsumer.accept(StringVisitable.plain(string))
                }

                val string = matcher.group(2)
                val string2 = translation.substring(k, l)
                if ("%" == string && "%%" == string2) {
                    partsConsumer.accept(LITERAL_PERCENT_SIGN)
                } else {
                    if ("s" != string) {
                        throw CuteTranslationException(this, "Unsupported format: '$string2'")
                    }

                    val string3 = matcher.group(1)
                    val m = if (string3 != null) string3.toInt() - 1 else i++
                    partsConsumer.accept(this.getArg(m))
                }

                j = l
            }

            if (j < translation.length) {
                val string4 = translation.substring(j)
                require(string4.indexOf(37.toChar()) == -1)

                partsConsumer.accept(StringVisitable.plain(string4))
            }
        } catch (var12: IllegalArgumentException) {
            throw CuteTranslationException(this, var12)
        }
    }

    fun getArg(index: Int): StringVisitable {
        if (index >= 0 && index < args.size) {
            val any = args[index]
            return if (any is Text) {
                any as Text
            } else {
                if (any == null) NULL_ARGUMENT else StringVisitable.plain(any.toString())
            }
        } else {
            throw CuteTranslationException(this, index)
        }
    }

    override fun <T : Any> visit(visitor: StringVisitable.StyledVisitor<T>, style: Style): Optional<T>? {
        this.updateTranslations()

        this.translations.forEach { stringVisitable ->
            val optional = stringVisitable.visit(visitor, style)
            if (optional.isPresent) {
                return optional
            }
        }
        return Optional.empty()
    }

    override fun <T : Any> visit(visitor: StringVisitable.Visitor<T>?): Optional<T> {
        this.updateTranslations()

        this.translations.forEach {  stringVisitable ->
            val optional = stringVisitable.visit(visitor)
            if (optional.isPresent) {
                return optional
            }
        }
        return Optional.empty()
    }

    override fun parse(source: ServerCommandSource?, sender: Entity?, depth: Int): MutableText {
        val objects = arrayOfNulls<Any>(this.args.size)

        for (i in objects.indices) {
            val any = this.args[i]
            if (any is Text) {
                objects[i] = Texts.parse(source, any, sender, depth)
            } else {
                objects[i] = any
            }
        }

        return MutableText.of(CuteTranslatableTextContent(this.key, this.fallback, objects))
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else {
            other is CuteTranslatableTextContent &&
                    Objects.equals(this.key, other.key) &&
                    Objects.equals(this.fallback, other.fallback) &&
                    this.args.contentEquals(other.args)
        }
    }

    override fun hashCode(): Int {
        var i = Objects.hashCode(this.key)
        i = 31 * i + Objects.hashCode(this.fallback)
        return 31 * i + this.args.contentHashCode()
    }

    override fun toString(): String {
        val fallback = if (this.fallback != null) {
            ", fallback='${this.fallback}'"
        } else {
            ""
        }

        return "\"cuteTranslation{key='$key'$fallback, args=${this.args.contentToString()}\"}"
    }

    fun getKey(): String {
        return this.key
    }

    fun getFallback(): String? {
        return this.fallback
    }

    fun getArgs(): Array<out Any?> {
        return this.args
    }

    companion object {
        val EMPTY_ARGUMENTS: Array<Any?> = arrayOfNulls(0)
        private val LITERAL_PERCENT_SIGN: StringVisitable = StringVisitable.plain("%")
        private val NULL_ARGUMENT: StringVisitable = StringVisitable.plain("null")
        private val ARG_FORMAT: Pattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)")
    }

}