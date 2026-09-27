package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.util.TagNames;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * NBT (de)serialization of the alchemical book teleport locations.
 * <p>
 * 1.20.4 has no stream codecs, so the lists of locations are synced as a plain compound tag holding a list of
 * compound tags, using the per location NBT form that
 * {@link CapabilityAlchemicalBookLocations.TeleportLocation#serialize()} /
 * {@link CapabilityAlchemicalBookLocations.TeleportLocation#deserialize(CompoundTag)} already provide.
 */
public final class TeleportLocationSync {

	private TeleportLocationSync() {
	}

	public static CompoundTag toTag(List<CapabilityAlchemicalBookLocations.TeleportLocation> locations) {
		ListTag list = new ListTag();
		for (CapabilityAlchemicalBookLocations.TeleportLocation location : locations) {
			list.add(location.serialize());
		}
		CompoundTag tag = new CompoundTag();
		tag.put(TagNames.LOCATIONS, list);
		return tag;
	}

	public static List<CapabilityAlchemicalBookLocations.TeleportLocation> fromTag(CompoundTag tag) {
		List<CapabilityAlchemicalBookLocations.TeleportLocation> locations = new ArrayList<>();
		ListTag list = tag.getList(TagNames.LOCATIONS, Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			locations.add(CapabilityAlchemicalBookLocations.TeleportLocation.deserialize(list.getCompound(i)));
		}
		return locations;
	}
}
