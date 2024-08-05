/*
 * Copyright 2024 CuteTrade's contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gdrfgdrf.cutetrade.extension

import io.github.gdrfgdrf.cutetrade.CuteTrade
import io.github.gdrfgdrf.cutetranslationapi.external.ExternalPlayerTranslationProvider
import io.github.gdrfgdrf.cutetranslationapi.external.ExternalTranslationProvider
import io.github.gdrfgdrf.cutetranslationapi.provider.TranslationProviderManager
import io.github.gdrfgdrf.cutetranslationapi.text.CuteText
import io.github.gdrfgdrf.cutetranslationapi.text.CuteTranslation

fun translatable(key: String): CuteTranslation {
    if (CuteTrade.TRANSLATION_PROVIDER == null) {
        throw IllegalStateException("Translation provider is not loaded normally")
    }
    val value = (CuteTrade.TRANSLATION_PROVIDER!! as ExternalTranslationProvider).get(key)
    return CuteTranslation.of(value)
}

fun translatable(playerName: String, key: String): CuteTranslation {
    if (CuteTrade.PLAYER_TRANSLATION_PROVIDER == null) {
        throw IllegalStateException("Player translation provider is not loaded normally")
    }
    val value = (CuteTrade.PLAYER_TRANSLATION_PROVIDER!! as ExternalPlayerTranslationProvider).get(playerName, key)
    return CuteTranslation.of(value)
}

fun translatableText(key: String): CuteText {
    if (CuteTrade.TRANSLATION_PROVIDER == null) {
        throw IllegalStateException("Translation provider is not loaded normally")
    }
    val value = (CuteTrade.TRANSLATION_PROVIDER!! as ExternalTranslationProvider).get(key)
    return CuteText.of(value)
}

fun translatableText(playerName: String, key: String): CuteText {
    if (CuteTrade.PLAYER_TRANSLATION_PROVIDER == null) {
        throw IllegalStateException("Player translation provider is not loaded normally")
    }
    val value = (CuteTrade.PLAYER_TRANSLATION_PROVIDER!! as ExternalPlayerTranslationProvider).get(playerName, key)
    return CuteText.of(value)
}
