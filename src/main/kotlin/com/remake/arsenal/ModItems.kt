package com.remake.arsenal

import com.remake.arsenal.item.*
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable

object ModItems {
	private fun props(name: String): Item.Properties =
		Item.Properties().setId(ResourceKey.create(Registries.ITEM, RemakeArsenal.id(name)))

	private fun <T : Item> reg(name: String, item: T): T {
		Registry.register(BuiltInRegistries.ITEM, RemakeArsenal.id(name), item)
		return item
	}

	private fun equippable(slot: EquipmentSlot, asset: String): Equippable =
		Equippable.builder(slot)
			.setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, RemakeArsenal.id(asset)))
			.build()

	val HOOKS: List<HookItem> = (0..5).map { tier ->
		val name = if (tier == 0) "grappling_hook" else "grappling_hook_$tier"
		reg(name, HookItem(props(name).stacksTo(1).enchantable(10), tier))
	}
	val HOOK_UPGRADE_TEMPLATE: Item = reg("hook_upgrade_template", Item(props("hook_upgrade_template")))
	val HOOK_HEADS: List<Item> = (0..5).map { reg("hook_head_$it", Item(props("hook_head_$it"))) }
	val MACE_HEAD: Item = reg("mace_head", Item(props("mace_head").stacksTo(1)))
	val TRIDENT_HEAD: Item = reg("trident_head", Item(props("trident_head").stacksTo(1)))
	val MAGNETITE_HEAD: Item = reg("magnetite_head", Item(props("magnetite_head").stacksTo(1)))

	val CLAWS: Item = reg(
		"climbing_claws",
		Item(
			props("climbing_claws").stacksTo(1).enchantable(10).attributes(
				ItemAttributeModifiers.builder()
					.add(
						Attributes.ATTACK_DAMAGE,
						AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
					)
					.add(
						Attributes.ATTACK_SPEED,
						AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -1.8, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
					)
					.build()
			)
		)
	)
	val HAND_PISTON: Item = reg("hand_piston", HandPistonItem(props("hand_piston").stacksTo(1).enchantable(10)))
	val NOTE_HELMET: Item = reg(
		"note_helmet",
		NoteHelmetItem(props("note_helmet").stacksTo(1).component(DataComponents.EQUIPPABLE, equippable(EquipmentSlot.HEAD, "note_helmet")))
	)
	val ELECTRO_WINGS: Item = reg(
		"electro_wings",
		ElectroWingsItem(
			props("electro_wings").stacksTo(1)
				.component(DataComponents.EQUIPPABLE, equippable(EquipmentSlot.CHEST, "electro_wings"))
				.component(DataComponents.GLIDER, net.minecraft.util.Unit.INSTANCE)
		)
	)
	val HATE_BAG: Item = reg("hate_bag", HateBagItem(props("hate_bag").stacksTo(1)))
	val MESSAGE_BOTTLE: Item = reg("message_bottle", MessageBottleItem(props("message_bottle")))
	val BRICK_SHARD: Item = reg("brick_shard", Item(props("brick_shard")))
	val NETHER_BRICK_SHARD: Item = reg("nether_brick_shard", Item(props("nether_brick_shard")))

	val DISC_IDS = listOf("steves_inferno", "big_shot", "hammer_of_justice", "its_tv_time", "the_world_revolving")
	val DISCS: List<Item> = DISC_IDS.map { id ->
		reg(
			"disc_$id",
			Item(
				props("disc_$id").stacksTo(1).rarity(Rarity.RARE)
					.jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, RemakeArsenal.id(id)))
			)
		)
	}

	fun init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register { e ->
			HOOKS.forEach { e.accept(it) }
			e.accept(CLAWS); e.accept(HAND_PISTON); e.accept(NOTE_HELMET); e.accept(ELECTRO_WINGS); e.accept(HATE_BAG)
		}
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register { e ->
			e.accept(HOOK_UPGRADE_TEMPLATE)
			HOOK_HEADS.forEach { e.accept(it) }
			e.accept(MACE_HEAD); e.accept(TRIDENT_HEAD); e.accept(MAGNETITE_HEAD)
			e.accept(MESSAGE_BOTTLE); e.accept(BRICK_SHARD); e.accept(NETHER_BRICK_SHARD)
		}
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register { e ->
			e.accept(ModBlocks.SPRING); e.accept(ModBlocks.CONVEYOR); e.accept(ModBlocks.STRIKE_TARGET)
		}
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register { e ->
			DISCS.forEach { e.accept(it) }
		}
	}
}
