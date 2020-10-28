package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;

public class StopCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                CommandManager.literal("stop")
                        .requires(event -> event.getMember().isPresent() && !event.getMember().get().isBot() && event.getMember().get().getRoleIds().stream().map(Snowflake::asLong).collect(Collectors.toSet()).contains(LegacyFabricBot.getInstance().getConfig().getRoleSnowflakes().getOpRole()))
                        .executes(StopCommand::execute)
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) {
        System.exit(0);
        return 0;
    }
}
