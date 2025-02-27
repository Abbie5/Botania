/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.processor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.client.patchouli.PatchouliUtils;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RunicAltarProcessor extends ReagentRecipeProcessor<RunicAltarRecipe> {
	@Override
	public void setup(Level level, IVariableProvider variables) {
		ResourceLocation id = ResourceLocation.parse(variables.get("recipe").asString());
		this.recipe = new RecipeHolder<>(id, PatchouliUtils.getRecipe(level, BotaniaRecipeTypes.RUNE_TYPE, id));
	}

	@Override
	public IVariable process(Level level, String key) {
		if (recipe == null) {
			return super.process(level, key);
		}
		if (key.equals("mana")) {
			return IVariable.wrap(recipe.value().getMana());
		}
		return super.process(level, key);
	}
}
