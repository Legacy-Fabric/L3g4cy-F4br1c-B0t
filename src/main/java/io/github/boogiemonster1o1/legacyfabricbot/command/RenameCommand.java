package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class RenameCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("rename")
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
                                argument("value", StringArgumentType.greedyString())
                                        .executes(RenameCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) {
        String value = StringArgumentType.getString(ctx, "value");
        ctx.getSource().getMessage().getChannel().flatMap(channel -> ((TextChannel) channel).edit(spec -> {
            spec.setName(value);
        })).subscribe();
        return 0;
    }
}
