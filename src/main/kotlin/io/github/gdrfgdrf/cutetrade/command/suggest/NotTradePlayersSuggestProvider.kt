package io.github.gdrfgdrf.cutetrade.command.suggest

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.gdrfgdrf.cutetrade.extension.isTrading
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object NotTradePlayersSuggestProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {

        val notTradePlayerList = context.source.server.playerManager.playerList.stream()
            .filter {
                !it.isTrading() || it.name.string.equals(context.source.player?.name?.string)
            }
            .toList()
        notTradePlayerList.forEach {
            builder.suggest(it.name.string)
        }

        return builder.buildFuture()
    }
}