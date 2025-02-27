/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana.spark;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * A block entity with this capability can have a mana spark attached to it.
 * For the Spark to be allowed to have upgrades, the same block position must also have an ManaPool capability
 */
public interface SparkAttachable {

	ResourceLocation ID = botaniaRL("spark_attachable");

	/**
	 * Can this block have a Spark attached to it. Note that this will not
	 * unattach the Spark if it's changed later.
	 */
	boolean canAttachSpark(ItemStack stack);

	/**
	 * Called when the Spark is attached.
	 */
	default void attachSpark(ManaSpark entity) {}

	/**
	 * Returns how much space for mana is available in this block, normally the total - the current.
	 * Should NEVER return negative values. Make sure to check against that.
	 */
	int getAvailableSpaceForMana();

	/**
	 * Gets the Spark that is attached to this block. A common implementation is
	 * to check for Spark entities above using world.getEntitiesWithinAABB()
	 */
	ManaSpark getAttachedSpark();

	/**
	 * Return true if this Tile no longer requires mana and all Sparks
	 * transferring mana to it should cancel their transfer.
	 */
	boolean areIncomingTranfersDone();

}
