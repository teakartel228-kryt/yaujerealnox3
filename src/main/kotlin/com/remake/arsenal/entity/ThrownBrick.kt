package com.remake.arsenal.entity

import com.remake.arsenal.logic.Util
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

class ThrownBrick(type: EntityType<out ThrownBrick>, level: Level) : ThrowableItemProjectile(type, level) {
	override fun getDefaultItem(): Item = Items.BRICK

	override fun onHitEntity(result: EntityHitResult) {
		super.onHitEntity(result)
		val lvl = level()
		if (lvl !is ServerLevel) return
		val target = result.entity
		val stack = item
		val src = lvl.damageSources().thrown(this, owner)
		Util.hurt(lvl, target, src, 4.0f)
		if (target is LivingEntity) {
			val fire = Util.ench(lvl, stack, "brick_fire")
			val frost = Util.ench(lvl, stack, "brick_frost")
			val venom = Util.ench(lvl, stack, "brick_venom")
			if (fire > 0) target.igniteForSeconds(4.0f * fire)
			if (frost > 0) {
				target.addEffect(MobEffectInstance(MobEffects.SLOWNESS, 60 * frost, frost))
				target.ticksFrozen = target.ticksRequiredToFreeze + 40
			}
			if (venom > 0) target.addEffect(MobEffectInstance(MobEffects.POISON, 80 * venom, venom - 1))
		}
	}

	override fun onHit(result: HitResult) {
		super.onHit(result)
		val lvl = level()
		if (lvl !is ServerLevel) return
		val stack = item
		val explosive = Util.ench(lvl, stack, "brick_explosive")
		val storm = Util.ench(lvl, stack, "brick_storm")
		if (explosive > 0) lvl.explode(this, x, y, z, 1.2f * explosive, net.minecraft.world.level.Level.ExplosionInteraction.NONE)
		if (storm > 0) {
			val bolt = EntityType.LIGHTNING_BOLT.create(lvl, EntitySpawnReason.TRIGGERED)
			if (bolt != null) {
				bolt.setPos(x, y, z)
				lvl.addFreshEntity(bolt)
			}
		}
		discard()
	}
}
