package io.github.gdrfgdrf.cutetrade.command.suggest

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object AllPlayersSuggestProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        context ?: return builder.buildFuture()

        val nameToPlayerMap = PlayerManager.playerProtobuf?.message?.nameToPlayerMap
            ?: return builder.buildFuture()

        nameToPlayerMap.forEach { (name, _) ->
            builder.suggest(name)
        }

        return builder.buildFuture()
    }
}