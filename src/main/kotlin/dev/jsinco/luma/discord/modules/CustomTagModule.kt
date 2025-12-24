package dev.jsinco.luma.discord.modules

import dev.jsinco.discord.framework.commands.DiscordCommand
import dev.jsinco.discord.framework.util.Module
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent

@DiscordCommand(name = "customtag",
    description = "Custom tag guidelines",
    permission = Permission.MESSAGE_SEND,
    guildOnly = true)
class CustomTagModule : Module {


    override fun execute(event: SlashCommandInteractionEvent) {
        val embed = customTagEmbed()
        event.replyEmbeds(embed).queue()
    }


    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        val channel = event.channel as? TextChannel ?: return
        if (event.message.contentRaw.contains("!customtag")) {
            val embed = customTagEmbed()
            channel.sendMessageEmbeds(embed).queue()
        }
    }

    private fun customTagEmbed(): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Custom Tag Guidelines")
        embedBuilder.setDescription("""
            To be approved, your custom tag request must adhere to the following criteria:

            - **Uniqueness:** The tag name must not already exist on the server.
            - **Appropriate Content:** The tag must be free of any language that is offensive, political, or sexual in nature.
            - **Length:** The tag name must be a minimum of three characters long.
            - **Colour Scheme:** The tag design can use a maximum of one or two colours. (You can choose colours from https://rgb.birdflop.com/)
            - **Recognisability:** The tag must be a common and generally recognisable word.
            - **Format:** The tag must be a single, continuous word with no spaces. It should not be numbers or random characters.
        """.trimIndent())
        embedBuilder.setColor(16029942)
        return embedBuilder.build()
    }
}