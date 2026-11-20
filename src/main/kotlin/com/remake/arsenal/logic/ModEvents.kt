package com.remake.arsenal.logic

import com.remake.arsenal.ModEntities
import com.remake.arsenal.ModItems
import com.remake.arsenal.entity.ThrownBrick
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

object ModEvents {
	private val bottleQueue = mutableListOf<Pair<UUID, Int>>()
	private val replies = listOf(
		"Море шепчет: иди на север.", "Копай глубже.", "Не доверяй зелёным.", "Крюк любит высоту.",
		"Кирпич решает всё.", "Ответ: 42.", "Береги шлем.", "Волны унесли твой вопрос.",
		"Диски играют лучше, чем ты думаешь.", "Просто прыгни."
	)

	fun queueBottleReply(player: ServerPlayer, dueTick: Int) {
		bottleQueue += player.uuid to dueTick
	}

	fun init() {
		UseItemCallback.EVENT.register { player, world, hand ->
			val stack = player.getItemInHand(hand)
			if (stack.`is`(Items.BRICK) || stack.`is`(Items.NETHER_BRICK)) {
				if (world is ServerLevel) {
					val e = ThrownBrick(ModEntities.THROWN_BRICK, world)
					e.item = stack.copyWithCount(1)
					e.setOwner(player)
					e.setPos(player.x, player.eyeY - 0.1, player.z)
					e.shootFromRotation(player, player.xRot, player.yRot, 0.0f, 1.5f, 1.0f)
					world.addFreshEntity(e)
					world.playSound(null, player.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.6f)
					if (!player.abilities.instabuild) stack.shrink(1)
				}
				InteractionResult.SUCCESS
			} else InteractionResult.PASS
		}

		AttackEntityCallback.EVENT.register { player, world, hand, entity, _ ->
			val stack = player.getItemInHand(hand)
			if (world is ServerLevel && stack.`is`(ModItems.CLAWS) && entity is LivingEntity) {
				val bleed = Util.ench(world, stack, "claws_bleed")
				val vamp = Util.ench(world, stack, "claws_vampire")
				if (bleed > 0) entity.addEffect(MobEffectInstance(MobEffects.WITHER, 40 * bleed, 0))
				if (vamp > 0) player.heal(vamp.toFloat())
			}
			InteractionResult.PASS
		}

		ServerTickEvents.END_SERVER_TICK.register { server ->
			for (p in server.playerList.players) {
				val holdsClaws = p.mainHandItem.`is`(ModItems.CLAWS) || p.offhandItem.`is`(ModItems.CLAWS)
				if (holdsClaws && p.horizontalCollision && !p.onGround() && !p.isInWater) {
					val m = p.deltaMovement
					p.setDeltaMovement(m.x, if (p.isShiftKeyDown) 0.0 else 0.2, m.z)
					p.resetFallDistance()
					p.hurtMarked = true
				}
			}
			val now = server.tickCount
			val it = bottleQueue.iterator()
			while (it.hasNext()) {
				val (id, due) = it.next()
				if (now >= due) {
					it.remove()
					val p = server.playerList.getPlayer(id) ?: continue
					val paper = ItemStack(Items.PAPER)
					paper.set(DataComponents.CUSTOM_NAME, Component.literal(replies.random()))
					if (!p.inventory.add(paper)) p.drop(paper, false)
					p.sendSystemMessage(Component.translatable("msg.remake_arsenal.bottle_reply"), true)
				}
			}
		}
	}
}
