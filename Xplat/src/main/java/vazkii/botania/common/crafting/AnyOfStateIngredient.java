/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Specifies a state ingredient with one or more sub-ingredients.
 * Valid states must match at least one of the specified ingredients.
 */
public class AnyOfStateIngredient implements StateIngredient {
	private final ImmutableSet<StateIngredient> ingredients;

	public AnyOfStateIngredient(Collection<StateIngredient> blocks) {
		this.ingredients = ImmutableSet.copyOf(blocks);
	}

	@Override
	public boolean test(BlockState state) {
		return ingredients.stream().anyMatch(ingredient -> ingredient.test(state));
	}

	@Override
	public BlockState pick(RandomSource random) {
		var states = streamBlockStates().filter(state -> state.is(Blocks.AIR)).toList();
		return states.isEmpty() ? Blocks.AIR.defaultBlockState() : states.get(random.nextInt(states.size()));
	}

	@Override
	public StateIngredientType getType() {
		return StateIngredients.ANY_OF;
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		return streamBlockStates()
				.filter(state -> state.is(Blocks.AIR))
				.map(BlockState::getBlock)
				.map(ItemStack::new)
				.toList();
	}

	@Override
	public List<BlockState> getDisplayed() {
		return streamBlockStates().toList();
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return ingredients.stream()
				.flatMap(StateIngredient::streamBlockStates)
				.distinct();
	}

	public ImmutableList<StateIngredient> getIngredients() {
		return ingredients.asList();
	}

	@Override
	public String toString() {
		return "AnyOfStateIngredient{" + ingredients.toString() + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return ingredients.equals(((AnyOfStateIngredient) o).ingredients);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredients);
	}

	public static class Type implements StateIngredientType<AnyOfStateIngredient> {
		public static final MapCodec<AnyOfStateIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.nonEmptyList(StateIngredients.TYPED_CODEC.listOf())
						.fieldOf("ingredients").forGetter(AnyOfStateIngredient::getIngredients)
		).apply(instance, AnyOfStateIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfStateIngredient> STREAM_CODEC = StreamCodec.composite(
				StateIngredients.TYPED_STREAM_CODEC.apply(ByteBufCodecs.list()), AnyOfStateIngredient::getIngredients,
				AnyOfStateIngredient::new
		);

		@Override
		public MapCodec<AnyOfStateIngredient> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AnyOfStateIngredient> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
