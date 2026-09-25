package moze_intel.projecte.network;

import io.netty.buffer.ByteBuf;
import java.math.BigInteger;
import java.util.function.IntFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;

public final class PEStreamCodecs {

	private PEStreamCodecs() {
	}

	public static final StreamCodec<ByteBuf, BigInteger> EMC_VALUE = ByteBufCodecs.STRING_UTF8.map(
			emc -> emc.isEmpty() ? BigInteger.ZERO : new BigInteger(emc),
			BigInteger::toString
	);

	public static final StreamCodec<ByteBuf, InteractionHand> INTERACTION_HAND = ByteBufCodecs.BOOL.map(
			bool -> bool ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
			hand -> hand == InteractionHand.MAIN_HAND
	);

	public static final StreamCodec<ByteBuf, Vec3> VEC_3 = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, Vec3::x,
			ByteBufCodecs.DOUBLE, Vec3::y,
			ByteBufCodecs.DOUBLE, Vec3::z,
			Vec3::new
	);

	public static StreamCodec<RegistryFriendlyByteBuf, ItemStacksResourceHandler> handlerStreamCodec(int handlerSize) {
		return new StreamCodec<>() {
			@Override
			public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull ItemStacksResourceHandler handler) {
				for (int slot = 0; slot < handlerSize; slot++) {
					ItemResource resource = handler.getResource(slot);
					ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(handler.getAmountAsInt(slot)));
				}
			}

			@NotNull
			@Override
			public ItemStacksResourceHandler decode(@NotNull RegistryFriendlyByteBuf buffer) {
				ItemStacksResourceHandler handler = new ItemStacksResourceHandler(handlerSize);
				for (int slot = 0; slot < handlerSize; slot++) {
					ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
					handler.set(slot, ItemResource.of(stack), stack.getCount());
				}
				return handler;
			}
		};
	}

	public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, V[]> array(IntFunction<V[]> factory) {
		return codec -> array(factory, codec, Integer.MAX_VALUE);
	}

	public static <B extends ByteBuf, V> StreamCodec<B, V[]> array(IntFunction<V[]> factory, StreamCodec<? super B, V> codec, int maxSize) {
		return new StreamCodec<>() {
			@Override
			public V @NotNull[] decode(@NotNull B buffer) {
				V[] array = factory.apply(ByteBufCodecs.readCount(buffer, maxSize));
				for (int i = 0; i < array.length; i++) {
					array[i] = codec.decode(buffer);
				}
				return array;
			}

			@Override
			public void encode(@NotNull B buffer, V @NotNull[] array) {
				ByteBufCodecs.writeCount(buffer, array.length, maxSize);
				for (V v : array) {
					codec.encode(buffer, v);
				}
			}
		};
	}
}