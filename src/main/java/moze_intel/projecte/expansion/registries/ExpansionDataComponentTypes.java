package moze_intel.projecte.expansion.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.util.BasicDataComponentTypes;
import moze_intel.projecte.expansion.util.TagNames;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.DataComponentTypeDeferredRegister;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class ExpansionDataComponentTypes {

	public static final DataComponentTypeDeferredRegister DATA_COMPONENT_TYPES = new DataComponentTypeDeferredRegister(PECore.MODID);

	public record OwnerData(UUID uuid, String name) {
		public static final Codec<OwnerData> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(UUIDUtil.CODEC.fieldOf(TagNames.OWNER).forGetter(OwnerData::uuid), Codec.STRING.fieldOf(TagNames.OWNER_NAME).forGetter(OwnerData::name)).apply(instance, OwnerData::new)
		);
		public static final StreamCodec<ByteBuf, OwnerData> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, OwnerData::uuid, ByteBufCodecs.STRING_UTF8, OwnerData::name, OwnerData::new);

		public boolean isNone() {
			return uuid == Util.DUMMY_UUID;
		}
	}

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<OwnerData>> OWNER = DATA_COMPONENT_TYPES.simple("uuid", builder -> builder.persistent(OwnerData.CODEC).networkSynchronized(OwnerData.STREAM_CODEC));
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<CapabilityAlchemicalBookLocations.AlchemicalBookLocationData>> ALCHEMICAL_BOOK_LOCATIONS = DATA_COMPONENT_TYPES.simple("alchemical_book_locations", builder -> builder.persistent(CapabilityAlchemicalBookLocations.AlchemicalBookLocationData.CODEC).networkSynchronized(CapabilityAlchemicalBookLocations.AlchemicalBookLocationData.STREAM_CODEC));
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<BasicDataComponentTypes.LongValue>> LAST_USED = DATA_COMPONENT_TYPES.simple("last_used", builder -> builder.persistent(BasicDataComponentTypes.LongValue.CODEC).networkSynchronized(BasicDataComponentTypes.LongValue.STREAM_CODEC));
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<BasicDataComponentTypes.LongValue>> KNOWLEDGE_GAINED = DATA_COMPONENT_TYPES.simple("knowledge_gained", builder -> builder.persistent(BasicDataComponentTypes.LongValue.CODEC).networkSynchronized(BasicDataComponentTypes.LongValue.STREAM_CODEC));
}
