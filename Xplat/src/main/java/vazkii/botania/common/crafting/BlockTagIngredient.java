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

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record BlockTagIngredient(TagKey<Block> tag) implements StateIngredient {

	public Stream<Block> resolve() {
		return StreamSupport.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(tag).spliterator(), false)
				.map(Holder::value);
	}

	@Override
	public boolean test(BlockState state) {
		return state.is(tag);
	}

	@Override
	public BlockState pick(RandomSource random) {
		var blocks = getDisplayed();
		if (blocks.isEmpty()) {
			return Blocks.AIR.defaultBlockState();
		}
		return blocks.get(random.nextInt(blocks.size()));
	}

	@Override
	public StateIngredientType getType() {
		return StateIngredients.BLOCK_TAG;
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		return resolve()
				.filter(b -> b.asItem() != Items.AIR)
				.map(ItemStack::new)
				.toList();
	}

	@Override
	public List<BlockState> getDisplayed() {
		return streamBlockStates().toList();
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return resolve().map(Block::defaultBlockState);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return tag.equals(((BlockTagIngredient) o).tag);
	}

	@Override
	public String toString() {
		return "BlockTagIngredient{" + tag + "}";
	}

	public static class Type implements StateIngredientType<BlockTagIngredient> {
		public static final MapCodec<BlockTagIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				TagKey.hashedCodec(Registries.BLOCK).fieldOf("tag").forGetter(BlockTagIngredient::tag)
		).apply(instance, BlockTagIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> STREAM_CODEC = StreamCodec.composite(
				ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.BLOCK, id), TagKey::location), BlockTagIngredient::tag,
				BlockTagIngredient::new
		);

		@Override
		public MapCodec<BlockTagIngredient> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
