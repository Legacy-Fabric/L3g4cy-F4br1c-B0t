package io.github.boogiemonster1o1.legacyfabricbot;

import java.nio.file.Path;
import java.util.Objects;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.config.ConfigManager;
import io.github.boogiemonster1o1.legacyfabricbot.object.config.Config;

public class LegacyFabricBot {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	public static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();
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
		ReactionEmoji check = ReactionEmoji.unicode("✅");
		ReactionEmoji x = ReactionEmoji.unicode("❌");
		this.client.on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.subscribe(message -> {
					String content = message.getContent();
					if (!content.startsWith("$suggest ")) {
						return;
					}
					String suggestion = content.substring(9);
					message.getGuild()
							.flatMap(g -> g.getChannelById(Snowflake.of(693196904284291102L)))
							.filter(channel -> channel instanceof TextChannel)
							.map(TextChannel.class::cast)
							.flatMap(channel -> channel.createEmbed(spec -> {
								spec.setTitle("Suggestion");
								spec.setDescription(suggestion);
								spec.setColor(Color.BLUE);
							}))
							.subscribe(embed -> {
								embed.addReaction(check).subscribe();
								embed.addReaction(x).subscribe();
							});
					message.delete().subscribe();
				});
		this.client.on(ReactionAddEvent.class)
				.filterWhen(event -> event.getChannel().map(channel -> channel.getId().asLong() == 693196904284291102L))
				.filterWhen(event -> event.getMessage()
						.filter(message -> message.getAuthor().isPresent())
						.map(message -> message.getAuthor().orElseThrow())
						.map(user -> user.getId().asLong() == this.client.getSelfId().asLong())
				)
				.filter(event -> event.getMember().isPresent())
				.filter(event -> !event.getMember().orElseThrow().isBot())
				.subscribe(event -> {
					Member member = event.getMember().orElseThrow();
					if (event.getEmoji().equals(x)){
						event.getMessage()
								.flatMapMany(message -> message.getReactors(check).filter(user -> user.getId().asLong() == member.getId().asLong()))
								.subscribe(user -> {
									event.getMessage().flatMap(m -> m.removeReaction(check, user.getId())).subscribe();
								});
					} else if (event.getEmoji().equals(check)){
						event.getMessage()
								.flatMapMany(message -> message.getReactors(x).filter(user -> user.getId().asLong() == member.getId().asLong()))
								.subscribe(user -> {
									event.getMessage().flatMap(m -> m.removeReaction(x, user.getId())).subscribe();
								});
					}
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
