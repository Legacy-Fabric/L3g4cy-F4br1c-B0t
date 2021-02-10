package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.ApodCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.HttpCatCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.HttpDogCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.YarnVersionCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.MuteCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.RenameCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.SlowmodeCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.StatusCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.StopCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.UnmuteCommand;

public class CommandManager {
    private final CommandDispatcher<MessageCreateEvent> dispatcher;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        this.register(ApodCommand::register);
        this.register(StatusCommand::register);
        this.register(StopCommand::register);
        this.register(YarnVersionCommand::register);
        this.register(SlowmodeCommand::register);
        this.register(RenameCommand::register);
        this.register(MuteCommand::register);
        this.register(UnmuteCommand::register);
        this.register(HttpCatCommand::register);
        this.register(HttpDogCommand::register);
        this.dispatcher.register(
                literal("help")
                        .executes(ctx -> HelpSupplier.printAll(ctx, ctx.getSource().getMessage().getChannel()))
                        .then(
                                argument("command", StringArgumentType.string())
                                .executes(ctx -> HelpSupplier.printOne(ctx, ctx.getSource().getMessage().getChannel(), StringArgumentType.getString(ctx, "command")))
                        )
        );
        Collection<String> commands = this.dispatcher.getRoot()
                .getChildren()
                .stream()
                .map(CommandNode::getName)
                .collect(ImmutableList.toImmutableList());
        LegacyFabricBot.getInstance().getClient()
                .on(MessageCreateEvent.class)
                .filter(event -> event.getMember().isPresent() && !event.getMember().get().isBot())
                .filter(event -> event.getMessage().getContent().startsWith("$")
                        && commands.contains(event.getMessage()
                        .getContent()
                        .substring(1)
                        .split(" ")[0]))
                .subscribe(event -> {
                    try {
                        this.dispatcher.execute(event.getMessage().getContent().substring(1), event);
                    } catch (CommandSyntaxException e) {
                        error(event, e);
                    }
                });
    }

    public CommandDispatcher<MessageCreateEvent> getDispatcher() {
        return this.dispatcher;
    }

    private void register(Factory factory) {
        factory.register(this.getDispatcher());
    }

    public static LiteralArgumentBuilder<MessageCreateEvent> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<MessageCreateEvent, T> argument(String name, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    public static void appendFooter(EmbedCreateSpec spec, MessageCreateEvent event) {
        if (event.getMember().isPresent()) {
            spec.setFooter("Requested by " + event.getMember().get().getDisplayName() + "#" + event.getMember().get().getDiscriminator(), event.getMember().get().getAvatarUrl());
        }
        spec.setTimestamp(Instant.now());
    }

    public static void error(MessageCreateEvent event, CommandSyntaxException e) {
        event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
            spec.setTitle("Error parsing command: ")
                    .setColor(Color.RED)
                    .setDescription(e.getMessage());
            appendFooter(spec, event);
        })).subscribe();
    }

    public static SimpleCommandExceptionType NO_PERM = new SimpleCommandExceptionType(() -> "You do not have permission to run this command!");

    public static void checkPerm(MessageCreateEvent event) throws CommandSyntaxException {
        if (
                event.getMember().isPresent()
                && !event.getMember().get().isBot()
                && event.getMessage().getChannel().block() instanceof TextChannel
                && event.getMember()
                .get()
                .getRoleIds()
                .stream()
                .map(Snowflake::asLong)
                .collect(Collectors.toSet())
                .contains(
                        LegacyFabricBot.getInstance()
                                .getConfig()
                                .getRoleSnowflakes()
                                .getOpRole()
                )
        ) {
            return;
        }

        throw NO_PERM.create();
    }

    @FunctionalInterface
    interface Factory extends Consumer<CommandDispatcher<MessageCreateEvent>> {
        @Override
        default void accept(CommandDispatcher<MessageCreateEvent> dispatcher) {
            this.register(dispatcher);
        }

        void register(CommandDispatcher<MessageCreateEvent> dispatcher);
    }
}
