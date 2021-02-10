package io.github.boogiemonster1o1.legacyfabricbot;

import java.nio.file.Path;
import java.util.Objects;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.config.ConfigManager;
import io.github.boogiemonster1o1.legacyfabricbot.object.config.Config;

public class LegacyFabricBot {
	private static LegacyFabricBot instance;
	private final ConfigManager<Config> configManager;
	private final GatewayDiscordClient client;
	private CommandManager commandManager;

	private LegacyFabricBot(Path configPath) {
		this.configManager = ConfigManager.createJankson(configPath, Config.CODEC, Config.DEFAULT);
		String token = this.getConfig()
				.getTokens()
				.getBotToken();
		this.client = Objects.requireNonNull(
				DiscordClientBuilder.create(token).build().login().block()
		);
	}

	public static void main(String[] args) {
		instance = new LegacyFabricBot(Path.of(System.getProperty("user.dir"), "config.json"));
		instance.init();
		instance.finish();
	}

	private void finish() {
		this.getConfigManager().serializeQuietly();
	}

	private void init() {
		this.client.on(ReadyEvent.class).subscribe(event -> {
			event.getClient().getChannelById(Snowflake.of(782985666988212254L))
					.filter(channel -> channel instanceof TextChannel)
					.map(TextChannel.class::cast)
					.flatMap(textChannel -> textChannel.createMessage(
							"WARNING: This is a development server. Do not use it in a production deployment\n"
									+ "Use a production WSGI server instead\n"
									+ "Debug mode: on\n"
									+ "Running on http://127.0.0.1:5000/"
					)).subscribe();
		});
		this.commandManager = new CommandManager();
		this.client.onDisconnect().block();
	}

	public ConfigManager<Config> getConfigManager() {
		return this.configManager;
	}

	public CommandManager getCommandManager() {
		return this.commandManager;
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
