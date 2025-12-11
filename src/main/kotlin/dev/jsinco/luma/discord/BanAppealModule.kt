package dev.jsinco.luma.discord

import dev.jsinco.discord.framework.commands.DiscordCommand
import dev.jsinco.discord.framework.util.Module
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent


@DiscordCommand(name = "appeal",
    description = "Ban appeal format",
    permission = Permission.MESSAGE_SEND,
    guildOnly = true)
class BanAppealModule : Module {


    override fun execute(event: SlashCommandInteractionEvent) {
        val channel = event.channel.asTextChannel()
        if (!channel.name.startsWith("ticket-")) {
            event.reply("This command can only be used in a ticket channel.").setEphemeral(true).queue()
            return
        }

        val embed = banAppealEmbed(channel)
        event.replyEmbeds(embed).queue()
    }


    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        val channel = event.channel as? TextChannel ?: return
        if (event.message.contentRaw.contains("!appeal") && channel.name.startsWith("ticket-")) {
            val embed = banAppealEmbed(channel)
            channel.sendMessageEmbeds(embed).queue()
        }
    }

    private fun banAppealEmbed(channel: TextChannel): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Ban Appeal Form")
        embedBuilder.setDescription("""
            If you'd like to appeal your ban, please fill out this form.
            
            **Username:**
            **Reason for ban:**
            **Why should your ban be lifted?:**
            **Is there anything else you'd like to say?:**
            
            Staff will review your appeal and get back to you soon.
        """.trimIndent())
        embedBuilder.setColor(16029942)
        return embedBuilder.build()
    }
}