package io.github.boogiemonster1o1.legacyfabricbot.command;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class KickCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("kick").then(
                        argument(
                                "id",
                                StringArgumentType.string()
                        )
                        .executes(KickCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        String value = StringArgumentType.getString(ctx, "id");
        MessageCreateEvent event = ctx.getSource();
        if (event.getMember().orElseThrow().getBasePermissions().block().asEnumSet().contains(Permission.KICK_MEMBERS)) {
            event.getGuild().block().kick(Snowflake.of(value));
            return 0;
        }
        throw new SimpleCommandExceptionType(() -> "You do not have the permission to run this command!").create();
    }
}
