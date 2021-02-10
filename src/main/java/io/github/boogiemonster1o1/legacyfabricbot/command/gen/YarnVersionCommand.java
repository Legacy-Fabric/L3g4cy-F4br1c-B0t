package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.DescriptiveCommand;
import io.github.boogiemonster1o1.legacyfabricbot.object.command.Version;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class YarnVersionCommand extends DescriptiveCommand {

	public YarnVersionCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("yv")
				.then(
						argument("version", StringArgumentType.string())
								.executes(ctx -> {
									MessageCreateEvent event = ctx.getSource();
									String ver = ctx.getArgument("version", String.class);
									try {
										URL url = new URL("https://meta.legacyfabric.net/v2/versions/yarn/" + ver);
										List<Version> versions = LegacyFabricBot.OBJECT_MAPPER.readValue(url, Version.VERSION_LIST_TYPE);
										if (versions.isEmpty()) {
											throw new SimpleCommandExceptionType(() -> "Could not get the latest yarn version for Minecraft " + ver + "!").create();
										}
										versions.sort(Comparator.reverseOrder());
										Version latest = versions.get(0);
										event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
											CommandManager.appendFooter(spec, event);
											spec.setTitle("Latest Yarn Version for " + latest.gameVersion);
											spec.setDescription("`" + latest.maven + "`");
										})).subscribe();
									} catch (IOException e) {
										throw new SimpleCommandExceptionType(e::getMessage).create();
									}

									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new YarnVersionCommand(node));
	}

	@Override
	public String getDescription() {
		return "Gets the latest yarn version for the specified minecraft version.";
	}

}
