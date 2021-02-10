package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;

public class StopCommand extends ModCommand {
	public StopCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = CommandManager.literal("stop")
				.executes(ctx -> {
					CommandManager.checkPerm(ctx.getSource());
					ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Stopping!")).subscribe();
					LegacyFabricBot.getInstance().getClient().logout();
					System.exit(0);
					return Command.SINGLE_SUCCESS;
				}).build();
		commandManager.register(new StopCommand(node));
	}
}
