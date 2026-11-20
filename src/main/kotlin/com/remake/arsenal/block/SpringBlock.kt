package com.remake.arsenal.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty

/** Launches entities that step on it along its facing direction. */
class SpringBlock(props: Properties) : BaseBlock(props) {
	companion object {
		val FACING: EnumProperty<Direction> = BlockStateProperties.FACING
		val COMPRESSED: BooleanProperty = BooleanProperty.create("compressed")
		val CODEC: MapCodec<SpringBlock> = codecOf { SpringBlock(it) }
	}

	init {
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(COMPRESSED, false))
	}

	override fun codec(): MapCodec<out BaseBlock> = CODEC

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState>) {
		builder.add(FACING, COMPRESSED)
	}

	override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? =
		defaultBlockState().setValue(FACING, ctx.clickedFace)

	override fun stepOn(level: Level, pos: BlockPos, state: BlockState, entity: Entity) {
		if (entity.isSteppingCarefully) return
		val dir = state.getValue(FACING)
		entity.setDeltaMovement(dir.stepX * 1.6, dir.stepY * 1.6 + (if (dir == Direction.UP) 0.0 else 0.3), dir.stepZ * 1.6)
		entity.hurtMarked = true
		entity.resetFallDistance()
		if (level is ServerLevel) {
			level.setBlock(pos, state.setValue(COMPRESSED, true), 3)
			level.scheduleTick(pos, this, 6)
			level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.7f, 1.6f)
		}
	}

	override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
		if (state.getValue(COMPRESSED)) level.setBlock(pos, state.setValue(COMPRESSED, false), 3)
	}
}
