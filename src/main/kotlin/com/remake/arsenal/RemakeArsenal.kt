package com.remake.arsenal

import com.remake.arsenal.logic.ModEvents
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory

object RemakeArsenal : ModInitializer {
	const val MOD_ID: String = "remake_arsenal"
	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		ModBlocks.init()
		ModEntities.init()
		ModItems.init()
		ModEvents.init()
		LOGGER.info("[REMAKE] Arsenal loaded")
	}

	fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)
}
