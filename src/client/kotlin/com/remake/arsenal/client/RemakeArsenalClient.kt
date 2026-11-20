package com.remake.arsenal.client

import com.remake.arsenal.ModEntities
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.entity.ThrownItemRenderer

object RemakeArsenalClient : ClientModInitializer {
	override fun onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.THROWN_BRICK) { ctx -> ThrownItemRenderer(ctx) }
	}
}
