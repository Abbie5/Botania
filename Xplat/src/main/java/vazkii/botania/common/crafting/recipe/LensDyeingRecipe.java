/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Suppliers;
import vazkii.botania.api.mana.BasicLensItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.lens.LensItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class LensDyeingRecipe extends CustomRecipe {
	public static final RecipeSerializer<LensDyeingRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(LensDyeingRecipe::new);

	private final Supplier<List<Ingredient>> dyes = Suppliers.memoize(() -> Arrays.asList(
			Ingredient.of(Items.WHITE_DYE), Ingredient.of(Items.ORANGE_DYE),
			Ingredient.of(Items.MAGENTA_DYE), Ingredient.of(Items.LIGHT_BLUE_DYE),
			Ingredient.of(Items.YELLOW_DYE), Ingredient.of(Items.LIME_DYE),
			Ingredient.of(Items.PINK_DYE), Ingredient.of(Items.GRAY_DYE),
			Ingredient.of(Items.LIGHT_GRAY_DYE), Ingredient.of(Items.CYAN_DYE),
			Ingredient.of(Items.PURPLE_DYE), Ingredient.of(Items.BLUE_DYE),
			Ingredient.of(Items.BROWN_DYE), Ingredient.of(Items.GREEN_DYE),
			Ingredient.of(Items.RED_DYE), Ingredient.of(Items.BLACK_DYE),
			Ingredient.of(BotaniaItems.manaPearl)
	));

	public LensDyeingRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
		boolean foundLens = false;
		boolean foundDye = false;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BasicLensItem && !foundLens) {
					foundLens = true;
				} else if (!foundDye) {
					int color = getStackColor(stack);
					if (color > -1) {
						foundDye = true;
					} else {
						return false;
					}
				} else {
					return false;//This means we have an additional item in the recipe after the lens and dye
				}
			}
		}

		return foundLens && foundDye;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registries) {
		ItemStack lens = ItemStack.EMPTY;
		int color = -1;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BasicLensItem && lens.isEmpty()) {
					lens = stack;
				} else {
					color = getStackColor(stack);//We can assume if its not a lens its a dye because we checked it in matches()
				}
			}
		}

		if (lens.getItem() instanceof BasicLensItem) {
			ItemStack lensCopy = lens.copyWithCount(1);
			LensItem.setLensColor(lensCopy, color);

			return lensCopy;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	private int getStackColor(ItemStack stack) {
		List<Ingredient> dyes = this.dyes.get();
		for (int i = 0; i < dyes.size(); i++) {
			if (dyes.get(i).test(stack)) {
				return i;
			}
		}

		return -1;
	}
}
