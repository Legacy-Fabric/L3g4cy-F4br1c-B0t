package io.github.boogiemonster1o1.legacyfabricbot.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.boogiemonster1o1.legacyfabricbot.LegacyFabricBot;

public abstract class AbstractCommand implements HelpSupplier {
	protected final LiteralCommandNode<MessageCreateEvent> node;

	public AbstractCommand(LiteralCommandNode<MessageCreateEvent> node) {
		this.node = node;
	}

	@Override
	public String getUsage(CommandContext<MessageCreateEvent> ctx) {
		return this.getLiteral() + " " + String.join("\n", LegacyFabricBot.getInstance().getCommandManager().getDispatcher().getAllUsage(this.node, ctx.getSource(), false));
	}

	@Override
	public abstract String getDescription();

	public abstract boolean hasDescription();

	public LiteralCommandNode<MessageCreateEvent> getNode() {
		return this.node;
	}

	public String getLiteral() {
		return this.node.getLiteral();
	}
}
