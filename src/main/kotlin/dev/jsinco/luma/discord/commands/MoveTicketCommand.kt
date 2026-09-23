package dev.jsinco.luma.discord.commands

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
            "urgent" to 1367503791582216294,
            "vouchers" to 1475698660645015584,
            "reports" to 1344222002285641818,
            "bugs" to 1342643719219970158,
            "handled" to 1340775656677703782,
        )
        val isTicketChannel = { channelName: String ->
            channelName.startsWith("ticket-")
        }
    }

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(OptionType.STRING, "cat", "The category to move this ticket to", true)
                .addChoices(CATEGORIES.keys.map { Command.Choice(it, it) })
        )
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val channel = event.channel as? TextChannel ?: return
        val guild = event.guild ?: return

        if (!isTicketChannel(channel.name)) {
            event.reply("This command can only be used in a ticket channel.").setEphemeral(true).queue()
            return
        }

        val name = event.getOption("cat")?.asString ?: return
        val category = CATEGORIES[name]?.let { guild.getCategoryById(it) } ?: run {
            event.reply("Category `$name` not found.").setEphemeral(true).queue()
            return
        }

        channel.manager.setParent(category).queue {
            event.reply("Ticket moved to ${category.name}.").setEphemeral(true).queue()
        }
    }
}
