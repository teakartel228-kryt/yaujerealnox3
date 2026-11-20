package com.remake.arsenal.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult

/** Emits a redstone signal for a moment when hit by a projectile. */
class StrikeTargetBlock(props: Properties) : BaseBlock(props) {
	companion object {
		val POWER: IntegerProperty = BlockStateProperties.POWER
		val CODEC: MapCodec<StrikeTargetBlock> = codecOf { StrikeTargetBlock(it) }
	}

	init {
		registerDefaultState(stateDefinition.any().setValue(POWER, 0))
	}

	override fun codec(): MapCodec<out BaseBlock> = CODEC

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState>) {
		builder.add(POWER)
	}

	override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
		if (level !is ServerLevel) return
		val pos = hit.blockPos
		val c = hit.location.subtract(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
		val dist = maxOf(Math.abs(c.x), Math.abs(c.y), Math.abs(c.z)) * 2.0
		val power = (15 - Math.min(14.0, dist * 15.0)).toInt().coerceIn(1, 15)
		level.setBlock(pos, state.setValue(POWER, power), 3)
		level.scheduleTick(pos, this, 20)
		level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.0f)
	}

	override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
		if (state.getValue(POWER) != 0) level.setBlock(pos, state.setValue(POWER, 0), 3)
	}

	override fun isSignalSource(state: BlockState): Boolean = true

	override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, dir: Direction): Int =
		state.getValue(POWER)
}
