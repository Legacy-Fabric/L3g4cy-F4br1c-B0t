package io.github.boogiemonster1o1.legacyfabricbot.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class StopCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                CommandManager.literal("stop")
                        .executes(StopCommand::execute)
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        CommandManager.checkPerm(ctx.getSource());
        System.exit(0);
        return 0;
    }
}
