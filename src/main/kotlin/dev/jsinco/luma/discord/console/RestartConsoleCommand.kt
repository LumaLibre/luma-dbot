package dev.jsinco.luma.discord.console

import dev.jsinco.discord.framework.FrameWork
import dev.jsinco.discord.framework.console.ConsoleCommand
import dev.jsinco.discord.framework.logging.FrameWorkLogger
import dev.jsinco.discord.framework.scheduling.ScheduleManager
import kotlin.system.exitProcess

class RestartConsoleCommand : ConsoleCommand {

    override fun name() = "restart"

    override fun execute(args: Array<out String>) {
        FrameWorkLogger.info("Stopping!")

        for (tickable in ScheduleManager.getInstance().scheduledTasks) {
            tickable.run()
        }

        FrameWork.getJda().shutdownNow()
        exitProcess(1)
    }
}