package moze_intel.projecte.expansion.util;

import moze_intel.projecte.expansion.block.*;
import moze_intel.projecte.expansion.config.Config;
import moze_intel.projecte.expansion.item.ItemCompressedCollector;
import moze_intel.projecte.expansion.registries.ExpansionBlocks;
import moze_intel.projecte.expansion.registries.ExpansionItems;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.PEBlockItem;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public enum Matter implements StringRepresentable, IMatterType {
	BASIC(0, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_GRAY, null, null, DyeColor.GRAY),
	DARK(2, EnumMatterType.DARK_MATTER.getAttackDamageBonus(), EnumMatterType.DARK_MATTER.getSpeed(), EnumMatterType.DARK_MATTER.getChargeModifier(), EnumMatterType.DARK_MATTER.getIncorrectBlocksForDrops(), () -> PEBlocks.DARK_MATTER.getBlock().defaultMapColor(), PEItems.DARK_MATTER, PEBlocks.DARK_MATTER::getBlock, DyeColor.BLACK),
	RED(4, EnumMatterType.RED_MATTER.getAttackDamageBonus(), EnumMatterType.RED_MATTER.getSpeed(), EnumMatterType.RED_MATTER.getChargeModifier(), EnumMatterType.RED_MATTER.getIncorrectBlocksForDrops(), () -> PEBlocks.RED_MATTER.getBlock().defaultMapColor(), PEItems.RED_MATTER, PEBlocks.RED_MATTER::getBlock, DyeColor.RED),
	MAGENTA(4, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_MAGENTA, null, null, DyeColor.MAGENTA),
	PINK(5, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_PINK, null, null, DyeColor.PINK),
	PURPLE(5, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_PURPLE, null, null, DyeColor.PURPLE),
	VIOLET(6, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_PURPLE, null, null, DyeColor.PURPLE),
	BLUE(6, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_BLACK, null, null, DyeColor.BLUE),
	CYAN(7, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_CYAN, null, null, DyeColor.CYAN),
	GREEN(7, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_GREEN, null, null, DyeColor.GREEN),
	LIME(8, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_LIGHT_GREEN, null, null, DyeColor.LIME),
	YELLOW(8, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_YELLOW, null, null, DyeColor.YELLOW),
	ORANGE(9, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_ORANGE, null, null, DyeColor.ORANGE),
	WHITE(9, 0, 0, 0, Util.EMPTY_TAG, null, null, null, DyeColor.WHITE),
	FADING(10, 0, 0, 0, Util.EMPTY_TAG, () -> MapColor.COLOR_BLACK, null, null, DyeColor.GRAY),
	FINAL(10, 0, 0, 0, Util.EMPTY_TAG, null, ExpansionItems.FINAL_STAR_SHARD::get, null, DyeColor.GRAY);

	public final BigDecimal BASE_COLLECTOR_OUTPUT = BigDecimal.valueOf(4L);
	public final BigDecimal BASE_RELAY_BONUS = BigDecimal.valueOf(1L);
	public final BigDecimal BASE_RELAY_TRANSFER = BigDecimal.valueOf(64L);

	public static final Matter[] VALUES = values();
	public static final StringRepresentable.StringRepresentableCodec<Matter> CODEC = StringRepresentable.fromEnum(Matter::values);

	public Matter prev() {
		return VALUES[(ordinal() - 1 + VALUES.length) % VALUES.length];
	}

	public Matter next() {
		return VALUES[(ordinal() + 1) % VALUES.length];
	}

	public static final List<Matter> COMMON_ITEMS = List.of(BASIC, DARK, RED);
	public static final List<Matter> UNCOMMON_ITEMS = List.of(MAGENTA, PURPLE, VIOLET, BLUE);
	public static final List<Matter> RARE_ITEMS = List.of(CYAN, GREEN, LIME, YELLOW);
	public static final List<Matter> EPIC_ITEMS = List.of(ORANGE, WHITE, FADING, FINAL);

	public final String name;
	public final boolean hasItem;
	public final boolean hasBlock;
	public final int level;
	public final BigDecimal collectorOutputBase;
	public final BigDecimal relayBonusBase;
	public final BigDecimal relayTransferBase;
	/**
	 * @deprecated Due to how 1.19.2 config values work, this will not be set to 100 when fluid efficiency is disabled.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public final int fluidEfficiency;
	public final float attackDamage;
	public final float efficiency;
	public final float chargeModifier;
	public final TagKey<Block> incorrectBlockForDrops;
	@Nullable
	public final Supplier<MapColor> mapColor;
	@Nullable
	public final Supplier<Item> existingItem;
	@Nullable
	public final Supplier<Block> existingBlock;
	@Nullable
	private ItemRegistryObject<Item> itemMatter = null;
	@Nullable
	private BlockRegistryObject<BlockPowerFlower, BlockItem> powerFlower = null;
	@Nullable
	private BlockRegistryObject<BlockCollector, BlockItem> collector = null;
	@Nullable
	private ItemRegistryObject<ItemCompressedCollector> itemCompressedCollector = null;
	@Nullable
	private BlockRegistryObject<BlockRelay, BlockItem> relay = null;
	@Nullable
	private BlockRegistryObject<BlockEMCLink, BlockItem> emcLink = null;
	@Nullable
	private BlockRegistryObject<BlockMatter, BlockItem> blockMatterBlock = null;
	private final DyeColor color;

	Matter(int fluidEfficiency, float attackDamage, float efficiency, float chargeModifier, TagKey<Block> incorrectBlockForDrops, @Nullable Supplier<MapColor> mapColor, @Nullable Supplier<Item> existingItem, @Nullable Supplier<Block> existingBlock, DyeColor color) {
		boolean isFinal = name().equals("FINAL"); // we can't access the FINAL member because we're in the constructor
		this.name = name().toLowerCase(Locale.US);
		this.hasItem = existingItem == null && ordinal() != 0;
		this.hasBlock = existingBlock == null && ordinal() != 0 && ordinal() != 15;
		this.level = ordinal() + 1;
		this.collectorOutputBase = getValue(BASE_COLLECTOR_OUTPUT);
		this.relayBonusBase = getValue(BASE_RELAY_BONUS);
		this.relayTransferBase = isFinal ? BigDecimal.valueOf(Long.MAX_VALUE) : getValue(BASE_RELAY_TRANSFER);
		this.fluidEfficiency = fluidEfficiency;
		this.attackDamage = attackDamage;
		this.efficiency = efficiency;
		this.chargeModifier = chargeModifier;
		this.incorrectBlockForDrops = incorrectBlockForDrops;
		this.mapColor = mapColor;
		this.existingItem = existingItem;
		this.existingBlock = existingBlock;
		this.color = color;
	}

	public Rarity getRarity() {
		if (COMMON_ITEMS.contains(this)) return Rarity.COMMON;
		if (UNCOMMON_ITEMS.contains(this)) return Rarity.UNCOMMON;
		if (RARE_ITEMS.contains(this)) return Rarity.RARE;
		if (EPIC_ITEMS.contains(this)) return Rarity.EPIC;
		return Rarity.COMMON;
	}

	public int getLevel() {
		return level;
	}

	private BigDecimal getValue(BigDecimal base) {
		BigDecimal val = base;
		for (int i = 0; i < ordinal(); i++) {
			val = val.multiply(BigDecimal.valueOf(6));
		}

		return val;
	}

	public DyeColor getColor() {
		return color;
	}

	public int getTextColor() {
		return color.getTextColor();
	}

	public int getFluidEfficiencyPercentage() {
		if (!Config.server.enableFluidEfficiency.get()) return 100;
		AtomicInteger efficiency = new AtomicInteger(fluidEfficiency);
		Arrays.stream(VALUES).filter(m -> m.level < level).forEach(m -> efficiency.addAndGet(m.fluidEfficiency));
		return efficiency.get();
	}

	/* Limits */

	public BigInteger getPowerFlowerOutput() {
		return collectorOutputBase.multiply(BigDecimal.valueOf(18)).add(relayBonusBase.multiply(BigDecimal.valueOf(30))).multiply(BigDecimal.valueOf(Config.server.powerflowerMultiplier.get())).toBigInteger();
	}

	public BigInteger getPowerFlowerOutputForTicks(int ticks) {
		if (ticks == 20) return getPowerFlowerOutput();
		BigInteger div20 = getPowerFlowerOutput().divide(BigInteger.valueOf(20));
		return div20.multiply(BigInteger.valueOf(ticks));
	}

	public BigInteger getCollectorOutput() {
		return collectorOutputBase.multiply(BigDecimal.valueOf(Config.server.collectorMultiplier.get())).toBigInteger();
	}

	public BigDecimal getCollectorOutputForTicks(int ticks) {
		if (ticks == 20) return new BigDecimal(getCollectorOutput());
		BigDecimal div20 = new BigDecimal(getCollectorOutput()).divide(BigDecimal.valueOf(20), 3, RoundingMode.UP);
		return div20.multiply(BigDecimal.valueOf(ticks));
	}

	/*
	unless we figure out a way to skip ticks or hard code numbers, dynamically changing the
	 tick rate of these 2 will grossly duplicate emc
	 */

	public BigInteger getRelayBonus() {
		return relayBonusBase.multiply(BigDecimal.valueOf(Config.server.relayBonusMultiplier.get())).toBigInteger();
	}

	public BigInteger getRelayBonusForTicks(int ticks) {
		return getRelayBonus();
	}

	public BigInteger getRelayTransfer() {
		return relayTransferBase.multiply(BigDecimal.valueOf(Config.server.relayTransferMultiplier.get())).toBigInteger();
	}

	public BigInteger getRelayTransferForTicks(int ticks) {
		return getRelayTransfer();
	}

	public int getEMCLinkInventorySize() {
		return level * 3;
	}

	public BigInteger getEMCLinkEMCLimit() {
		return BigDecimal.valueOf(16)
				.pow(level)
				.multiply(BigDecimal.valueOf(Config.server.emcLinkEMCLimitMultiplier.get())).toBigInteger();
	}

	public int getEMCLinkItemLimit() {
		try {
			return BigDecimal.valueOf(2).pow(level - 1).multiply(BigDecimal.valueOf(Config.server.emcLinkItemLimitMultiplier.get())).intValueExact();
		} catch (ArithmeticException ignore) {
			return Integer.MAX_VALUE;
		}
	}

	public int getEMCLinkFluidLimit() {
		try {
			return BigDecimal.valueOf(2).pow(level - 1).multiply(BigDecimal.valueOf(1000)).multiply(BigDecimal.valueOf(Config.server.emcLinkFluidLimitMultiplier.get())).intValueExact();
		} catch (ArithmeticException ignore) {
			return Integer.MAX_VALUE;
		}
	}

	public MutableComponent getFormattedComponent(int value) {
		return getFormattedComponent(BigInteger.valueOf(value));
	}

	public MutableComponent getFormattedComponent(long value) {
		return getFormattedComponent(BigInteger.valueOf(value));
	}

	public MutableComponent getFormattedComponent(BigInteger value) {
		//  && !Screen.hasShiftDown()
		return (equals(FINAL) ? Component.literal("INFINITY") : EMCFormat.getComponent(value)).setStyle(ColorStyle.GREEN);
	}

	public MutableComponent getEMCLinkItemLimitComponent() {
		return getFormattedComponent(getEMCLinkItemLimit());
	}

	public MutableComponent getEMCLinkFluidLimitComponent() {
		return getFormattedComponent(getEMCLinkFluidLimit());
	}

	public MutableComponent getEMCLinkEMCLimitComponent() {
		return getFormattedComponent(getEMCLinkEMCLimit());
	}

	public MutableComponent getRelayTransferComponent() {
		return getFormattedComponent(getRelayTransferForTicks(Config.server.tickDelay.get()));
	}

	/* Registry Objects */

	public @Nullable Item getMatter() {
		return itemMatter == null ? null : itemMatter.get();
	}

	public @Nullable Item getMatterOrExisting() {
		return itemMatter == null ? existingItem == null ? null : existingItem.get() : itemMatter.get();
	}

	public @Nullable BlockPowerFlower getPowerFlower() {
		return powerFlower == null ? null : powerFlower.getBlock();
	}

	public @Nullable BlockItem getPowerFlowerItem() {
		return powerFlower == null ? null : powerFlower.asItem();
	}

	public @Nullable BlockRelay getRelay() {
		return relay == null ? null : relay.getBlock();
	}

	public @Nullable BlockItem getRelayItem() {
		return relay == null ? null : relay.asItem();
	}

	public @Nullable BlockCollector getCollector() {
		return collector == null ? null : collector.getBlock();
	}

	public @Nullable BlockItem getCollectorItem() {
		return collector == null ? null : collector.asItem();
	}

	public @Nullable ItemCompressedCollector getCompressedCollectorItem() {
		return itemCompressedCollector == null ? null : itemCompressedCollector.get();
	}

	public @Nullable BlockEMCLink getEMCLink() {
		return emcLink == null ? null : emcLink.getBlock();
	}

	public @Nullable BlockItem getEMCLinkItem() {
		return emcLink == null ? null : emcLink.asItem();
	}

	/* Registration */

	private void register(RegistrationType reg) {
		switch (reg) {
			case MATTER -> {
				if (hasItem) {
					itemMatter = ExpansionItems.ITEMS.registerSimple(String.format("%s_matter", name), properties -> new Item(properties.rarity(getRarity())));
				}
			}

			case MATTER_BLOCK -> {
				if (hasBlock) {
					blockMatterBlock = ExpansionBlocks.BLOCKS.register(String.format("%s_matter_block", name), properties -> new BlockMatter(BlockMatter.getProperties(properties, this), this), (block, itemProperties) -> PEBlockItem.of(block, itemProperties.rarity(getRarity())));
				}
			}

			case COLLECTOR -> collector = ExpansionBlocks.BLOCKS.register(String.format("%s_collector", name), properties -> new BlockCollector(BlockCollector.getProperties(properties, this), this), (block, itemProperties) -> PEBlockItem.of(block, itemProperties.rarity(getRarity())));

			case COMPRESSED_COLLECTOR -> itemCompressedCollector = ExpansionItems.ITEMS.registerSimple(String.format("%s_compressed_collector", name), properties -> new ItemCompressedCollector(properties, this));
			case POWER_FLOWER -> powerFlower = ExpansionBlocks.BLOCKS.register(String.format("%s_power_flower", name), properties -> new BlockPowerFlower(BlockPowerFlower.getProperties(properties, this), this), (block, itemProperties) -> PEBlockItem.of(block, itemProperties.rarity(getRarity())));
			case RELAY -> relay = ExpansionBlocks.BLOCKS.register(String.format("%s_relay", name), properties -> new BlockRelay(BlockRelay.getProperties(properties, this), this), (block, itemProperties) -> PEBlockItem.of(block, itemProperties.rarity(getRarity())));
			case EMC_LINK -> emcLink = ExpansionBlocks.BLOCKS.register(String.format("%s_emc_link", name), properties -> new BlockEMCLink(BlockEMCLink.getProperties(properties, this), this), (block, itemProperties) -> PEBlockItem.of(block, itemProperties.rarity(getRarity())));
		}
	}

	public static void registerAll() {
		Arrays.stream(RegistrationType.values()).forEach(type -> Arrays.stream(VALUES).forEach(val -> val.register(type)));
	}

	public static void setAllCreativeTab(CreativeModeTab.Output output) {
		Arrays.stream(RegistrationType.values()).forEach(type -> Arrays.stream(VALUES).forEach(val -> val.setCreativeTab(output, type)));
	}

	private void setCreativeTab(CreativeModeTab.Output output, RegistrationType type) {
		if (type == RegistrationType.MATTER && itemMatter != null) output.accept(itemMatter.get());
		if (type == RegistrationType.MATTER_BLOCK && blockMatterBlock != null) output.accept(blockMatterBlock.asItem());
		if (type == RegistrationType.COLLECTOR && collector != null) output.accept(collector.asItem());
		if (type == RegistrationType.COMPRESSED_COLLECTOR && itemCompressedCollector != null) output.accept(itemCompressedCollector.get());
		if (type == RegistrationType.POWER_FLOWER && powerFlower != null) output.accept(powerFlower.asItem());
		if (type == RegistrationType.RELAY && relay != null) output.accept(relay.asItem());
		if (type == RegistrationType.EMC_LINK && emcLink != null) output.accept(emcLink.asItem());
	}

	@Override
	public String getSerializedName() {
		return name.toLowerCase(Locale.US);
	}

	@Override
	public int getMatterTier() {
		return ordinal() - 1;
	}

	@Override
	public float getChargeModifier() {
		return chargeModifier;
	}

	@Override
	public int getUses() {
		return 0;
	}

	@Override
	public float getSpeed() {
		return efficiency;
	}

	@Override
	public float getAttackDamageBonus() {
		return attackDamage;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlockForDrops;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	private enum RegistrationType {
		MATTER,
		MATTER_BLOCK,
		COLLECTOR,
		COMPRESSED_COLLECTOR,
		POWER_FLOWER,
		RELAY,
		EMC_LINK
	}
}
