package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.gateway.StatusUpdate;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class StatusCommand extends ModCommand {
	public StatusCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("status")
				.then(
						argument("value", StringArgumentType.greedyString())
								.executes(ctx -> {
									CommandManager.checkPerm(ctx.getSource());
									LegacyFabricBot.getInstance().getClient().updatePresence(StatusUpdate.builder().status(StringArgumentType.getString(ctx, "value")).afk(false).build()).block();
									ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Updated status")).subscribe();
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new StatusCommand(node));
	}
}
