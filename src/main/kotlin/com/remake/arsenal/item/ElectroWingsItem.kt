package com.remake.arsenal.item

import com.remake.arsenal.logic.Util
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

/** Chest slot, glides. Right-click while holding it: dash forward. */
class ElectroWingsItem(props: Properties) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		val stack = player.getItemInHand(hand)
		if (player.cooldowns.isOnCooldown(stack)) return InteractionResult.PASS
		if (level !is ServerLevel) return InteractionResult.SUCCESS
		Util.launch(player, player.lookAngle.scale(2.2))
		player.resetFallDistance()
		level.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.x, player.y + 1.0, player.z, 20, 0.4, 0.6, 0.4, 0.1)
		level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.5f, 2.0f)
		player.cooldowns.addCooldown(stack, 40)
		return InteractionResult.SUCCESS
	}
}
