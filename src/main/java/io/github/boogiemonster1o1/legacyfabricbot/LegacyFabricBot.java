package io.github.boogiemonster1o1.legacyfabricbot;

import java.nio.file.Path;
import java.util.Objects;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import io.github.boogiemonster1o1.legacyfabricbot.config.ConfigManager;
import io.github.boogiemonster1o1.legacyfabricbot.object.Config;

public class LegacyFabricBot {
    private static LegacyFabricBot instance;
    private final ConfigManager<Config> configManager;
    private GatewayDiscordClient client;

    private LegacyFabricBot(Path configPath) {
        this.configManager = ConfigManager.createJankson(configPath, Config.CODEC, Config.DEFAULT);
    }

    public static void main(String[] args) {
        instance = new LegacyFabricBot(Path.of(System.getProperty("user.dir"), "config.json"));
        instance.init();
        instance.finish();
    }

    private void finish() {
        this.client.onDisconnect().block();
    }

    private void init() {
        DiscordClient discordClient = DiscordClient.builder(this.getConfig().getTokens().getBotToken())
                .build();
        this.client = Objects.requireNonNull(discordClient.login().block());
    }

    public ConfigManager<Config> getConfigManager() {
        return this.configManager;
    }

    public GatewayDiscordClient getClient() {
        return this.client;
    }

    public Config getConfig() {
        return this.getConfigManager().getConfig();
    }

    public static LegacyFabricBot getInstance() {
        return instance;
    }
}
