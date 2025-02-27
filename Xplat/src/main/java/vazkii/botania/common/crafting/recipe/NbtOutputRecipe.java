/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class NbtOutputRecipe<C extends RecipeInput> implements Recipe<C> {
	public static final RecipeSerializer<NbtOutputRecipe<?>> SERIALIZER = new NbtOutputRecipe.Serializer();

	private final Recipe<C> recipe;
	private final CompoundTag nbt;

	public NbtOutputRecipe(Recipe<C> recipe, CompoundTag nbt) {
		this.recipe = recipe;
		this.nbt = nbt;
	}

	@Override
	public boolean matches(C container, Level level) {
		return recipe.matches(container, level);
	}

	@Override
	public ItemStack assemble(C container, HolderLookup.Provider registryAccess) {
		ItemStack result = recipe.assemble(container, registryAccess);
		result.setTag(nbt);
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return recipe.canCraftInDimensions(width, height);
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
		return recipe.getResultItem(registryAccess);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return recipe.getType();
	}

	private static class Serializer implements RecipeSerializer<NbtOutputRecipe<?>> {
		public static final MapCodec<NbtOutputRecipe<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Recipe.CODEC.fieldOf("recipe").forGetter(r -> r.recipe),
				CompoundTag.CODEC.fieldOf("nbt").forGetter(r -> r.nbt)
		).apply(instance, NbtOutputRecipe::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, NbtOutputRecipe<?>> STREAM_CODEC = StreamCodec.composite(
				Recipe.STREAM_CODEC, r -> r.recipe,
				CompoundTag.STREAM_CODEC, r -> r.nbt,
				NbtOutputRecipe::new
		);

		@Override
		public MapCodec<NbtOutputRecipe<?>> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, NbtOutputRecipe<?>> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
