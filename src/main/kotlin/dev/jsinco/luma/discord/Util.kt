package dev.jsinco.luma.discord

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

object Util {

    fun hexToIntColor(hexColor: String): Int {
        val colorString = hexColor.removePrefix("#")

        return when (colorString.length) {
            8 -> colorString.toLong(radix = 16).toInt()
            6 -> "FF$colorString".toLong(radix = 16).toInt()
            else -> throw IllegalArgumentException("Invalid hex color format: $hexColor")
        }
    }



    // Enum

    fun formatEnumName(name: String): String {
        return name.lowercase().replace('_', ' ').split(' ').joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    fun <E : Enum<E>> getEnumByName(enumClass: Class<E>, name: String?): E? {
        if (name == null) return null
        return try {
            java.lang.Enum.valueOf(enumClass, name.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }

    fun <E : Enum<E>?> buildChoicesFromEnum(enumClass: Class<E>): List<Command.Choice> {
        return buildChoicesFromEnum(enumClass, *arrayOfNulls<String>(0))
    }


    fun <E : Enum<E>?> buildChoicesFromEnum(enumClass: Class<E>, vararg ignore: String?): List<Command.Choice> {
        val ignoreList = listOf(*ignore)
        return enumClass.enumConstants
            .map {
                if (ignoreList.contains(it?.name)) {
                    return@map null
                }
                it?.name?.lowercase()?.let { it1 -> Command.Choice(it1, it.name) }
            }
            .filterNotNull()
    }

    // OptionMappings

    fun <T> getOption(optionMapping: OptionMapping?, optionType: OptionType?): T? {
        return getOption<T?>(optionMapping, optionType, null)
    }

    fun <T> getOption(optionMapping: OptionMapping?, optionType: OptionType?, defaultValue: T): T? {
        if (optionMapping == null) return defaultValue

        return when (optionType) {
            OptionType.UNKNOWN, OptionType.SUB_COMMAND, OptionType.SUB_COMMAND_GROUP -> defaultValue
            OptionType.STRING -> optionMapping.asString as T
            OptionType.INTEGER -> optionMapping.asInt as T
            OptionType.BOOLEAN -> optionMapping.asBoolean as T
            OptionType.CHANNEL -> optionMapping.asChannel as T
            OptionType.ROLE -> optionMapping.asRole as T
            OptionType.MENTIONABLE -> optionMapping.asMentionable as T
            OptionType.ATTACHMENT -> optionMapping.asAttachment as T
            OptionType.USER -> {
                try {
                    optionMapping.asMember as T?
                } catch (e: IllegalStateException) {
                    optionMapping.asUser as T
                }
            }

            OptionType.NUMBER -> {
                try {
                    optionMapping.asDouble as T
                } catch (e: IllegalStateException) {
                    optionMapping.asLong as T
                }
            }

            null -> defaultValue
        }
    }
}