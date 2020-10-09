package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import discord4j.core.event.domain.message.MessageCreateEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class YarnVersionCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("yv")
                        .executes(ctx -> execute(ctx, "1.8.9"))
                        .then(
                                argument("version", StringArgumentType.string())
                                        .executes(ctx -> execute(ctx, ctx.getArgument("version", String.class)))
                        )
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx, String ver) throws CommandSyntaxException {
        String version;
        try {
            URL url = new URL("http://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/yarn/maven-metadata.xml");
            Document doc = new SAXReader().read(url);
            Element root = doc.getRootElement();
            String latestVersion = "NULL";
            Iterator<Element> iterator = root.element("versioning").element("versions").elementIterator();
            while (iterator.hasNext()) {
                Element next = iterator.next();
                if (!next.getData().toString().startsWith(ver)) {
                    continue;
                }
                latestVersion = next.getData().toString();
            }
            version = latestVersion;
        } catch (MalformedURLException | DocumentException e) {
            throw new AssertionError(e); // cant happen
        }
        if (version.equals("NULL")) {
            throw new SimpleCommandExceptionType(() -> "Could not get the latest yarn version!").create();
        }
        ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
            CommandManager.appendFooter(spec, ctx.getSource());
            spec.setTitle("Latest Yarn Version");
            spec.addField(version, String.format("`mappings 'net.fabricmc:yarn:%s:v2`", version), false);
        })).subscribe();
        return 0;
    }
}
