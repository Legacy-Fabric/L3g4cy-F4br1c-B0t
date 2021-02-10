package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.argument.SnowflakeArgumentType;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class UnmuteCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("unmute")
                        .then(
                                argument("value", new SnowflakeArgumentType())
                                        .executes(UnmuteCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        CommandManager.checkPerm(ctx.getSource());
        Snowflake flake = SnowflakeArgumentType.get(ctx, "value");
        ctx.getSource()
                .getMessage()
                .getGuild()
                .flatMap(guild -> guild.getMemberById(flake))
                .flatMap(member -> {
                    return member.removeRole(Snowflake.of(LegacyFabricBot.getInstance().getConfig().getRoleSnowflakes().getMuteRole()));
                })
                .subscribe();
        return Command.SINGLE_SUCCESS;
    }
}
