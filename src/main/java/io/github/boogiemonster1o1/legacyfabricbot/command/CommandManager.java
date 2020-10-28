package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.time.Instant;
import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;
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

public class CommandManager {
    private final CommandDispatcher<MessageCreateEvent> dispatcher;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        this.register(ApodCommand::register);
        this.register(StopCommand::register);
        this.register(YarnVersionCommand::register);
        this.register(SlowmodeCommand::register);
        this.register(RenameCommand::register);
        this.register(MuteCommand::register);
        this.register(UnmuteCommand::register);
        LegacyFabricBot.getInstance().getClient()
                .on(MessageCreateEvent.class)
                .filter(event -> event.getMember().isPresent() && !event.getMember().get().isBot())
                .filter(event -> event.getMessage().getContent().charAt(0) == '$' && this.dispatcher.getRoot()
                        .getChildren()
                        .stream()
                        .map(CommandNode::getName)
                        .collect(ImmutableList.toImmutableList())
                        .contains(event.getMessage()
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
            spec.setFooter("Requested by " + event.getMember().get().getMention(), event.getMember().get().getAvatarUrl());
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

    @FunctionalInterface
    interface Factory extends Consumer<CommandDispatcher<MessageCreateEvent>> {
        @Override
        default void accept(CommandDispatcher<MessageCreateEvent> dispatcher) {
            this.register(dispatcher);
        }

        void register(CommandDispatcher<MessageCreateEvent> dispatcher);
    }
}
