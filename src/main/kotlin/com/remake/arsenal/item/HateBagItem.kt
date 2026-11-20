package com.remake.arsenal.item

import com.remake.arsenal.logic.Util
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

/** Right-click: hate lightning strikes the target you look at. */
class HateBagItem(props: Properties) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		val stack = player.getItemInHand(hand)
		if (player.cooldowns.isOnCooldown(stack)) return InteractionResult.PASS
		if (level !is ServerLevel) return InteractionResult.SUCCESS
		val target = Util.rayEntity(level, player, 40.0)
		val pos = target?.position() ?: Util.rayBlock(level, player, 40.0)?.location
		if (pos == null) return InteractionResult.PASS
		val bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED)
		if (bolt != null) {
			bolt.setPos(pos.x, pos.y, pos.z)
			level.addFreshEntity(bolt)
		}
		player.cooldowns.addCooldown(stack, 100)
		return InteractionResult.SUCCESS
	}
}
