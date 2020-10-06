package io.github.boogiemonster1o1.legacyfabricbot.object.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Tokens {
    public static final Codec<Tokens> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("bot_token").forGetter(Tokens::getBotToken),
            Codec.STRING.optionalFieldOf("apod_token", "DEMO_KEY").forGetter(Tokens::getApodToken)
    ).apply(instance, Tokens::new));
    public static final Tokens DEFAULT = new Tokens("discord_bot_token", "DEMO_KEY");
    private final String botToken;
    private final String apodToken;

    public Tokens(String botToken, String apodToken) {
        this.botToken = botToken;
        this.apodToken = apodToken;
    }

    public String getBotToken() {
        return this.botToken;
    }

    public String getApodToken() {
        return this.apodToken;
    }
}
