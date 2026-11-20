package com.remake.arsenal.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty

/** Moves entities standing on it along its facing direction. */
class ConveyorBlock(props: Properties) : BaseBlock(props) {
	companion object {
		val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
		val POWERED: BooleanProperty = BlockStateProperties.POWERED
		val CODEC: MapCodec<ConveyorBlock> = codecOf { ConveyorBlock(it) }
	}

	init {
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false))
	}

	override fun codec(): MapCodec<out BaseBlock> = CODEC

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState>) {
		builder.add(FACING, POWERED)
	}

	override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? =
		defaultBlockState().setValue(FACING, ctx.horizontalDirection)
			.setValue(POWERED, ctx.level.hasNeighborSignal(ctx.clickedPos))

	override fun stepOn(level: Level, pos: BlockPos, state: BlockState, entity: Entity) {
		if (state.getValue(POWERED)) return
		val dir = state.getValue(FACING)
		val m = entity.deltaMovement
		entity.setDeltaMovement(dir.stepX * 0.22, m.y, dir.stepZ * 0.22)
		entity.hurtMarked = true
	}
}
