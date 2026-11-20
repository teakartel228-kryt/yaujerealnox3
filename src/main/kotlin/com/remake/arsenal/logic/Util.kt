package com.remake.arsenal.logic

import com.remake.arsenal.RemakeArsenal
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

object Util {
	/** Level of a mod enchantment on [stack], 0 if absent. */
	fun ench(level: Level, stack: ItemStack, name: String): Int {
		val lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
		val holder = lookup.get(ResourceKey.create(Registries.ENCHANTMENT, RemakeArsenal.id(name)))
		return if (holder.isPresent) EnchantmentHelper.getItemEnchantmentLevel(holder.get(), stack) else 0
	}

	/** Nearest living entity along the player's view within [range]. */
	fun rayEntity(level: Level, player: Player, range: Double, blockDist: Double = range): LivingEntity? {
		val eye = player.eyePosition
		val end = eye.add(player.lookAngle.scale(range))
		var best: LivingEntity? = null
		var bestDist = blockDist
		val box = AABB(eye, end).inflate(1.0)
		for (e in level.getEntities(player, box) { it is LivingEntity && it.isAlive }) {
			val hit = e.boundingBox.inflate(0.3).clip(eye, end)
			if (hit.isPresent) {
				val d = eye.distanceTo(hit.get())
				if (d < bestDist) {
					bestDist = d
					best = e as LivingEntity
				}
			}
		}
		return best
	}

	/** Distance to the first solid block along the view or null. */
	fun rayBlock(level: Level, player: Player, range: Double): net.minecraft.world.phys.BlockHitResult? {
		val eye = player.eyePosition
		val end = eye.add(player.lookAngle.scale(range))
		val r = level.clip(ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
		return if (r.type == HitResult.Type.BLOCK) r else null
	}

	fun hurt(level: ServerLevel, target: Entity, source: DamageSource, amount: Float): Boolean =
		target.hurtServer(level, source, amount)

	fun launch(e: Entity, v: Vec3) {
		e.setDeltaMovement(v)
		e.hurtMarked = true
	}
}
