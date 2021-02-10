package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.HelpSupplier;
import io.github.boogiemonster1o1.legacyfabricbot.config.JanksonOps;
import io.github.boogiemonster1o1.legacyfabricbot.object.command.Apod;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class ApodCommand {
    private static final Jankson JANKSON = Jankson.builder().build();
    private static LiteralCommandNode<MessageCreateEvent> NODE;
    public static final HelpSupplier HELP_SUPPLIER = new HelpSupplier() {
        @Override
        public String getUsage(CommandContext<MessageCreateEvent> ctx) {
            return NODE.getLiteral() + " " + String.join("\n", LegacyFabricBot.getInstance().getCommandManager().getDispatcher().getAllUsage(NODE, ctx.getSource(), false));
        }

        @Override
        public String getDescription() {
            return "Shows the Astronomy Picture Of the Day. Each day, a different image or photograph of our fascinating universe is featured, along with a brief explanation written by a professional astronomer.";
        }
    };

    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        NODE = dispatcher.register(
                literal("apod").executes(ApodCommand::execute)
        );
        HelpSupplier.SUPPLIERS.put(NODE.getLiteral(), HELP_SUPPLIER);
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) throws CommandSyntaxException {
        MessageCreateEvent event = ctx.getSource();
        String url = "https://api.nasa.gov/planetary/apod?api_key=" + LegacyFabricBot.getInstance().getConfig().getTokens().getApodToken();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            try(InputStream in = conn.getInputStream()) {
                JsonObject jsonObject = JANKSON.load(in);
                Apod apod = Apod.CODEC.decode(JanksonOps.INSTANCE, jsonObject).getOrThrow(false, System.err::println).getFirst();
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
    }
}
