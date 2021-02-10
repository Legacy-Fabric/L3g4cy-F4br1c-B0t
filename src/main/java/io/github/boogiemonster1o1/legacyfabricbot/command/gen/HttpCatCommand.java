package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.DescriptiveCommand;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class HttpCatCommand extends DescriptiveCommand {
	public HttpCatCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("httpcat")
				.then(
						argument("code", IntegerArgumentType.integer(100, 599))
								.executes(ctx -> {
									ctx.getSource()
											.getMessage()
											.getChannel()
											.flatMap(channel -> channel.createEmbed(spec -> {
												spec.setImage("https://http.cat/" + IntegerArgumentType.getInteger(ctx, "code") + ".jpg");
											})).subscribe();
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new HttpCatCommand(node));
	}

	@Override
	public String getDescription() {
		return "Shows a picture of a cat.";
	}
}
