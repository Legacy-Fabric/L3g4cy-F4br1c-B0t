package io.github.boogiemonster1o1.legacyfabricbot.command;

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
        if (ctx.getSource().getMember().orElseThrow().getUsername().equals("BoogieMonster1O1") && ctx.getSource().getMember().orElseThrow().getDiscriminator().equals("2458")) {
            System.exit(0);
            return 0;
        }
        throw new SimpleCommandExceptionType(() -> "You do not have the permission to run this command!").create();
    }
}
