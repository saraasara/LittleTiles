package com.creativemd.littletiles.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemBlockColored2 extends ItemBlock {
	
	public ItemBlockColored2(Block block, ResourceLocation location) {
		super(block);
		setUnlocalizedName(location.getResourcePath());
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = "default";
		if (stack.getMetadata() < BlockLTColored2.ColoredEnumType2.values().length)
			name = BlockLTColored2.ColoredEnumType2.values()[stack.getMetadata()].getName();
		return getUnlocalizedName() + "." + name;
	}
	
	@Override
	public int getMetadata(int meta) {
		return meta;
	}
	
}
