package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.HelpSupplier;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class HttpCatCommand {
	private static LiteralCommandNode<MessageCreateEvent> NODE;
	public static final HelpSupplier HELP_SUPPLIER = new HelpSupplier() {
		@Override
		public String getUsage(CommandContext<MessageCreateEvent> ctx) {
			return NODE.getLiteral() + " " + String.join("\n", LegacyFabricBot.getInstance().getCommandManager().getDispatcher().getAllUsage(NODE, ctx.getSource(), false));
		}

		@Override
		public String getDescription() {
			return "Shows a picture of a cat.";
		}
	};

	public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
		NODE = dispatcher.register(
				literal("httpcat")
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
						)
		);
		HelpSupplier.SUPPLIERS.put(NODE.getLiteral(), HELP_SUPPLIER);
	}
}
