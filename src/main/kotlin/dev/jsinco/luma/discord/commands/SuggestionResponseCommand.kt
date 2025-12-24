package dev.jsinco.luma.discord.commands

import dev.jsinco.discord.framework.commands.CommandModule
import dev.jsinco.discord.framework.commands.DiscordCommand
import dev.jsinco.luma.discord.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

@DiscordCommand(name = "response",
    description = "Respond to a suggestion and notify thread members",
    permission = Permission.ADMINISTRATOR)
class SuggestionResponseCommand : CommandModule {

    override fun execute(event: SlashCommandInteractionEvent) {
        val responseType: ResponseType = Util.getEnumByName(
            ResponseType::class.java,
            Util.getOption(event.getOption("type"), OptionType.STRING, null)
        ) ?: ResponseType.UNDECIDED

        event.deferReply().queue()

        val sender = event.member ?: return
        val channel = event.channel.asThreadChannel()

        val parentMessage = channel.retrieveParentMessage().complete()
        val newEmbeds = setParentEmbedColor(parentMessage.embeds, responseType)
        parentMessage.editMessageEmbeds(newEmbeds).queue()

        val threadMembers = channel.retrieveThreadMembers()
            .complete()
            .filter { member -> !member.user.isBot && member.id != event.jda.selfUser.id && member.id != sender.id }

        val responseEmbed = createResponseMessage(
            members = threadMembers.mapNotNull { it.member },
            responseType = responseType
        )

        event.hook.sendMessageEmbeds(responseEmbed).queue()
    }

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(
                OptionType.STRING,
                "type",
                "The type of response to send.",
                true
            ).addChoices(
                Util.buildChoicesFromEnum(ResponseType::class.java)
            )
        )
    }

    fun setParentEmbedColor(embeds: List<MessageEmbed>, responseType: ResponseType): List<MessageEmbed> {
        val newEmbeds = mutableListOf<MessageEmbed>()
        for (embed in embeds) {
            val embedBuilder = EmbedBuilder(embed)
                .setColor(Util.hexToIntColor(responseType.hex))
            newEmbeds.add(embedBuilder.build())
        }
        return newEmbeds
    }

    fun createResponseMessage(members: List<Member>, responseType: ResponseType): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Suggestion Response: ${Util.formatEnumName(responseType.name)}")
        embedBuilder.setDescription(responseType.definition)

        if (members.isNotEmpty()) {
            embedBuilder.addField("Thread Watchers", members.joinToString(", ") { "<@${it.id}>" }, false)
        }

        embedBuilder.setColor(Util.hexToIntColor(responseType.hex))
        return embedBuilder.build()
    }


    enum class ResponseType(val hex: String, val definition: String) {
        ACCEPTED(
            hex = "#71F35D",
            definition = "This suggestion has been accepted and has been or will be implemented in the near future. **Thank you for your suggestion!**"
        ),
        DENIED(
            hex ="#F86461",
            definition = """
                This suggestion has been denied. There are various reasons why your suggestion may have been denied:
                
                - **The suggestion does not fit well in Luma's gameplay loop or overall vision.**
                - **The suggestion is too similar to an existing feature.**
                - **Your suggestion does not have enough detail or explanation to be properly evaluated.**
                - **The suggestion may have potential, but requires significant changes or improvements before it can be considered.**
                
                Please be aware that this list is not exhaustive, and there may be other reasons why this suggestion was denied.
            """.trimIndent()
        ),
        UNDECIDED(
            hex = "#F6E866",
            definition = """
                This suggestion has potential but requires further consideration, refinement, or a decent amount of work before it can be implemented. Some possible reasons for this designation include:
                
                - **The suggestion needs more detail or explanation to be properly evaluated.**
                - **The suggestion may have potential, but requires significant changes or improvements before it can be considered.**
                - **The suggestion is dependent on other features or systems that are not yet in place.**
                
                The suggestion is still under review, and may be revisited in the future as circumstances change.
            """.trimIndent()
        ),
        NO_ETA(
            hex = "#6F8BEA",
            definition = """
                This suggestion has been accepted, was already planned, or is in development, but there is currently no estimated time of arrival (ETA) for its implementation. There may be various reasons for this, such as:
                
                - **Luma staff are prioritizing other features or tasks at the moment.**
                - **The suggestion requires significant development work or resources that are not currently available.**
                - **The suggestion is dependent on other features or systems that are not yet in place.**
                
                While there is no ETA at this time, the suggestion may still be implemented in the future as circumstances change.
            """.trimIndent()
        ),
        IMPOSSIBLE(
            hex ="#47535F",
            definition = """
                This suggestion is currently impossible to implement due to technical limitations, resource constraints, or other factors. Some possible reasons for this designation include:
                
                - **This suggestion goes past Minecraft server capabilities or limitations. (e.g. The game runs this behavior on the client)**
                - **This suggestion would require significant changes to an existing plugin and the work required outweighs the benefit of the suggestion.**
                - **This suggestion would require patching the server directly or bytecode manipulation to achieve the desired effect. (We're sticking to plugin development!)**
                
                This suggestion is not feasible at this point in time. However, it may be revisited after future Minecraft updates or if other circumstances change.
            """.trimIndent()
        )
    }
}