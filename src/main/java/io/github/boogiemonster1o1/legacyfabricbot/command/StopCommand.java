package io.github.boogiemonster1o1.legacyfabricbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StopCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(CommandManager.literal("stop").executes(StopCommand::execute));
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getMember().orElseThrow().getHighestRole().block().getPermissions().asEnumSet().contains(Permission.KICK_MEMBERS)) {
            System.exit(0);
            return 0;
        }
        throw new SimpleCommandExceptionType(() -> "You do not have the permission to run this command!").create();
    }
}
