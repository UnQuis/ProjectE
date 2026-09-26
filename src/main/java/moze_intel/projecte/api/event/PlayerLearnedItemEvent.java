package moze_intel.projecte.api.event;

import moze_intel.projecte.api.ItemInfo;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the server after a player successfully learned a new item in the transmutation table.
 * <p>
 * This event is fired on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}
 */
public class PlayerLearnedItemEvent extends Event {

	private final Player player;
	private final ItemInfo learnedInfo;

	public PlayerLearnedItemEvent(@NotNull Player player, @NotNull ItemInfo learnedInfo) {
		this.player = player;
		this.learnedInfo = learnedInfo;
	}

	/**
	 * @return The player who learned the item.
	 */
	@NotNull
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The {@link ItemInfo} that was learned.
	 */
	@NotNull
	public ItemInfo getLearnedInfo() {
		return learnedInfo;
	}
}
