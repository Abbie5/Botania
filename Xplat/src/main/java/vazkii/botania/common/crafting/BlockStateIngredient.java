/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record BlockStateIngredient(BlockState state) implements StateIngredient {

	@Override
	public boolean test(BlockState blockState) {
		return this.state == blockState;
	}

	@Override
	public BlockState pick(RandomSource random) {
		return state;
	}

	@Override
	public StateIngredientType<BlockStateIngredient> getType() {
		return StateIngredients.BLOCK_STATE;
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		Block block = state.getBlock();
		if (block.asItem() == Items.AIR) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new ItemStack(block));
	}

	@Nullable
	@Override
	public List<Component> descriptionTooltip() {
		Map<Property<?>, Comparable<?>> map = state.getValues();
		if (map.isEmpty()) {
			return StateIngredient.super.descriptionTooltip();
		}
		List<Component> tooltip = new ArrayList<>(map.size());
		for (Map.Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
			Property<?> key = entry.getKey();
			@SuppressWarnings({ "unchecked", "rawtypes" })
			String name = ((Property) key).getName(entry.getValue());

			tooltip.add(Component.literal(key.getName() + " = " + name).withStyle(ChatFormatting.GRAY));
		}
		return tooltip;
	}

	@Override
	public List<BlockState> getDisplayed() {
		return Collections.singletonList(state);
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return Stream.of(state);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return state == ((BlockStateIngredient) o).state;
	}

	@Override
	public String toString() {
		return "BlockStateIngredient{" + state + "}";
	}

	public static class Type implements StateIngredientType<BlockStateIngredient> {
		public static final MapCodec<BlockStateIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				BlockState.CODEC.fieldOf("state").forGetter(BlockStateIngredient::state)
		).apply(instance, BlockStateIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateIngredient> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT.map(Block::stateById, Block::getId), BlockStateIngredient::state,
				BlockStateIngredient::new
		);

		@Override
		public MapCodec<BlockStateIngredient> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BlockStateIngredient> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
