package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StopCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(CommandManager.literal("stop").executes(StopCommand::execute));
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getMember().orElseThrow().getRoleIds().stream().map(Snowflake::asLong).collect(Collectors.toUnmodifiableList()).contains(730903564708478976L)) {
            System.exit(0);
            return 0;
        }
        throw new SimpleCommandExceptionType(() -> "You do not have the permission to run this command!").create();
    }
}
