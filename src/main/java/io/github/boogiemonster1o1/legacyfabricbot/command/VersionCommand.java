package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.annotation.Nonnull;

import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.util.IteratorIterable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class VersionCommand {
    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        dispatcher.register(
                literal("ver")
                        .executes(VersionCommand::execute)
        );
    }

    private static int execute(CommandContext<MessageCreateEvent> ctx) {
        try {
            URL url = new URL("http://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/yarn/maven-metadata.xml");
            Document doc = new SAXReader().read(url);
            Element root = doc.getRootElement();
            root.elementIterator();

        } catch (MalformedURLException | DocumentException e) {
            throw new AssertionError(e);
        }
        return 0;
    }
}
