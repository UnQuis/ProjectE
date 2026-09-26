package moze_intel.projecte.expansion.block.entity;

import com.mojang.serialization.Codec;
import moze_intel.projecte.expansion.util.ColorStyle;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.TagNames;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockEntityOwnable extends BlockEntityBase {
	public UUID owner = new UUID(0L, 0L);
	public String ownerName = "";

	public BlockEntityOwnable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		owner = input.read(TagNames.OWNER, UUIDUtil.CODEC).orElse(owner);
		ownerName = input.read(TagNames.OWNER_NAME, Codec.STRING).orElse(ownerName);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store(TagNames.OWNER, UUIDUtil.CODEC, owner);
		output.putString(TagNames.OWNER_NAME, ownerName);
	}

	public void setOwner(Player player) {
		owner = player.getUUID();
		ownerName = player.getScoreboardName();
		Util.markDirty(this);
	}

	public enum ActivationType {
		DISPLAY_NAME,
		CHECK_OWNERSHIP
	}

	// return true if ownership not checked, or if passed
	public boolean handleActivation(Player player, ActivationType activationType) {
		switch (activationType) {
			case DISPLAY_NAME -> player.displayClientMessage(Component.literal(ownerName), true);
			case CHECK_OWNERSHIP -> {
				if (!owner.equals(player.getUUID())) {
					player.displayClientMessage(Lang.NOT_OWNER.translateColored(ChatFormatting.RED, Component.literal(ownerName).setStyle(ColorStyle.RED)), true);
					return false;
				}
			}
		}

		return true;
	}

	public void handlePlace(@Nullable LivingEntity livingEntity, ItemStack stack) {
		if (livingEntity instanceof Player player) setOwner(player);
	}
}
