package com.remake.arsenal.item

import com.remake.arsenal.logic.Util
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

/** Wear it on the head. Right-click (holding it) emits a sound wave. */
class NoteHelmetItem(props: Properties) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		val stack = player.getItemInHand(hand)
		if (player.cooldowns.isOnCooldown(stack)) {
			(player as? ServerPlayer)?.sendSystemMessage(Component.translatable("msg.remake_arsenal.sonic_cooldown", 5), true)
			return InteractionResult.PASS
		}
		if (level !is ServerLevel) return InteractionResult.SUCCESS
		for (e in level.getEntitiesOfClass(LivingEntity::class.java, player.boundingBox.inflate(8.0)) { it !== player }) {
			Util.hurt(level, e, level.damageSources().sonicBoom(player), 6.0f)
			Util.launch(e, e.position().subtract(player.position()).normalize().scale(1.2).add(0.0, 0.3, 0.0))
		}
		level.sendParticles(ParticleTypes.SONIC_BOOM, player.x, player.eyeY, player.z, 1, 0.0, 0.0, 0.0, 0.0)
		level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f)
		player.cooldowns.addCooldown(stack, 100)
		return InteractionResult.SUCCESS
	}
}
