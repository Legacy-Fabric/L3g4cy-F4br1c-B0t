package io.github.boogiemonster1o1.legacyfabricbot.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Tokens {
    public static final Codec<Tokens> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("bot_token").forGetter(Tokens::getBotToken)
    ).apply(instance, Tokens::new));
    public static final Tokens DEFAULT = new Tokens("discord_bot_token");
    private final String botToken;

    public Tokens(String botToken) {
        this.botToken = botToken;
    }

    public String getBotToken() {
        return this.botToken;
    }
}
