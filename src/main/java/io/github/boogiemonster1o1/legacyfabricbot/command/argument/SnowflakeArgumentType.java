package io.github.boogiemonster1o1.legacyfabricbot.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class SnowflakeArgumentType implements ArgumentType<Snowflake> {
	@Override
	public Snowflake parse(StringReader reader) throws CommandSyntaxException {
		String snowflakeStr = reader.readString();
		if (snowflakeStr.startsWith("<@")) {
			snowflakeStr = snowflakeStr.substring(2, snowflakeStr.length() - 1);
		}
		long id;
		try {
			id = Long.parseLong(snowflakeStr);
		} catch (NumberFormatException e) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(reader, snowflakeStr);
		}
		return Snowflake.of(id);
	}

	public static Snowflake get(CommandContext<?> ctx, String name) {
		return ctx.getArgument(name, Snowflake.class);
	}

	public static Mono<Member> getMember(CommandContext<?> ctx, String name, Mono<Guild> guild) {
		return guild.flatMap(g -> g.getMemberById(get(ctx, name)));
	}
}
