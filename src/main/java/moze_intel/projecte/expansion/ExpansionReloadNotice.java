package moze_intel.projecte.expansion;

import java.util.function.Supplier;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * The notice ProjectE's emc value commands show after they modified an emc value.
 * <p>
 * If the expansion's {@code /px reloademc} command is enabled, the notice points at that command,
 * otherwise ProjectE's own reload notice is used.
 */
public final class ExpansionReloadNotice {

	private static final String RELOAD_COMMAND = "/px reloademc";

	private ExpansionReloadNotice() {}

	/**
	 * @return The supplier of the notice to send after an emc value was modified
	 */
	public static Supplier<Component> get() {
		if (!ExpansionSettings.reloadEmcCommandNotice()) {
			return PELang.RELOAD_NOTICE::translate;
		}
		return () -> Lang.Commands.RELOAD_NOTICE.translate(Component.literal(RELOAD_COMMAND).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
				.withClickEvent(new ClickEvent.SuggestCommand(RELOAD_COMMAND))));
	}
}
