package io.github.boogiemonster1o1.legacyfabricbot.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class DescriptiveCommand extends AbstractCommand {
	public DescriptiveCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	@Override
	public final boolean hasDescription() {
		return true;
	}
}
