package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class UnmuteCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("unmute")
                        .requires(
                                event -> event.getMember().isPresent()
                                        && !event.getMember().get().isBot()
                                        && event.getMessage().getChannel().block() instanceof TextChannel
                                        && event.getMember()
                                        .get()
                                        .getRoleIds()
                                        .stream()
                                        .map(Snowflake::asLong)
                                        .collect(Collectors.toSet())
                                        .contains(
                                                LegacyFabricBot.getInstance()
                                                        .getConfig()
                                                        .getRoleSnowflakes()
                                                        .getOpRole()
                                        )
                        )
                        .then(
                                argument("value", StringArgumentType.string())
                                        .executes(UnmuteCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        String value = StringArgumentType.getString(ctx, "value");
        if (value.startsWith("<&") && value.endsWith(">")) {
            value = value.substring(2, value.length() - 1);
        }
        Snowflake flake = Snowflake.of(value);
        if (ctx.getSource().getMessage().getGuild().flatMap(guild -> guild.getMemberById(flake)).blockOptional().isPresent()) {
            ctx.getSource().getMessage().getGuild().flatMap(guild -> guild.getMemberById(flake).flatMap(member -> member.removeRole(Snowflake.of(LegacyFabricBot.getInstance().getConfig().getRoleSnowflakes().getMuteRole())))).subscribe();
            return 0;
        }
        String finalValue = value;
        throw new SimpleCommandExceptionType(() -> "Could not find member with id " + finalValue).create();
    }
}
