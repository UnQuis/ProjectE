package moze_intel.projecte.expansion.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.expansion.util.EMCFormat;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.math.BigInteger;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EMCDisplay {
	public static final Overlay INSTANCE = new Overlay();
	public static final int PADDING_X = 2;
	public static final int PADDING_Y = 2;
	private static BigInteger emc = BigInteger.ZERO;
	private static final BigInteger[] history = new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
	private static BigInteger lastEMC = BigInteger.ZERO;
	private static int tick = 0;
	private static int repeatedFailures = 0;

	private static @Nullable LocalPlayer getPlayer() {
		return Minecraft.getInstance().player;
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (ProjectEConfig.client.isLoaded() && !ProjectEConfig.client.expansion.emcDisplay.get()) return;
		LocalPlayer player = getPlayer();
		tick++;
		if (player != null && tick >= 20) {
			tick = 0;
			IKnowledgeProvider provider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (provider == null) {
				++repeatedFailures;
				if (repeatedFailures < 10) {
					PECore.LOGGER.warn("Failed to get provider in EMCDisplay");
				} else if (repeatedFailures == 10) {
					PECore.LOGGER.error("Failed to get provider in EMCDisplay 10 times in a row, assuming error and no longer logging.");
				}
				return;
			}

			repeatedFailures = 0;
			emc = provider.getEmc();
			history[1] = history[0];
			history[0] = emc.subtract(lastEMC);
			lastEMC = emc;
		}
	}

	private static void reset() {
		emc = lastEMC = BigInteger.ZERO;
		tick = 0;
	}

	@SubscribeEvent
	public static void clientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		if (!ProjectEConfig.client.expansion.emcDisplay.get()) return;
		reset();
	}

	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload event) {
		if (!ProjectEConfig.client.expansion.emcDisplay.get()) return;
		reset();
	}

	@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class Overlay implements IGuiOverlay {
		@SubscribeEvent
		public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
			event.registerAboveAll(PECore.rl("emc_display"), INSTANCE);
		}

		@Override
		public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
			if (ProjectEConfig.client.isLoaded() && !ProjectEConfig.client.expansion.emcDisplay.get()) return;
			Minecraft mc = Minecraft.getInstance();
			BigInteger avg = history[0].add(history[1]);
			String str = EMCFormat.format(emc);
			if (!avg.equals(BigInteger.ZERO)) str += " " + (avg.compareTo(BigInteger.ZERO) > 0 ? (ChatFormatting.GREEN + "+") : (ChatFormatting.RED + "-")) + EMCFormat.format(avg.abs()) + "/s";
			String text = String.format("EMC: %s", str);
			int x = PADDING_X, y = PADDING_Y, width = mc.getWindow().getGuiScaledWidth(), height = mc.getWindow().getGuiScaledHeight(), fontWidth = mc.font.width(text), fontHeight = mc.font.lineHeight;

			EmcDisplayPosition position = ProjectEConfig.client.expansion.emcDisplayPosition.get();

			if (position.isRight()) {
				x = width - fontWidth - PADDING_X;
			}
			if (position.isBottom()) {
				y = height - fontHeight - PADDING_Y;
			}

			guiGraphics.drawString(mc.font, text, x, y, 0xffffff);
		}
	}

	//Note: NeoForge 20.4 has no TranslatableEnum, ILangEntry already provides the translated name the config screen uses
	public enum EmcDisplayPosition implements ILangEntry {
		TOP_LEFT(Lang.Configuration.EMC_DISPLAY_POSITION_TOP_LEFT, true, true),
		TOP_RIGHT(Lang.Configuration.EMC_DISPLAY_POSITION_TOP_RIGHT, true, false),
		BOTTOM_LEFT(Lang.Configuration.EMC_DISPLAY_POSITION_BOTTOM_LEFT, false, true),
		BOTTOM_RIGHT(Lang.Configuration.EMC_DISPLAY_POSITION_BOTTOM_RIGHT, false, false);

		private final ILangEntry translation;
		private final boolean top;
		private final boolean left;

		EmcDisplayPosition(ILangEntry translation, boolean top, boolean left) {
			this.translation = translation;
			this.top = top;
			this.left = left;
		}

				public Component getTranslatedName() {
			return this.translation.translate();
		}

		@Override
		public String getTranslationKey() {
			return this.translation.getTranslationKey();
		}

		public boolean isTop() {
			return top;
		}

		public boolean isBottom() {
			return !top;
		}

		public boolean isLeft() {
			return left;
		}

		public boolean isRight() {
			return !left;
		}
	}
}
