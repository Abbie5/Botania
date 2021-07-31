/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.item.IAvatarWieldable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TileAvatar extends TileSimpleInventory implements IAvatarTile, Tickable {
	private static final int MAX_MANA = 6400;

	private static final String TAG_ENABLED = "enabled";
	private static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	private static final String TAG_MANA = "mana";
	private static final String TAG_COOLDOWNS = "boostCooldowns";

	private boolean enabled;
	private int ticksElapsed;
	private int mana;
	private final Map<UUID, Integer> boostCooldowns = new HashMap<>();

	public TileAvatar() {
		super(ModTiles.AVATAR);
	}

	@Override
	public void tick() {
		enabled = world.isReceivingRedstonePower(pos);

		ItemStack stack = getItemHandler().getStack(0);
		if (!stack.isEmpty() && stack.getItem() instanceof IAvatarWieldable) {
			IAvatarWieldable wieldable = (IAvatarWieldable) stack.getItem();
			wieldable.onAvatarUpdate(this, stack);
		}

		if (enabled) {
			ticksElapsed++;
		}
	}

	@Override
	public void writePacketNBT(CompoundTag tag) {
		super.writePacketNBT(tag);
		tag.putBoolean(TAG_ENABLED, enabled);
		tag.putInt(TAG_TICKS_ELAPSED, ticksElapsed);
		tag.putInt(TAG_MANA, mana);
		ListTag boostCooldowns = new ListTag();
		for (Map.Entry<UUID, Integer> e : this.boostCooldowns.entrySet()) {
			CompoundTag cmp = new CompoundTag();
			cmp.putUuid("id", e.getKey());
			cmp.putInt("cooldown", e.getValue());
			boostCooldowns.add(cmp);
		}
		tag.put(TAG_COOLDOWNS, boostCooldowns);
	}

	@Override
	public void readPacketNBT(CompoundTag tag) {
		super.readPacketNBT(tag);
		enabled = tag.getBoolean(TAG_ENABLED);
		ticksElapsed = tag.getInt(TAG_TICKS_ELAPSED);
		mana = tag.getInt(TAG_MANA);
		boostCooldowns.clear();
		ListTag boostCooldowns = tag.getList(TAG_COOLDOWNS, 10);
		for (Tag nbt : boostCooldowns) {
			CompoundTag cmp = ((CompoundTag) nbt);
			UUID id = cmp.getUuid("id");
			int cooldown = cmp.getInt("cooldown");
			this.boostCooldowns.put(id, cooldown);
		}
	}

	@Override
	protected SimpleInventory createItemHandler() {
		return new SimpleInventory(1) {
			@Override
			public int getMaxCountPerStack() {
				return 1;
			}
		};
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(3 * MAX_MANA, this.mana + mana);
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return !getItemHandler().getStack(0).isEmpty();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public Inventory getInventory() {
		return getItemHandler();
	}

	@Override
	public Direction getAvatarFacing() {
		return getCachedState().get(Properties.HORIZONTAL_FACING);
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Map<UUID, Integer> getBoostCooldowns() {
		return boostCooldowns;
	}
}
