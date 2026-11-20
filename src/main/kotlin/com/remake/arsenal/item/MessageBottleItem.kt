package com.remake.arsenal.item

import com.remake.arsenal.logic.ModEvents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class MessageBottleItem(props: Properties) : Item(props) {
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
		if (level !is ServerLevel || player !is ServerPlayer) return InteractionResult.SUCCESS
		player.getItemInHand(hand).shrink(1)
		ModEvents.queueBottleReply(player, level.server.tickCount + 600)
		player.sendSystemMessage(Component.translatable("msg.remake_arsenal.bottle_sent"), true)
		return InteractionResult.SUCCESS
	}
}
