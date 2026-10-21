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

@DiscordCommand(name = "customluma",
    description = "Custom Luma guidelines",
    permission = Permission.MESSAGE_SEND,
    guildOnly = true)
class CustomLumaModule : Module {


    override fun execute(event: SlashCommandInteractionEvent) {
        val embed = customLumaEmbed()
        event.replyEmbeds(embed).queue()
    }


    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        val channel = event.channel as? TextChannel ?: return
        if (event.message.contentRaw.contains("!customluma") && !event.author.isBot) {
            val embed = customLumaEmbed()
            channel.sendMessageEmbeds(embed).queue()
        }
    }

    private fun customLumaEmbed(): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Custom Luma Guidelines")
        embedBuilder.setDescription("""
            **To be approved, your custom Luma request must adhere to the following criteria:**
            - **Must be a Luma**: Your design must be a request for a **Luma**, we will not honor requests for other characters or drawings.
            - **Appropriate Content**: Your Luma must not contain anything that is offensive, political, or sexual in nature.
            - **Art Style**: Our drawings are always 2D with black outlines and a "shine" finish on them.
            - **Luma Anatomy**: Do not ask for us to change the base design or anatomy of our Lumas. For example: "Can you draw it with ears?", "Can you draw it with XYZ?".
            
            **You can request any design, but in order to get results you're happy with we recommend you follow these tips:**
            - **Reference Images**: If you have a specific design in mind, please provide reference images. This will help us understand your vision and create a Luma that matches your expectations.
            - **Clear Description**: Provide a clear and detailed description of your desired Luma design.
            - **Clothing**: Clothing can be difficult to draw because our Lumas are drawn in a specific art style.
                - Some clothing in general cannot work. If you ask for a Luma with a collar or glasses, we're most likely going to reject your request. Lumas don't have necks and they dont have faces for glasses to sit.
            
            **Kataray (the artist)** reserves the right to reject any request they may not be able to draw or are not willing to draw.
            If you make edits or remix your own Luma drawing, we are not obligated to reflect your changes in-game (icons) or in any other places we may showcase your Luma.
        """.trimIndent())
        embedBuilder.setColor(16029942)
        return embedBuilder.build()
    }
}