package io.github.gdrfgdrf.cutetrade.command.suggest

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.gdrfgdrf.cutetrade.manager.TradeRequestManager
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object TradeRequestBindSuggestProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val redPlayers = TradeRequestManager.getRedPlayersByBluePlayer(context.source.player!!)
        if (redPlayers.isNullOrEmpty()) {
            return builder.buildFuture()
        }

        redPlayers.forEach {
            builder.suggest(it.name.string)
        }

        return builder.buildFuture()
    }
}