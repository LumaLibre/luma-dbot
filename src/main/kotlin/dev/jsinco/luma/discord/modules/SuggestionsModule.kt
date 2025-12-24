package dev.jsinco.luma.discord.modules

import dev.jsinco.discord.framework.FrameWork
import dev.jsinco.discord.framework.commands.DiscordCommand
import dev.jsinco.discord.framework.util.Module
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

@DiscordCommand(
    name = "suggest",
    description = "Make a new suggestion",
    permission = Permission.MESSAGE_SEND,
    guildOnly = true
)
class SuggestionsModule : Module {

    companion object {
        // hardcoding is fine, only used it one specific guild
        private const val SUGGESTIONS_CHANNEL_ID = "1188329205973401621"
        private const val COMMAND_PREFIX = "!suggest "

        private val THREAD_OPEN_REGEX = Regex("^.+?\\s+started a thread:\\s+\\*\\*.+?\\*\\*\\. See all threads\\.$\n")
    }

    override fun persistRegistration(): Boolean = true

    override fun execute(event: SlashCommandInteractionEvent) {
        val suggestion = event.getOption("suggestion")?.asString ?: return
        sendSuggestionEmbed(event.user, suggestion)
        event.reply("Your suggestion has been posted!").queue()
    }

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(OptionType.STRING, "suggestion", "Enter suggestion", true)
        )
    }

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        val contentRaw = event.message.contentRaw

        if (contentRaw.startsWith(COMMAND_PREFIX))  {
            sendSuggestionEmbed(event.author, contentRaw.removePrefix(COMMAND_PREFIX))
        } else if (THREAD_OPEN_REGEX.containsMatchIn(contentRaw) && event.channel.id == SUGGESTIONS_CHANNEL_ID) {
            event.message.delete().queue()
        }
    }


    private fun sendSuggestionEmbed(user: User, txt: String) {
        val channel = FrameWork.getJda().getTextChannelById(SUGGESTIONS_CHANNEL_ID) ?: return

        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Suggestion from ${user.effectiveName}")
        embedBuilder.setDescription(txt)
        embedBuilder.setColor(16029942)
        embedBuilder.setThumbnail(user.effectiveAvatarUrl)
        val message = channel.sendMessageEmbeds(embedBuilder.build()).complete()
        message.addReaction(Emoji.fromUnicode("U+2705")).queue()
        message.addReaction(Emoji.fromUnicode("U+274C")).queue()
    }
}