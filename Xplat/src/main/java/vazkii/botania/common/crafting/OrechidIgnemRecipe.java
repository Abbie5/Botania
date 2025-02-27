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

import net.minecraft.commands.CacheableFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.function.Function;

public class OrechidIgnemRecipe extends OrechidRecipe {
	public OrechidIgnemRecipe(StateIngredient input, StateIngredient output, int weight,
			@Nullable CacheableFunction successFunction, int weightBonus, TagKey<Biome> biomes) {
		super(input, output, weight, successFunction, weightBonus, biomes);
	}

	public OrechidIgnemRecipe(StateIngredient input, StateIngredient output, int weight,
			@Nullable CacheableFunction successFunction) {
		this(input, output, weight, successFunction, 0, null);
	}

	private OrechidIgnemRecipe(OrechidRecipe orechidRecipe) {
		this(orechidRecipe.getInput(), orechidRecipe.getOutput(), orechidRecipe.getWeight(),
				orechidRecipe.getSuccessFunction().orElse(null), orechidRecipe.getWeightBonus(),
				orechidRecipe.getBiomes().orElse(null));
	}

	@NotNull
	@Override
	public RecipeType<? extends vazkii.botania.api.recipe.OrechidRecipe> getType() {
		return BotaniaRecipeTypes.ORECHID_IGNEM_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.ORECHID_IGNEM_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<OrechidIgnemRecipe> {
		public static final MapCodec<OrechidIgnemRecipe> CODEC = BotaniaRecipeTypes.ORECHID_SERIALIZER.codec()
				.xmap(OrechidIgnemRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, OrechidIgnemRecipe> STREAM_CODEC = BotaniaRecipeTypes.ORECHID_SERIALIZER.streamCodec()
				.map(OrechidIgnemRecipe::new, Function.identity());

		@Override
		public MapCodec<OrechidIgnemRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, OrechidIgnemRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
