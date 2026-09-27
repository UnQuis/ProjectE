package moze_intel.projecte.expansion.util;

import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public interface IChestLike {
	void startOpen(Player player);
	void stopOpen(Player player);
	void recheckOpen();
	float getOpenNess(float partialTicks);
}
