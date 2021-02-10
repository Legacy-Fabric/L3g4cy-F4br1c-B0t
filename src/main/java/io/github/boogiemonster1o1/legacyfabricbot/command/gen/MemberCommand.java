package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.DescriptiveCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.ModCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.argument.SnowflakeArgumentType;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class MemberCommand extends DescriptiveCommand {
	public MemberCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	@Override
	public String getDescription() {
		return "Shows information about someone";
	}

	@SuppressWarnings("ConstantConditions")
	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("member")
				.then(
						argument("value", new SnowflakeArgumentType())
								.executes(ctx -> {
									Snowflake flake = SnowflakeArgumentType.get(ctx, "value");
									ctx.getSource()
											.getMessage()
											.getGuild()
											.flatMap(guild -> guild.getMemberById(flake))
											.subscribe(member -> {
												ctx.getSource()
														.getMessage()
														.getChannel()
														.flatMap(channel -> channel.createEmbed(spec -> {
															spec.setTitle(member.getDisplayName() + "#" + member.getDiscriminator());
															spec.setImage(member.getAvatarUrl());
															long time = ((member.getId().asLong()) >> 22) + Snowflake.DISCORD_EPOCH;
															spec.addField("Join Time", Date.from(Instant.ofEpochMilli(time)).toGMTString(), false);
															spec.addField("Server Join Time", Date.from(member.getJoinTime()).toGMTString(), false);
														})).subscribe();
											});
									return Command.SINGLE_SUCCESS;
								})
				).build();
		commandManager.register(new MemberCommand(node));
	}
}
