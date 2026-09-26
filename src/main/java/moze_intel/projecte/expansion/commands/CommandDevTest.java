package moze_intel.projecte.expansion.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.fml.loading.FMLEnvironment;

public class CommandDevTest {
	private CommandDevTest() {}

	public static LiteralArgumentBuilder<CommandSourceStack> getArguments() {
		//26.3: FMLEnvironment#production became the isProduction() accessor, like in PECore#debugLog
		return Commands.literal("devtest").requires(ignore -> !FMLEnvironment.isProduction())
				.executes(CommandDevTest::handle);
	}

	@SuppressWarnings({"SameReturnValue", "unused"})
	public static int handle(CommandContext<CommandSourceStack> ctx) {
		return 1;
	}
}
