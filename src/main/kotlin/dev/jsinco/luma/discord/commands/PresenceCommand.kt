package dev.jsinco.luma.discord.commands

import dev.jsinco.discord.framework.commands.CommandModule
import dev.jsinco.discord.framework.commands.DiscordCommand
import dev.jsinco.discord.framework.settings.Settings
import dev.jsinco.luma.discord.Util
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.managers.Presence

@DiscordCommand(name = "presence",
    description = "Set the presence of this bot",
    permission = Permission.ADMINISTRATOR)
class PresenceCommand : CommandModule {

    override fun persistRegistration(): Boolean = true

    override fun execute(event: SlashCommandInteractionEvent) {
        val status: OnlineStatus? = Util.getEnumByName(
            OnlineStatus::class.java,
            Util.getOption(event.getOption("status"), OptionType.STRING, "ONLINE") ?: "ONLINE"
        )
        val activityType: Activity.ActivityType? = Util.getEnumByName(
            Activity.ActivityType::class.java,
            Util.getOption(event.getOption("activity-type"), OptionType.STRING) ?: "PLAYING"
        )
        val activity: String? = Util.getOption(event.getOption("activity"), OptionType.STRING, "LumaMC")

        val presence: Presence = event.jda.presence
        presence.setStatus(status)
        presence.activity =
            if (activityType == null) null else Activity.of(activityType, activity ?: "")

        val settings = Settings.getInstance()
        settings.defaultStatus = status
        settings.defaultActivityType = activityType
        settings.defaultActivity = activity
        settings.save()
        event.reply("Presence updated.").queue()
    }

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(
                OptionType.STRING,
                "status",
                "The online status of this bot.",
                true
            ).addChoices(
                Util.buildChoicesFromEnum(
                    OnlineStatus::class.java, "UNKNOWN"
                )
            ),
            OptionData(
                OptionType.STRING,
                "activity-type",
                "The type of activity this bot is doing.",
                false
            ).addChoices(
                Util.buildChoicesFromEnum(
                    Activity.ActivityType::class.java
                )
            ),
            OptionData(
                OptionType.STRING,
                "activity",
                "The activity this bot is doing.",
                false
            )
        )
    }
}