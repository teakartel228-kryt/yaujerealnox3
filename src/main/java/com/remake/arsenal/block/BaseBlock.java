package com.remake.arsenal.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;

/** Java shim: BlockBehaviour.simpleCodec is protected static. */
public abstract class BaseBlock extends Block {
	protected BaseBlock(Properties properties) {
		super(properties);
	}

	public static <B extends Block> MapCodec<B> codecOf(Function<Properties, B> factory) {
		return simpleCodec(factory);
	}
}
