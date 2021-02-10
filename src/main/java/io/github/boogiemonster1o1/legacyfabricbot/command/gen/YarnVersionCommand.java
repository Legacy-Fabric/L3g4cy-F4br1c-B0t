package io.github.boogiemonster1o1.legacyfabricbot.command.gen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import blue.endless.jankson.Jankson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.MoreObjects;
import com.google.gson.JsonArray;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import discord4j.core.object.entity.Member;
import io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager;
import io.github.boogiemonster1o1.legacyfabricbot.command.HelpSupplier;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.argument;
import static io.github.boogiemonster1o1.legacyfabricbot.command.CommandManager.literal;

public class YarnVersionCommand {
    private static LiteralCommandNode<MessageCreateEvent> NODE;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();
    private static final CollectionType VERSION_LIST_TYPE = TYPE_FACTORY.constructCollectionType(List.class, Version.class);
    public static final HelpSupplier HELP_SUPPLIER = new HelpSupplier() {
        @Override
        public String getUsage() {
            return NODE.getUsageText();
        }

        @Override
        public String getDescription() {
            return "Gets the latest yarn version for the specified minecraft version.";
        }
    };

    public static void register(CommandDispatcher<MessageCreateEvent> dispatcher) {
        NODE = dispatcher.register(
                literal("yv")
                        .then(
                                argument("version", StringArgumentType.string())
                                        .executes(ctx -> execute(ctx.getSource(), ctx.getArgument("version", String.class)))
                        )
        );
        HelpSupplier.SUPPLIERS.put(NODE.getLiteral(), HELP_SUPPLIER);
    }

    private static int execute(MessageCreateEvent event, String ver) throws CommandSyntaxException {
        try {
            URL url = new URL("https://meta.legacyfabric.net/v2/versions/yarn/" + ver);
            List<Version> versions = OBJECT_MAPPER.readValue(url, VERSION_LIST_TYPE);
            if (versions.isEmpty()) {
                throw new SimpleCommandExceptionType(() -> "Could not get the latest yarn version for Minecraft " + ver + "!").create();
            }
            versions.sort(Comparator.reverseOrder());
            Version latest = versions.get(0);
            event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
                CommandManager.appendFooter(spec, event);
                spec.setTitle("Latest Yarn Version for " + latest.gameVersion);
                spec.setDescription("`" + latest.maven + "`");
            })).subscribe();
        } catch (IOException e) {
            throw new SimpleCommandExceptionType(e::getMessage).create();
        }

        return Command.SINGLE_SUCCESS;
    }

    private static class Version implements Comparable<Version> {
        public String gameVersion;
        public String separator;
        public long build;
        public String maven;
        public String version;
        public boolean stable;

        @Override
        public int compareTo(Version o) {
            return this.build > o.build ? 1 : -1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Version version1 = (Version) o;
            return this.build == version1.build && this.stable == version1.stable && Objects.equals(this.gameVersion, version1.gameVersion) && Objects.equals(this.separator, version1.separator) && Objects.equals(this.maven, version1.maven) && Objects.equals(this.version, version1.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.gameVersion, this.separator, this.build, this.maven, this.version, this.stable);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("gameVersion", this.gameVersion)
                    .add("separator", this.separator)
                    .add("build", this.build)
                    .add("maven", this.maven)
                    .add("version", this.version)
                    .add("stable", this.stable)
                    .toString();
        }
    }
}
