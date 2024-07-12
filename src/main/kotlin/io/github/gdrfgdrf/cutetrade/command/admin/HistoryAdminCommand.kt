package io.github.gdrfgdrf.cutetrade.command.admin

import com.mojang.brigadier.arguments.StringArgumentType
import io.github.gdrfgdrf.cutetrade.command.AbstractCommand
import io.github.gdrfgdrf.cutetrade.command.HistoryCommand
import io.github.gdrfgdrf.cutetrade.command.suggest.AllPlayersSuggestProvider
import io.github.gdrfgdrf.cutetrade.extension.getContent
import io.github.gdrfgdrf.cutetrade.extension.notBlank
import net.minecraft.server.command.CommandManager

object HistoryAdminCommand : AbstractCommand(
    command = "history",
    onlyPlayer = true,
    needOp = true,
    tree = {
        CommandManager.argument("player-name", StringArgumentType.string())
            .suggests(AllPlayersSuggestProvider)
            .executes { context ->
                playerCheck(HistoryAdminCommand, context) {
                    argumentCheck(
                        context,
                        argumentCheckers = {
                            notBlank("player-name".getContent(context))
                        },
                        success = {
                            HistoryCommand.print(context.source, "player-name".getContent(context))
                        }
                    )
                }
                0
            }
    }
) {
}