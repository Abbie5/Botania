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

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.BrewContainer;
import vazkii.botania.common.block.BotaniaBlocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BotanicalBreweryRecipe implements vazkii.botania.api.recipe.BotanicalBreweryRecipe {
	private final Brew brew;
	private final NonNullList<Ingredient> inputs;

	public BotanicalBreweryRecipe(Brew brew, Ingredient... inputs) {
		this.brew = brew;
		this.inputs = NonNullList.of(Ingredient.EMPTY, inputs);
	}

	public BotanicalBreweryRecipe(Brew brew, List<Ingredient> ingredients) {
		this(brew, ingredients.toArray(Ingredient[]::new));
	}

	@Override
	public boolean matches(RecipeInput inv, @NotNull Level world) {
		List<Ingredient> inputsMissing = new ArrayList<>(inputs);

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) {
				break;
			}

			if (stack.getItem() instanceof BrewContainer) {
				continue;
			}

			boolean matchedOne = false;

			Iterator<Ingredient> iter = inputsMissing.iterator();
			while (iter.hasNext()) {
				Ingredient input = iter.next();
				if (input.test(stack)) {
					iter.remove();
					matchedOne = true;
					break;
				}
			}

			if (!matchedOne) {
				return false;
			}
		}

		return inputsMissing.isEmpty();
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return inputs;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.brewery);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.BREW_SERIALIZER;
	}

	@Override
	public Brew getBrew() {
		return brew;
	}

	@Override
	public int getManaUsage() {
		return brew.getManaCost();
	}

	@Override
	public ItemStack getOutput(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof BrewContainer container)) {
			return new ItemStack(Items.GLASS_BOTTLE); // Fallback...
		}

		return container.getItemForBrew(brew, stack);
	}

	@Override
	public int hashCode() {
		return 31 * brew.hashCode() ^ inputs.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BotanicalBreweryRecipe brewRecipe
				&& brew == brewRecipe.brew
				&& inputs.equals(brewRecipe.inputs);
	}

	public static class Serializer implements RecipeSerializer<BotanicalBreweryRecipe> {
		public static final MapCodec<BotanicalBreweryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				BotaniaAPI.instance().getBrewRegistry().byNameCodec().fieldOf("brew").forGetter(BotanicalBreweryRecipe::getBrew),
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("ingredients").forGetter(BotanicalBreweryRecipe::getIngredients)
		).apply(instance, BotanicalBreweryRecipe::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, BotanicalBreweryRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.registry(BotaniaRegistries.BREWS), BotanicalBreweryRecipe::getBrew,
				Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), BotanicalBreweryRecipe::getIngredients,
				BotanicalBreweryRecipe::new
		);

		@Override
		public MapCodec<BotanicalBreweryRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BotanicalBreweryRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
