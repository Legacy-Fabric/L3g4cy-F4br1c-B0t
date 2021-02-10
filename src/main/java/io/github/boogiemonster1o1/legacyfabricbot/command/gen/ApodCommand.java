package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import com.fasterxml.jackson.core.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.DescriptiveCommand;
import io.github.boogiemonster1o1.legacyfabricbot.config.JanksonOps;
import io.github.boogiemonster1o1.legacyfabricbot.object.command.Apod;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class ApodCommand extends DescriptiveCommand {
	private static final Jankson JANKSON = Jankson.builder().build();
	private static LiteralCommandNode<MessageCreateEvent> NODE;

	public ApodCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	public static void register(CommandManager commandManager) {
		LiteralCommandNode<MessageCreateEvent> node = literal("apod").executes(ctx -> {
			MessageCreateEvent event = ctx.getSource();
			String url = "https://api.nasa.gov/planetary/apod?api_key=" + LegacyFabricBot.getInstance().getConfig().getTokens().getApodToken();
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
				try (InputStream in = conn.getInputStream()) {
					LegacyFabricBot.OBJECT_MAPPER.reader().readTree(new JsonParser)
					Apod apod = LegacyFabricBot.OBJECT_MAPPER.readValue(in, Apod.class);
					event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
						CommandManager.appendFooter(spec, event);
						String explanation = apod.getExplanation();
						if (explanation.length() > 1024) {
							explanation = explanation.substring(0, 1021).concat("...");
						}
						spec.setColor(Color.VIVID_VIOLET)
								.setTitle("NASA Astronomy Picture of the Day")
								.addField(apod.getTitle(), explanation, false)
								.addField("Link to HD image", apod.getHdurl(), false)
								.setImage(apod.getUrl());
					})).subscribe();
				} catch (SyntaxError e) {
					throw new AssertionError("THIS CAN'T HAPPEN!", e);
				}
			} catch (MalformedURLException e) {
				throw new AssertionError("THIS CAN'T HAPPEN!", e);
			} catch (IOException exception) {
				exception.printStackTrace();
				CommandSyntaxException e = new SimpleCommandExceptionType(exception::getMessage).create();
				e.addSuppressed(exception);
				throw e;
			}
			return Command.SINGLE_SUCCESS;
		}).build();
		commandManager.register(new ApodCommand(node));
	}

	@Override
	public String getDescription() {
		return "Shows the Astronomy Picture Of the Day. Each day, a different image or photograph of our fascinating universe is featured, along with a brief explanation written by a professional astronomer.";
	}
}
