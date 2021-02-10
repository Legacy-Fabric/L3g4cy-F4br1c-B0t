package io.github.boogiemonster1o1.legacyfabricbot.command;

import java.util.Optional;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import reactor.core.publisher.Mono;

public interface HelpSupplier {
	Object2ObjectMap<String, HelpSupplier> SUPPLIERS = new Object2ObjectOpenHashMap<>();
	DynamicCommandExceptionType EXCEPTION_TYPE = new DynamicCommandExceptionType((obj) -> () -> "Unknown command " + obj);

	@CanIgnoreReturnValue
	static int printAll(CommandContext<MessageCreateEvent> ctx, Mono<MessageChannel> channelMono) {
		channelMono.flatMap(channel -> channel.createEmbed(spec -> {
			spec.setColor(Color.DISCORD_WHITE);
			spec.setTitle("Commands");
			SUPPLIERS.forEach((str, supp) -> {
				spec.addField(str, "`" + supp.getUsage(ctx) + "`", true);
			});
		})).subscribe();
		return Command.SINGLE_SUCCESS;
	}

	@CanIgnoreReturnValue
	static int printOne(CommandContext<MessageCreateEvent> ctx, Mono<MessageChannel> channelMono, String command) throws CommandSyntaxException {
		HelpSupplier supp = Optional.ofNullable(SUPPLIERS.get(command)).orElseThrow(() -> EXCEPTION_TYPE.create(command));

		channelMono.flatMap(channel -> channel.createEmbed(spec -> {
			spec.setColor(Color.CINNABAR);
			spec.setTitle(command);
			spec.addField("Usage", supp.getUsage(ctx), false);
			spec.addField("Description", "`" + supp.getDescription() + "`", false);
		})).subscribe();
		SUPPLIERS.get(command);
		return Command.SINGLE_SUCCESS;
	}

	String getUsage(CommandContext<MessageCreateEvent> ctx);

	String getDescription();
}
