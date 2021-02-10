package io.github.boogiemonster1o1.legacyfabricbot.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class ModCommand extends AbstractCommand {
	public ModCommand(LiteralCommandNode<MessageCreateEvent> node) {
		super(node);
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean hasDescription() {
		return false;
	}
}
