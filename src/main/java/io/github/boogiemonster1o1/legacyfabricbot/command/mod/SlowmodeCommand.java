package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;
import io.github.boogiemonster1o1.legacyfabricbot.util.Utils;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class SlowmodeCommand extends ModCommand {
	private static final Integer MAX = 21600;

	public SlowmodeCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("slowmode")
				.then(
						argument("time", IntegerArgumentType.integer())
								.executes(ctx -> {
									CommandManager.checkPerm(ctx.getSource());
									int time = Utils.clamp(IntegerArgumentType.getInteger(ctx, "time"), 0, MAX);
									ctx.getSource().getMessage().getChannel().flatMap(channel -> ((TextChannel) channel).edit(spec -> {
										spec.setRateLimitPerUser(time);
									})).subscribe();
									ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Set slowmode to " + time)).subscribe();
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new SlowmodeCommand(node));
	}
}
