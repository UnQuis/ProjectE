package moze_intel.projecte.expansion.commands;

import java.net.URI;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class CommandWiki {
	private CommandWiki() {}

	public static LiteralArgumentBuilder<CommandSourceStack> getArguments() {
		return Commands.literal("wiki")
				.requires(Permissions.WIKI)
				.executes(CommandWiki::handle);
	}

	@SuppressWarnings("SameReturnValue")
	private static int handle(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSystemMessage(Component.literal(Util.WIKI).withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent.OpenUrl(URI.create(Util.WIKI)))));
		return 1;
	}
}
