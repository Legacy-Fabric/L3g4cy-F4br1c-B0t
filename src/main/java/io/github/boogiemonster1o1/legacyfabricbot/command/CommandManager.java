package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.ApodCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.HttpCatCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.HttpDogCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.JohnRobertsCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.MemberCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.YarnVersionCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.MuteCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.RenameCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.SlowmodeCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.StatusCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.StopCommand;
import io.github.boogiemonster1o1.legacyfabricbot.command.mod.UnmuteCommand;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class CommandManager {
	private final CommandDispatcher<MessageCreateEvent> dispatcher;
	private final ObjectList<AbstractCommand> commandList;
	private final Object2ObjectMap<String, AbstractCommand> literal2CommandMap;

	public CommandManager() {
		this.commandList = new ObjectArrayList<>();
		this.literal2CommandMap = new Object2ObjectOpenHashMap<>();
		this.dispatcher = new CommandDispatcher<>();
		ApodCommand.register(this);
		HttpCatCommand.register(this);
		HttpDogCommand.register(this);
		JohnRobertsCommand.register(this);
		YarnVersionCommand.register(this);
		MuteCommand.register(this);
		MemberCommand.register(this);

		RenameCommand.register(this);
		SlowmodeCommand.register(this);
		StatusCommand.register(this);
		StopCommand.register(this);
		UnmuteCommand.register(this);

		this.dispatcher.register(
				literal("help")
						.executes(ctx -> {
                            ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
                                spec.setColor(Color.DISCORD_WHITE);
                                spec.setTitle("Commands");
                                this.commandList.forEach(command -> {
                                    spec.addField(command.getLiteral(), command.getUsage(ctx), true);
                                });
                                CommandManager.appendFooter(spec, ctx.getSource());
                            })).subscribe();
                            return Command.SINGLE_SUCCESS;
                        })
						.then(
								argument("command", StringArgumentType.string())
										.executes(ctx -> {
											String commandStr = ctx.getArgument("command", String.class);
											AbstractCommand command = Optional.ofNullable(this.literal2CommandMap.get(commandStr)).orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create());
											if (!command.hasDescription()) {
												return Command.SINGLE_SUCCESS;
											}
											ctx.getSource().getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
												spec.setColor(Color.CINNABAR);
												spec.setTitle(commandStr);
												spec.addField("Usage", "`" + command.getUsage(ctx) + "`", false);
												spec.addField("Description", command.getDescription(), false);
												CommandManager.appendFooter(spec, ctx.getSource());
											})).subscribe();
											return Command.SINGLE_SUCCESS;
										})
						)
		);
		Collection<String> commands = this.commandList.stream().map(AbstractCommand::getLiteral).collect(Collectors.toSet());
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

	public void register(AbstractCommand abstractCommand) {
		this.commandList.add(abstractCommand);
		this.literal2CommandMap.put(abstractCommand.getLiteral(), abstractCommand);
		this.dispatcher.getRoot().addChild(abstractCommand.getNode());
	}

	public CommandDispatcher<MessageCreateEvent> getDispatcher() {
		return this.dispatcher;
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
}
