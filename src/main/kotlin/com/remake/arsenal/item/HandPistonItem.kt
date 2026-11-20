package com.remake.arsenal.item

import com.remake.arsenal.logic.Util
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class HandPistonItem(props: Properties) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		val stack = player.getItemInHand(hand)
		if (player.cooldowns.isOnCooldown(stack)) return InteractionResult.PASS
		if (level !is ServerLevel) return InteractionResult.SUCCESS

		val overdrive = Util.ench(level, stack, "piston_overdrive")
		val shock = Util.ench(level, stack, "piston_shockwave")
		val look = player.lookAngle
		val target = Util.rayEntity(level, player, 4.0)
		val force = 1.8 + overdrive * 0.7

		if (target != null) {
			Util.launch(target, look.scale(force).add(0.0, 0.35, 0.0))
			Util.hurt(level, target, level.damageSources().playerAttack(player), 3.0f + overdrive)
			if (shock > 0) {
				for (e in level.getEntitiesOfClass(LivingEntity::class.java, target.boundingBox.inflate(2.0 + shock * 1.5)) { it !== player && it !== target }) {
					Util.launch(e, e.position().subtract(target.position()).normalize().scale(force * 0.6).add(0.0, 0.3, 0.0))
				}
			}
		} else {
			Util.launch(player, look.scale(-0.6 - overdrive * 0.2).add(0.0, 0.2, 0.0))
		}
		level.playSound(null, player.blockPosition(), SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 1.0f, 1.0f)
		player.cooldowns.addCooldown(stack, maxOf(8, 24 - overdrive * 4))
		return InteractionResult.SUCCESS
	}
}
