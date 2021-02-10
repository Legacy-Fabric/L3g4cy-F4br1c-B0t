package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.DescriptiveCommand;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class JohnRobertsCommand extends DescriptiveCommand {
	public JohnRobertsCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("johnroberts")
				.executes(ctx -> {
					ctx.getSource()
							.getMessage()
							.getChannel()
							.flatMap(channel -> channel.createEmbed(spec -> {
								spec.setImage("https://upload.wikimedia.org/wikipedia/commons/4/43/Official_roberts_CJ.jpg");
							})).subscribe();
					ctx.getSource().getMessage().delete().subscribe();
					return Command.SINGLE_SUCCESS;
				}).build();
		commandManager.register(new JohnRobertsCommand(node));
	}

	@Override
	public String getDescription() {
		return "Shows a picture of John roberts.";
	}
}
