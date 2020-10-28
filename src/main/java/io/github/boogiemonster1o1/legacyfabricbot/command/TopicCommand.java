package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.util.Utils;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class TopicCommand {
    private static final Integer MAX = 21600;

    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("topic")
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
                                argument("topic", StringArgumentType.greedyString())
                                        .executes(TopicCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) {
        String topic = StringArgumentType.getString(ctx, "topic");
        ctx.getSource().getMessage().getChannel().flatMap(channel -> ((TextChannel) channel).edit(spec -> {
            spec.setTopic(topic);
        })).subscribe();
        return 0;
    }
}
