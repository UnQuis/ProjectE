package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockTypeDeferredRegister extends PEDeferredRegister<MapCodec<? extends Block>> {

	private final String modid;

	public BlockTypeDeferredRegister(String modid) {
		super(Registries.BLOCK_TYPE, modid);
		this.modid = modid;
	}

	public <BLOCK extends Block> PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BLOCK>> registerSimple(String name, Function<BlockBehaviour.Properties, BLOCK> factory) {
		//Since 26.1, the block's registry key has to be set on the properties before the block is created, otherwise the block throws "Block id not set"
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(modid, name));
		return register(name, () -> BlockBehaviour.simpleCodec(properties -> factory.apply(properties.setId(key))));
	}
}