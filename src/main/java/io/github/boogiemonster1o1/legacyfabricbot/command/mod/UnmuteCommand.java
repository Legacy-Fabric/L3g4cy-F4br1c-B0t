package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class UnmuteCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("unmute")
                        .then(
                                argument("value", StringArgumentType.string())
                                        .executes(UnmuteCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        CommandManager.checkPerm(ctx.getSource());
        String value = StringArgumentType.getString(ctx, "value");
        if (value.startsWith("<&") && value.endsWith(">")) {
            value = value.substring(2, value.length() - 1);
        }
        Snowflake flake = Snowflake.of(value);
        if (ctx.getSource().getMessage().getGuild().flatMap(guild -> guild.getMemberById(flake)).blockOptional().isPresent()) {
            ctx.getSource().getMessage().getGuild().flatMap(guild -> guild.getMemberById(flake).flatMap(member -> member.removeRole(Snowflake.of(LegacyFabricBot.getInstance().getConfig().getRoleSnowflakes().getMuteRole())))).subscribe();
            ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Unmuted " + flake.toString())).subscribe();
            return 0;
        }
        String finalValue = value;
        throw new SimpleCommandExceptionType(() -> "Could not find member with id " + finalValue).create();
    }
}
