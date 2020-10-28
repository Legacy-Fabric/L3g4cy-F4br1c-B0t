package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.util.Utils;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class SlowmodeCommand {
    private static final Integer MAX = 21600;

    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("slowmode")
                        .requires(
                                event -> event.getMember().isPresent()
                                        && !event.getMember().get().isBot()
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
                                argument("time", IntegerArgumentType.integer())
                                        .executes(SlowmodeCommand::execute)
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) {
        int time = Utils.clamp(IntegerArgumentType.getInteger(ctx, "time"), 0, MAX);
        ctx.getSource().getMessage().getChannel().flatMap(channel -> ((TextChannel) channel).edit(spec -> {
            spec.setRateLimitPerUser(time);
        })).subscribe();
        return 0;
    }
}
