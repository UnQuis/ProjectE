package moze_intel.projecte.expansion.gui;

import java.math.BigInteger;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.expansion.util.EMCFormat;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.TranslatableEnum;
import net.neoforged.neoforge.event.level.LevelEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = PECore.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
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
	public static void onTick(ClientTickEvent.Post event) {
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

	@EventBusSubscriber(modid = PECore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class Overlay implements GuiLayer {
		@SubscribeEvent
		public static void onRegisterLayers(RegisterGuiLayersEvent event) {
			event.registerAboveAll(PECore.rl("emc_display"), INSTANCE);
		}

		@Override
		public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
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

			//Note: 26.3: GuiGraphics#drawString became GuiGraphicsExtractor#text, which always has a drop shadow flag
			guiGraphics.text(mc.font, text, x, y, 0xffffffff, true);
		}
	}

	public enum EmcDisplayPosition implements TranslatableEnum, ILangEntry {
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

		@Override
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

