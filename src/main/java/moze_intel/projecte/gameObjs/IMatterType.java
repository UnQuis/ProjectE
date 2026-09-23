package moze_intel.projecte.gameObjs;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

//IMatterType used to extend net.minecraft.world.item.Tier, but that interface was removed in Minecraft 26.1.
//All methods Tier used to provide are now declared here directly; EnumMatterType implements them.
public interface IMatterType {

	float getChargeModifier();

	int getMatterTier();

	int getUses();

	float getSpeed();

	float getAttackDamageBonus();

	TagKey<Block> getIncorrectBlocksForDrops();

	int getEnchantmentValue();
}
