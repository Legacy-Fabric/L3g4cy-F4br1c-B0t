package io.github.boogiemonster1o1.legacyfabricbot.command.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.argument.SnowflakeArgumentType;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class MuteCommand extends ModCommand {
	public MuteCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("mute")
				.then(
						argument("value", new SnowflakeArgumentType())
								.executes(ctx -> {
									CommandManager.checkPerm(ctx.getSource());
									Snowflake flake = SnowflakeArgumentType.get(ctx, "value");
									ctx.getSource()
											.getMessage()
											.getGuild()
											.flatMap(guild -> guild.getMemberById(flake))
											.flatMap(member -> {
												return member.addRole(Snowflake.of(LegacyFabricBot.getInstance().getConfig().getRoleSnowflakes().getMuteRole()));
											})
											.subscribe();
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new MuteCommand(node));
	}
}
