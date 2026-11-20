package com.remake.arsenal

import com.remake.arsenal.block.ConveyorBlock
import com.remake.arsenal.block.SpringBlock
import com.remake.arsenal.block.StrikeTargetBlock
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour

object ModBlocks {
	private fun blockProps(name: String): BlockBehaviour.Properties =
		BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, RemakeArsenal.id(name)))

	private fun <T : Block> reg(name: String, block: T): T {
		Registry.register(BuiltInRegistries.BLOCK, RemakeArsenal.id(name), block)
		val itemProps = Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, RemakeArsenal.id(name)))
			.useBlockDescriptionPrefix()
		Registry.register(BuiltInRegistries.ITEM, RemakeArsenal.id(name), BlockItem(block, itemProps))
		return block
	}

	val SPRING: SpringBlock = reg("spring", SpringBlock(blockProps("spring").strength(2.0f).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops()))
	val CONVEYOR: ConveyorBlock = reg("conveyor", ConveyorBlock(blockProps("conveyor").strength(2.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()))
	val STRIKE_TARGET: StrikeTargetBlock = reg("strike_target", StrikeTargetBlock(blockProps("strike_target").strength(1.5f).sound(SoundType.METAL).requiresCorrectToolForDrops()))

	fun init() {}
}
