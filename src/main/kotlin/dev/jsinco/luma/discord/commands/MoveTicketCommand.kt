package dev.jsinco.luma.discord.commands

import dev.jsinco.discord.framework.commands.CommandManager
import dev.jsinco.discord.framework.commands.CommandModule
import dev.jsinco.discord.framework.commands.DiscordCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

@DiscordCommand(
    name = "move",
    description = "Move this ticket to a different category.",
    permission = Permission.MESSAGE_MANAGE,
    guildOnly = true
)
class MoveTicketCommand : CommandModule {


    companion object {
        // Hardcoding for now is fine, but config should be done soon
        val CATEGORIES = linkedMapOf(
            "default" to 1188333999949619261,
            "tiktok" to 1547023112393068574,
            "event" to 1433828802198503467,
            "misc" to 1367503791582216294,
            "vouchers" to 1475698660645015584,
            "reports" to 1344222002285641818,
            "bugs" to 1342643719219970158,
            "handled" to 1340775656677703782,
        )
        val isTicketChannel = { channelName: String ->
            channelName.startsWith("ticket-")
        }

        val EPHEMERAL_OPTION = OptionData(OptionType.BOOLEAN, "ephemeral", "Only show the reply to you (default: false)", false)

        fun moveTicket(event: SlashCommandInteractionEvent, name: String) {
            val channel = event.channel as? TextChannel ?: return
            val guild = event.guild ?: return

            if (!isTicketChannel(channel.name)) {
                event.reply("This command can only be used in a ticket channel.").setEphemeral(true).queue()
                return
            }

            val category = CATEGORIES[name]?.let { guild.getCategoryById(it) } ?: run {
                event.reply("Category `$name` not found.").setEphemeral(true).queue()
                return
            }

            channel.manager.setParent(category).queue {
                event.reply("Ticket moved to `${category.name}`.")
                    .setEphemeral(event.getOption("ephemeral")?.asBoolean ?: false)
                    .queue()
            }
        }
    }

    override fun register() {
        CommandManager.registerCommand(this)
        CATEGORIES.keys.forEach { CommandManager.registerCommand(CategoryAliasCommand(it)) }
    }

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(OptionType.STRING, "cat", "The category to move this ticket to", true)
                .addChoices(CATEGORIES.keys.map { Command.Choice(it, it) }),
            EPHEMERAL_OPTION
        )
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        moveTicket(event, event.getOption("cat")?.asString ?: return)
    }

    // Alias for /move cat:<category>, e.g. /handled. No no-args constructor, so registered by MoveTicketCommand.
    private class CategoryAliasCommand(private val category: String) : CommandModule {
        private val info = DiscordCommand(
            name = category,
            description = "Move this ticket to the ${category.replaceFirstChar { it.uppercase() }} category.",
            permission = Permission.MESSAGE_MANAGE,
            guildOnly = true
        )

        override fun getCommandInfo(): DiscordCommand = info

        override fun getOptions(): List<OptionData> = listOf(EPHEMERAL_OPTION)

        override fun execute(event: SlashCommandInteractionEvent) {
            moveTicket(event, category)
        }
    }
}
