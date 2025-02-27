/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;
import vazkii.botania.common.helper.RegistryHelper;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class StateIngredients {
	public static final Codec<StateIngredient> TYPED_CODEC = Codec.lazyInitialized(() -> RegistryHelper
			.getRegistry(BotaniaRegistries.STATE_INGREDIENT_TYPE)
			.byNameCodec()
			.dispatch("type", StateIngredient::getType, StateIngredientType::codec));
	public static final StreamCodec<RegistryFriendlyByteBuf, StateIngredient> TYPED_STREAM_CODEC = ByteBufCodecs
			.registry(BotaniaRegistries.STATE_INGREDIENT_TYPE)
			.dispatch(StateIngredient::getType, StateIngredientType::streamCodec);

	public static final StateIngredientType<BlockTypeIngredient> BLOCK_TYPE = new BlockTypeIngredient.Type();
	public static final StateIngredientType<BlockStateIngredient> BLOCK_STATE = new BlockStateIngredient.Type();
	public static final StateIngredientType<BlockStateIngredient> BLOCK_TAG = new BlockStateIngredient.Type();
	public static final StateIngredientType<AnyOfStateIngredient> ANY_OF = new AnyOfStateIngredient.Type();
	public static final StateIngredientType<AllOfExcludingStateIngredient> ALL_OF_EXCLUDING = new AllOfExcludingStateIngredient.Type();

	public static final StateIngredient NONE = new StateIngredient() {
		@Override
		public boolean test(BlockState state) {
			return true;
		}

		@Override
		public BlockState pick(RandomSource random) {
			throw new UnsupportedOperationException("Should never try to pick from NONE state ingredient");
		}

		@Override
		public StateIngredientType<?> getType() {
			return null;
		}

		@Override
		public List<ItemStack> getDisplayedStacks() {
			return List.of();
		}

		@Override
		public List<BlockState> getDisplayed() {
			return List.of();
		}

		@Override
		public Stream<BlockState> streamBlockStates() {
			return Stream.empty();
		}
	};

	public static void submitRegistrations(BiConsumer<StateIngredientType<?>, ResourceLocation> r) {
		r.accept(BLOCK_TYPE, botaniaRL("block"));
		r.accept(BLOCK_STATE, botaniaRL("state"));
		r.accept(BLOCK_TAG, botaniaRL("tag"));
		r.accept(ANY_OF, botaniaRL("any_of"));
		r.accept(ALL_OF_EXCLUDING, botaniaRL("all_of_excluding"));
	}

	public static StateIngredient of(Block block) {
		return new BlockTypeIngredient(block);
	}

	public static StateIngredient of(BlockState state) {
		return new BlockStateIngredient(state);
	}

	public static StateIngredient of(TagKey<Block> tag) {
		return new BlockTagIngredient(tag);
	}

	public static StateIngredient allOf(StateIngredient... ingredients) {
		return new AllOfExcludingStateIngredient(List.of(ingredients), List.of());
	}

	public static StateIngredient ofExcept(StateIngredient ingredient, StateIngredient... exceptions) {
		return new AllOfExcludingStateIngredient(List.of(ingredient), List.of(exceptions));
	}

	public static StateIngredient allOfExcept(Collection<StateIngredient> ingredients, StateIngredient... exceptions) {
		return new AllOfExcludingStateIngredient(ingredients, List.of(exceptions));
	}

	public static StateIngredient anyOf(StateIngredient... ingredients) {
		return new AnyOfStateIngredient(List.of(ingredients));
	}
}
