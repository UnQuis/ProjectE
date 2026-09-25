package moze_intel.projecte.client;

import com.mojang.math.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import moze_intel.projecte.PECore;
import moze_intel.projecte.client.rendering.item.ShieldISTER;
import moze_intel.projecte.client.rendering.item.TridentISTER;
import moze_intel.projecte.gameObjs.items.KleinStar.KleinTier;
import moze_intel.projecte.gameObjs.registration.INamedEntry;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

public class PEItemModelProvider extends ModelProvider {

	private static final TextureSlot CHEST = TextureSlot.create("chest");
	private static final RangeSelectItemModelProperty ACTIVE_PROPERTY = loadProperty("ActiveProperty");
	private static final RangeSelectItemModelProperty MODE_PROPERTY = loadProperty("ModeProperty");
	private static final RangeSelectItemModelProperty USING_ITEM_PROPERTY = loadProperty("UsingItemProperty");
	//Same transformation vanilla uses for the shield and trident, so our versions are not rendered upside down or mirrored in hand
	private static final Transformation HAND_TRANSFORMATION = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, -1, -1), new Quaternionf());

	public PEItemModelProvider(PackOutput output) {
		super(output, PECore.MODID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		registerBlockParentModels(itemModels, PEBlocks.ALCHEMICAL_BARREL, PEBlocks.ALCHEMICAL_COAL, PEBlocks.MOBIUS_FUEL,
				PEBlocks.AETERNALIS_FUEL, PEBlocks.DARK_MATTER, PEBlocks.RED_MATTER, PEBlocks.DARK_MATTER_PEDESTAL,
				PEBlocks.DARK_MATTER_FURNACE, PEBlocks.RED_MATTER_FURNACE, PEBlocks.COLLECTOR,
				PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR_MK3, PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATACLYSM,
				PEBlocks.TRANSMUTATION_TABLE, PEBlocks.RELAY, PEBlocks.RELAY_MK2, PEBlocks.RELAY_MK3);
		registerGenerated(itemModels, PEItems.CATALYTIC_LENS, PEItems.DESTRUCTION_CATALYST, PEItems.LOW_DIVINING_ROD,
				PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_DIVINING_ROD, PEItems.HYPERKINETIC_LENS,
				PEItems.MERCURIAL_EYE, PEItems.PHILOSOPHERS_STONE, PEItems.REPAIR_TALISMAN, PEItems.TOME_OF_KNOWLEDGE,
				PEItems.TRANSMUTATION_TABLET);
		generated(itemModels, PEItems.ALCHEMICAL_COAL, material("item/fuels/alchemical_coal"));
		generated(itemModels, PEItems.MOBIUS_FUEL, material("item/fuels/mobius"));
		generated(itemModels, PEItems.AETERNALIS_FUEL, material("item/fuels/aeternalis"));
		generated(itemModels, PEItems.DARK_MATTER, material("item/matter/dark"));
		generated(itemModels, PEItems.RED_MATTER, material("item/matter/red"));
		generated(itemModels, PEItems.LOW_COVALENCE_DUST, material("item/covalence_dust/low"));
		generated(itemModels, PEItems.MEDIUM_COVALENCE_DUST, material("item/covalence_dust/medium"));
		generated(itemModels, PEItems.HIGH_COVALENCE_DUST, material("item/covalence_dust/high"));
		generated(itemModels, PEBlocks.INTERDICTION_TORCH, material("block/interdiction_torch"));
		generated(itemModels, PEBlocks.INTERDICTION_LANTERN, material("item/interdiction_lantern"));
		generateAlchemicalBags(itemModels);
		generateChests(itemModels);
		generateRings(itemModels);
		generateKleinStars(itemModels);
		generateGear(itemModels);
		generateShields(itemModels);
		generateTridents(itemModels);
		activeDispatch(itemModels, PEItems.GEM_OF_ETERNAL_DENSITY, material("item/dense_gem_off"),
				() -> ItemModelUtils.plainModel(generatedModel(itemModels, "gem_of_eternal_density_on", material("item/dense_gem_on"), ModelTemplates.FLAT_ITEM)));
		//Note: We don't actually have a manual, but I moved this model over to data gen anyways
		Identifier manual = ModelTemplates.FLAT_ITEM.create(Identifier.fromNamespaceAndPath(PECore.MODID, "item/manual"),
				TextureMapping.layer0(material("item/book")), itemModels.modelOutput);
		itemModels.itemModelOutput.register(manual, new ClientItem(ItemModelUtils.plainModel(manual), ClientItem.Properties.DEFAULT));
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.empty();
	}

	@Override
	public String getName() {
		return "ProjectE Item Models";
	}

	private void generateAlchemicalBags(ItemModelGenerators models) {
		for (DyeColor color : Constants.COLORS) {
			generated(models, PEItems.getBagReference(color), material("item/alchemy_bags/" + color));
		}
	}

	private void generateChests(ItemModelGenerators models) {
		generateChest(models, PEBlocks.ALCHEMICAL_CHEST);
		generateChest(models, PEBlocks.CONDENSER);
		generateChest(models, PEBlocks.CONDENSER_MK2);
	}

	private void generateChest(ItemModelGenerators models,
			moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject<?, ?> block) {
		Identifier model = ExtendedModelTemplateBuilder.builder()
				.parent(PECore.rl("block/base_chest"))
				.requiredTextureSlot(CHEST)
				//Since 26.3 item models need their own particle texture reference
				.requiredTextureSlot(TextureSlot.PARTICLE)
				.build().create(ModelLocationUtils.getModelLocation(block.asItem()),
						new TextureMapping().put(CHEST, material("block/" + block.getName()))
								.put(TextureSlot.PARTICLE, material("block/" + block.getName())), models.modelOutput);
		models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
	}

	private void generateRings(ItemModelGenerators models) {
		Identifier[] zero = flatPair(models, "arcana_zero", "item/rings/arcana_0");
		Identifier[] ignition = flatPair(models, "arcana_ignition", "item/rings/arcana_1");
		Identifier[] harvest = flatPair(models, "arcana_harvest", "item/rings/arcana_2");
		Identifier[] swrg = flatPair(models, "arcana_swrg", "item/rings/arcana_3");
		ItemModel.Unbaked activeZero = modeDispatch(models, zero[0], zero[1], 0);
		ItemModel.Unbaked activeIgnition = modeDispatch(models, ignition[0], ignition[1], 1);
		ItemModel.Unbaked activeHarvest = modeDispatch(models, harvest[0], harvest[1], 2);
		ItemModel.Unbaked activeSwrg = modeDispatch(models, swrg[0], swrg[1], 3);
		models.itemModelOutput.accept(PEItems.ARCANA_RING.asItem(), ItemModelUtils.rangeSelect(ACTIVE_PROPERTY,
				activeZero, ItemModelUtils.override(activeZero, 0), ItemModelUtils.override(activeIgnition, 1)));
		generated(models, PEItems.ARCHANGEL_SMITE, material("item/rings/archangel_smite"));
		activeDispatch(models, PEItems.BLACK_HOLE_BAND, material("item/rings/black_hole_off"),
				() -> generatedUnbakedModel(models, "black_hole_band_on", material("item/rings/black_hole_on"), ModelTemplates.FLAT_ITEM));
		activeDispatch(models, PEItems.BODY_STONE, material("item/rings/body_stone_off"),
				() -> generatedUnbakedModel(models, "body_stone_on", material("item/rings/body_stone_on"), ModelTemplates.FLAT_ITEM));
		generated(models, PEItems.EVERTIDE_AMULET, material("item/rings/evertide_amulet"));
		activeDispatch(models, PEItems.HARVEST_GODDESS_BAND, material("item/rings/harvest_god_off"),
				() -> generatedUnbakedModel(models, "harvest_goddess_band_on", material("item/rings/harvest_god_on"), ModelTemplates.FLAT_ITEM));
		activeDispatch(models, PEItems.IGNITION_RING, material("item/rings/ignition_off"),
				() -> generatedUnbakedModel(models, "ignition_on", material("item/rings/ignition_on"), ModelTemplates.FLAT_ITEM));
		generated(models, PEItems.IRON_BAND, material("item/rings/iron_band"));
		activeDispatch(models, PEItems.LIFE_STONE, material("item/rings/life_stone_off"),
				() -> generatedUnbakedModel(models, "life_stone_on", material("item/rings/life_stone_on"), ModelTemplates.FLAT_ITEM));
		activeDispatch(models, PEItems.MIND_STONE, material("item/rings/mind_stone_off"),
				() -> generatedUnbakedModel(models, "mind_stone_on", material("item/rings/mind_stone_on"), ModelTemplates.FLAT_ITEM));
		activeDispatch(models, PEItems.SOUL_STONE, material("item/rings/soul_stone_off"),
				() -> generatedUnbakedModel(models, "soul_stone_on", material("item/rings/soul_stone_on"), ModelTemplates.FLAT_ITEM));

		Identifier swrgBase = generatedModel(models, PEItems.SWIFTWOLF_RENDING_GALE, material("item/rings/swrg_off"), ModelTemplates.FLAT_ITEM);
		Identifier fly = generatedModel(models, "swiftwolf_rending_gale_fly", material("item/rings/swrg_on1"), ModelTemplates.FLAT_ITEM);
		Identifier repel = generatedModel(models, "swiftwolf_rending_gale_repel", material("item/rings/swrg_on3"), ModelTemplates.FLAT_ITEM);
		Identifier both = generatedModel(models, "swiftwolf_rending_gale_fly_repel", material("item/rings/swrg_on2"), ModelTemplates.FLAT_ITEM);
		models.itemModelOutput.accept(PEItems.SWIFTWOLF_RENDING_GALE.asItem(), ItemModelUtils.rangeSelect(MODE_PROPERTY,
				ItemModelUtils.plainModel(swrgBase), ItemModelUtils.override(ItemModelUtils.plainModel(swrgBase), 0),
				ItemModelUtils.override(ItemModelUtils.plainModel(fly), 1),
				ItemModelUtils.override(ItemModelUtils.plainModel(repel), 2),
				ItemModelUtils.override(ItemModelUtils.plainModel(both), 3)));
		activeDispatch(models, PEItems.VOID_RING, material("item/rings/void_off"),
				() -> generatedUnbakedModel(models, "void_ring_on", material("item/rings/void_on"), ModelTemplates.FLAT_ITEM));
		generated(models, PEItems.VOLCANITE_AMULET, material("item/rings/volcanite_amulet"));
		activeDispatch(models, PEItems.WATCH_OF_FLOWING_TIME, material("item/rings/time_watch_off"),
				() -> generatedUnbakedModel(models, "watch_of_flowing_time_on", material("item/rings/time_watch_on"), ModelTemplates.FLAT_ITEM));
		activeDispatch(models, PEItems.ZERO_RING, material("item/rings/zero_off"),
				() -> generatedUnbakedModel(models, "zero_ring_on", material("item/rings/zero_on"), ModelTemplates.FLAT_ITEM));
	}

	private void generateKleinStars(ItemModelGenerators models) {
		KleinTier[] tiers = KleinTier.values();
		for (int tier = 0; tier < tiers.length; tier++) {
			generated(models, PEItems.getStar(tiers[tier]), material("item/stars/klein_star_" + (tier + 1)));
		}
	}

	private void generateGear(ItemModelGenerators models) {
		armorWithTrim(models, PEItems.DARK_MATTER_HELMET, ArmorType.HELMET, material("item/dm_armor/head"));
		armorWithTrim(models, PEItems.DARK_MATTER_CHESTPLATE, ArmorType.CHESTPLATE, material("item/dm_armor/chest"));
		armorWithTrim(models, PEItems.DARK_MATTER_LEGGINGS, ArmorType.LEGGINGS, material("item/dm_armor/legs"));
		armorWithTrim(models, PEItems.DARK_MATTER_BOOTS, ArmorType.BOOTS, material("item/dm_armor/feet"));
		handheld(models, PEItems.DARK_MATTER_AXE, material("item/dm_tools/axe"));
		handheld(models, PEItems.DARK_MATTER_HAMMER, material("item/dm_tools/hammer"));
		handheld(models, PEItems.DARK_MATTER_HOE, material("item/dm_tools/hoe"));
		handheld(models, PEItems.DARK_MATTER_PICKAXE, material("item/dm_tools/pickaxe"));
		handheld(models, PEItems.DARK_MATTER_SHEARS, material("item/dm_tools/shears"));
		handheld(models, PEItems.DARK_MATTER_SHOVEL, material("item/dm_tools/shovel"));
		handheld(models, PEItems.DARK_MATTER_SWORD, material("item/dm_tools/sword"));
		//Red Matter
		armorWithTrim(models, PEItems.RED_MATTER_HELMET, ArmorType.HELMET, material("item/rm_armor/head"));
		armorWithTrim(models, PEItems.RED_MATTER_CHESTPLATE, ArmorType.CHESTPLATE, material("item/rm_armor/chest"));
		armorWithTrim(models, PEItems.RED_MATTER_LEGGINGS, ArmorType.LEGGINGS, material("item/rm_armor/legs"));
		armorWithTrim(models, PEItems.RED_MATTER_BOOTS, ArmorType.BOOTS, material("item/rm_armor/feet"));
		handheld(models, PEItems.RED_MATTER_AXE, material("item/rm_tools/axe"));
		handheld(models, PEItems.RED_MATTER_HAMMER, material("item/rm_tools/hammer"));
		handheld(models, PEItems.RED_MATTER_HOE, material("item/rm_tools/hoe"));
		handheld(models, PEItems.RED_MATTER_PICKAXE, material("item/rm_tools/pickaxe"));
		handheld(models, PEItems.RED_MATTER_SHEARS, material("item/rm_tools/shears"));
		handheld(models, PEItems.RED_MATTER_SHOVEL, material("item/rm_tools/shovel"));
		handheld(models, PEItems.RED_MATTER_SWORD, material("item/rm_tools/sword"));
		handheld(models, PEItems.RED_MATTER_KATAR, material("item/rm_tools/katar"));
		handheld(models, PEItems.RED_MATTER_MORNING_STAR, material("item/rm_tools/morning_star"));
		//Gem
		armorWithTrim(models, PEItems.GEM_HELMET, ArmorType.HELMET, material("item/gem_armor/head"));
		armorWithTrim(models, PEItems.GEM_CHESTPLATE, ArmorType.CHESTPLATE, material("item/gem_armor/chest"));
		armorWithTrim(models, PEItems.GEM_LEGGINGS, ArmorType.LEGGINGS, material("item/gem_armor/legs"));
		armorWithTrim(models, PEItems.GEM_BOOTS, ArmorType.BOOTS, material("item/gem_armor/feet"));
	}

	private void generateShields(ItemModelGenerators models) {
		generateShieldModel(models, PEItems.DARK_MATTER_SHIELD);
		generateShieldModel(models, PEItems.RED_MATTER_SHIELD);
	}

	private void generateShieldModel(ItemModelGenerators models, ItemLike item) {
		//Since 26.3 the special model element requires a base model that exists, our renderer draws the shield itself, so vanilla's shield item model is used as the base.
		//The hand transformation flips the model like vanilla does for the shield and trident, otherwise they render upside down/mirrored
		Identifier base = Identifier.withDefaultNamespace("item/shield");
		ItemModel.Unbaked normal = ItemModelUtils.specialModel(base, HAND_TRANSFORMATION, new ShieldISTER.Unbaked());
		ItemModel.Unbaked blocking = ItemModelUtils.specialModel(base, HAND_TRANSFORMATION, new ShieldISTER.Unbaked());
		models.itemModelOutput.accept(item.asItem(), ItemModelUtils.rangeSelect(USING_ITEM_PROPERTY, normal,
				ItemModelUtils.override(normal, 0), ItemModelUtils.override(blocking, 1)));
	}

	private void generateTridents(ItemModelGenerators models) {
		generateTridentModel(models, PEItems.DARK_MATTER_TRIDENT);
		generateTridentModel(models, PEItems.RED_MATTER_TRIDENT);
	}

	private void generateTridentModel(ItemModelGenerators models, ItemLike item) {
		Identifier flat = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item.asItem()),
				TextureMapping.layer0(material("item/" + itemName(item))), models.modelOutput);
		ItemModel.Unbaked normal = ItemModelUtils.specialModel(flat, HAND_TRANSFORMATION, new TridentISTER.Unbaked());
		ItemModel.Unbaked throwingModel = ItemModelUtils.specialModel(flat, HAND_TRANSFORMATION, new TridentISTER.Unbaked());
		ItemModel.Unbaked inHand = ItemModelUtils.rangeSelect(USING_ITEM_PROPERTY, normal,
				ItemModelUtils.override(normal, 0), ItemModelUtils.override(throwingModel, 1));
		ItemModel.Unbaked dispatched = ItemModelUtils.select(new net.minecraft.client.renderer.item.properties.select.DisplayContext(), inHand,
				ItemModelUtils.when(List.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED,
						ItemDisplayContext.ON_SHELF), ItemModelUtils.plainModel(flat)));
		models.itemModelOutput.accept(item.asItem(), dispatched);
	}

	private void registerBlockParentModels(ItemModelGenerators models,
			moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject<?, ?>... blocks) {
		for (moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject<?, ?> block : blocks) {
			models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(block.getBlock())));
		}
	}

	private void registerGenerated(ItemModelGenerators models, ItemLike... items) {
		for (ItemLike item : items) {
			generated(models, item, material("item/" + itemName(item)));
		}
	}

	private void generated(ItemModelGenerators models, ItemLike item, Material texture) {
		Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item.asItem()),
				TextureMapping.layer0(texture), models.modelOutput);
		models.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
	}

	private void handheld(ItemModelGenerators models, ItemLike item, Material texture) {
		Identifier model = ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(item.asItem()),
				TextureMapping.layer0(texture), models.modelOutput);
		models.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
	}

	private void armorWithTrim(ItemModelGenerators models, ItemLike item, ArmorType armorType, Material armorTexture) {
		Identifier base = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item.asItem()),
				TextureMapping.layer0(armorTexture), models.modelOutput);
		List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>();
		for (ItemModelGenerators.TrimMaterialData trim : ItemModelGenerators.TRIM_MATERIAL_MODELS) {
			String suffix = trim.palette().suffix();
			Identifier trimModel = ModelLocationUtils.getModelLocation(item.asItem()).withSuffix("_" + suffix + "_trim");
			Material trimTexture = new Material(ItemModelGenerators.prefixForSlotTrim(armorType.getName()).withSuffix("_" + suffix));
			//Since 26.3 trimmable armor without a dye layer uses a two layered item (armor + trim), same as vanilla
			models.generateLayeredItem(trimModel, armorTexture, trimTexture);
			cases.add(ItemModelUtils.when(trim.materialKey(), ItemModelUtils.plainModel(trimModel)));
		}
		models.itemModelOutput.accept(item.asItem(), ItemModelUtils.select(new TrimMaterialProperty(),
				ItemModelUtils.plainModel(base), cases));
	}

	private void activeDispatch(ItemModelGenerators models, ItemLike item, Material offTexture,
			java.util.function.Supplier<ItemModel.Unbaked> onModel) {
		Identifier base = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item.asItem()),
				TextureMapping.layer0(offTexture), models.modelOutput);
		ItemModel.Unbaked off = ItemModelUtils.plainModel(base);
		models.itemModelOutput.accept(item.asItem(), ItemModelUtils.rangeSelect(ACTIVE_PROPERTY, off,
				ItemModelUtils.override(off, 0), ItemModelUtils.override(onModel.get(), 1)));
	}

	private Identifier[] flatPair(ItemModelGenerators models, String name, String texturePath) {
		Identifier off = generatedModel(models, name + "_off", material(texturePath), ModelTemplates.FLAT_ITEM);
		Identifier on = generatedModel(models, name + "_on", material(texturePath + "_on"), ModelTemplates.FLAT_ITEM);
		return new Identifier[]{off, on};
	}

	private ItemModel.Unbaked modeDispatch(ItemModelGenerators models, Identifier off, Identifier on, int mode) {
		ItemModel.Unbaked offModel = ItemModelUtils.plainModel(off);
		ItemModel.Unbaked onModel = ItemModelUtils.plainModel(on);
		if (mode == 0) {
			return ItemModelUtils.rangeSelect(MODE_PROPERTY, offModel, ItemModelUtils.override(offModel, 0));
		}
		return ItemModelUtils.rangeSelect(MODE_PROPERTY, offModel,
				ItemModelUtils.override(offModel, 0), ItemModelUtils.override(onModel, mode));
	}

	private static Identifier generatedModel(ItemModelGenerators models, ItemLike item, Material texture, ModelTemplate template) {
		return template.create(ModelLocationUtils.getModelLocation(item.asItem()), TextureMapping.layer0(texture), models.modelOutput);
	}

	private static Identifier generatedModel(ItemModelGenerators models, String name, Material texture, ModelTemplate template) {
		return template.create(Identifier.fromNamespaceAndPath(PECore.MODID, "item/" + name), TextureMapping.layer0(texture), models.modelOutput);
	}

	private static ItemModel.Unbaked generatedUnbakedModel(ItemModelGenerators models, String name, Material texture, ModelTemplate template) {
		return ItemModelUtils.plainModel(generatedModel(models, name, texture, template));
	}

	private static String itemName(ItemLike item) {
		return item instanceof INamedEntry named ? named.getName()
				: BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
	}

	private static Material material(String path) {
		return new Material(PECore.rl(path));
	}

	private static RangeSelectItemModelProperty loadProperty(String simpleName) {
		try {
			Class<?> propertyClass = Class.forName(PEClient.class.getName() + "$" + simpleName, false, PEClient.class.getClassLoader());
			var constructor = propertyClass.getDeclaredConstructor();
			constructor.trySetAccessible();
			return (RangeSelectItemModelProperty) constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Unable to instantiate ProjectE item model property " + simpleName, e);
		}
	}
}
