package com.remake.arsenal

import com.remake.arsenal.entity.ThrownBrick
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object ModEntities {
	val THROWN_BRICK: EntityType<ThrownBrick> = run {
		val key = ResourceKey.create(Registries.ENTITY_TYPE, RemakeArsenal.id("thrown_brick"))
		Registry.register(
			BuiltInRegistries.ENTITY_TYPE, key,
			EntityType.Builder.of(::ThrownBrick, MobCategory.MISC)
				.sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build(key)
		)
	}

	fun init() {}
}
