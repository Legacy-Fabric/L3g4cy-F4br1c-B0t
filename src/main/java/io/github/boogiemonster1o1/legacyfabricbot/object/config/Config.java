package io.github.boogiemonster1o1.legacyfabricbot.object.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Config {
    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Tokens.CODEC.fieldOf("tokens").forGetter(Config::getTokens)
    ).apply(instance, Config::new));
    public static final Config DEFAULT = new Config(Tokens.DEFAULT);
    private final Tokens tokens;

    public Config(Tokens tokens) {
        this.tokens = tokens;
    }

    public Tokens getTokens() {
        return this.tokens;
    }
}
