package io.github.boogiemonster1o1.legacyfabricbot.object.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Config {
	public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Tokens.CODEC.fieldOf("tokens").forGetter(Config::getTokens),
			RoleSnowflakes.CODEC.fieldOf("role_snowflakes").forGetter(Config::getRoleSnowflakes)
	).apply(instance, Config::new));
	public static final Config DEFAULT = new Config(Tokens.DEFAULT, RoleSnowflakes.DEFAULT);
	private final Tokens tokens;
	private final RoleSnowflakes roleSnowflakes;

	public Config(Tokens tokens, RoleSnowflakes roleSnowflakes) {
		this.tokens = tokens;
		this.roleSnowflakes = roleSnowflakes;
	}

	public Tokens getTokens() {
		return this.tokens;
	}

	public RoleSnowflakes getRoleSnowflakes() {
		return this.roleSnowflakes;
	}
}
