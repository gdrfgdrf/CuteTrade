package io.github.gdrfgdrf.cutetrade.utils.text

import java.util.*

class CuteTranslationException : IllegalArgumentException {
    constructor(text: CuteTranslatableTextContent?, message: String?):
            super(String.format(Locale.ROOT, "Error parsing: %s: %s", text, message))

    constructor(text: CuteTranslatableTextContent?, index: Int):
            super(String.format(Locale.ROOT, "Invalid index %d requested for %s", index, text))

    constructor(text: CuteTranslatableTextContent?, cause: Throwable?):
            super(String.format(Locale.ROOT, "Error while parsing: %s", text), cause)
}