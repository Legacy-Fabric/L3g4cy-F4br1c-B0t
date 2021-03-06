package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class RenameCommand extends ModCommand {
	public RenameCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("rename")
				.then(
						argument("value", StringArgumentType.string())
								.executes(ctx -> {
									CommandManager.checkPerm(ctx.getSource());
									String value = StringArgumentType.getString(ctx, "value");
									ctx.getSource().getMessage().getChannel().flatMap(channel -> ((TextChannel) channel).edit(spec -> {
										spec.setName(value);
									})).subscribe();
									ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createMessage("Renamed to " + value)).subscribe();
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new RenameCommand(node));
	}
}
