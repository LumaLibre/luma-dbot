package dev.jsinco.luma.discord

import dev.jsinco.discord.framework.FrameWork
import dev.jsinco.discord.framework.console.ConsoleCommandManager
import dev.jsinco.luma.discord.console.RestartConsoleCommand

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        FrameWork.start(Main::class.java)
        ConsoleCommandManager.getInstance().registerCommand(RestartConsoleCommand())
    }
}