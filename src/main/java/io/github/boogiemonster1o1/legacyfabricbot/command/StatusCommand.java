package io.github.boogiemonster1o1.legacyfabricbot.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.gateway.StatusUpdate;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class StatusCommand {
	public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
		dispatcher.register(
				literal("status")
						.then(
								argument("value", StringArgumentType.greedyString())
										.executes(StatusCommand::execute)
						)
		);
	}

	private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
		CommandManager.checkPerm(ctx.getSource());
		LegacyFabricBot.getInstance().getClient().updatePresence(StatusUpdate.builder().status(StringArgumentType.getString(ctx, "value")).afk(false).build()).block();
		ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Updated status")).subscribe();
		return Command.SINGLE_SUCCESS;
	}
}
