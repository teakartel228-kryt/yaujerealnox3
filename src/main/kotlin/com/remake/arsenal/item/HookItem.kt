package com.remake.arsenal.item

import com.remake.arsenal.ModItems
import com.remake.arsenal.logic.Util
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

/** Instant grappling hook. Tier 0..5. Head (mace/trident/magnetite) goes in the other hand. */
class HookItem(props: Properties, val tier: Int) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		val stack = player.getItemInHand(hand)
		if (player.cooldowns.isOnCooldown(stack)) return InteractionResult.PASS
		if (level !is ServerLevel) return InteractionResult.SUCCESS

		val power = Util.ench(level, stack, "hook_power")
		val quick = Util.ench(level, stack, "hook_quick_recharge")
		val multi = Util.ench(level, stack, "hook_multishot")
		val range = 18.0 + tier * 8.0 + power * 6.0
		val speed = 1.1 + tier * 0.15 + power * 0.12

		val other = player.getItemInHand(if (hand == InteractionHand.MAIN_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND)
		val mace = other.`is`(ModItems.MACE_HEAD)
		val trident = other.`is`(ModItems.TRIDENT_HEAD)
		val magnet = other.`is`(ModItems.MAGNETITE_HEAD)

		val block = Util.rayBlock(level, player, range)
		val blockDist = block?.let { player.eyePosition.distanceTo(it.location) } ?: range
		val target = Util.rayEntity(level, player, range, blockDist)

		var used = false
		if (target != null) {
			used = true
			val targets = mutableListOf<LivingEntity>(target)
			if (multi > 0) {
				targets += level.getEntitiesOfClass(LivingEntity::class.java, target.boundingBox.inflate(4.0)) {
					it !== player && it !== target && it.isAlive
				}.take(3)
			}
			for (t in targets) {
				val dir = player.position().subtract(t.position()).normalize()
				Util.launch(t, dir.scale(speed * 1.3).add(0.0, 0.3, 0.0))
				if (mace) Util.hurt(level, t, level.damageSources().playerAttack(player), 7.0f)
				if (trident) Util.hurt(level, t, level.damageSources().playerAttack(player), 5.0f)
			}
			level.playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0f, 1.0f)
		} else if (block != null) {
			val pos = block.blockPos
			val hardness = level.getBlockState(pos).getDestroySpeed(level, pos)
			if (hardness < 0f || (hardness >= 50f && !magnet)) {
				(player as? ServerPlayer)?.sendSystemMessage(Component.translatable("msg.remake_arsenal.hook_too_hard"), true)
				return InteractionResult.FAIL
			}
			used = true
			val dir = block.location.subtract(player.position().add(0.0, 1.0, 0.0)).normalize()
			Util.launch(player, dir.scale(speed * 1.6).add(0.0, 0.15, 0.0))
			player.resetFallDistance()
			level.playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.PLAYERS, 1.0f, 1.2f)
		}

		if (magnet) {
			for (item in level.getEntitiesOfClass(ItemEntity::class.java, player.boundingBox.inflate(range * 0.6))) {
				Util.launch(item, player.position().subtract(item.position()).normalize().scale(0.9))
			}
		}

		if (used) {
			player.cooldowns.addCooldown(stack, maxOf(6, 30 - tier * 4 - quick * 6))
			return InteractionResult.SUCCESS
		}
		return InteractionResult.PASS
	}
}
