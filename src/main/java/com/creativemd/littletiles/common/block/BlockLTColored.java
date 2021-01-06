package com.creativemd.littletiles.common.block;

import com.creativemd.littletiles.LittleTiles;
import com.creativemd.littletiles.common.api.block.ISpecialBlockHandler;
import com.creativemd.littletiles.common.tile.LittleTile;
import com.creativemd.littletiles.common.tile.parent.IParentTileList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLTColored extends Block implements ISpecialBlockHandler {
	
	public static final PropertyEnum<BlockLTColored.EnumType> VARIANT = PropertyEnum.<BlockLTColored.EnumType>create("variant", BlockLTColored.EnumType.class);
	
	public BlockLTColored() {
		super(Material.ROCK);
		setCreativeTab(LittleTiles.littleTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockLTColored.EnumType.clean));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int i = 0; i < EnumType.values().length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockLTColored.EnumType.byMetadata(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}
	
	@Override
	public int getLightValue(IBlockState state) {
		if (state.getValue(VARIANT) == EnumType.light_clean)
			return 15;
		return 0;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(VARIANT) == EnumType.light_clean)
			return 15;
		return 0;
	}
	
	public IBlockState get(EnumType type) {
		return getDefaultState().withProperty(VARIANT, type);
	}
	
	public enum EnumType implements IStringSerializable {
		
		clean,
		floor,
		grainy_big,
		grainy,
		grainy_low,
		brick,
		bordered,
		brick_big,
		structured,
		broken_brick_big,
		clay,
		light_clean,
		lava {
			@Override
			public boolean isLava() {
				return true;
			}
		},
		plank,
		white_lava {
			@Override
			public boolean isLava() {
				return true;
			}
		};
		
		public boolean isLava() {
			return false;
		}
		
		public static EnumType byMetadata(int meta) {
			return values()[meta];
		}
		
		public int getMetadata() {
			return ordinal();
		}
		
		@Override
		public String getName() {
			return name();
		}
	}
	
	@Override
	public boolean canWalkThrough(LittleTile tile) {
		return tile.getBlockState().getValue(VARIANT).isLava();
	}
	
	@Override
	public boolean isMaterial(LittleTile tile, Material material) {
		if (tile.getBlockState().getValue(VARIANT).isLava())
			return material == Material.LAVA;
		return ISpecialBlockHandler.super.isMaterial(tile, material);
	}
	
	@Override
	public boolean isLiquid(LittleTile tile) {
		if (tile.getBlockState().getValue(VARIANT).isLava())
			return true;
		return ISpecialBlockHandler.super.isLiquid(tile);
	}
	
	@Override
	public boolean canBeConvertedToVanilla(LittleTile tile) {
		return !tile.getBlockState().getValue(VARIANT).isLava();
	}
	
	@Override
	public boolean onBlockActivated(IParentTileList list, LittleTile tile, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		IBlockState state = tile.getBlockState();
		if (state.getValue(VARIANT).isLava() && hand == EnumHand.MAIN_HAND && heldItem.getItem() instanceof ItemBucket) {
			if (state.getValue(VARIANT) == EnumType.lava)
				tile.setBlock(LittleTiles.flowingLava, 0);
			else
				tile.setBlock(LittleTiles.whiteFlowingLava, 0);
			list.getTe().updateTiles();
			return true;
		}
		return ISpecialBlockHandler.super.onBlockActivated(list, tile, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	@Override
	public Vec3d getFogColor(IParentTileList list, LittleTile tile, Entity entity, Vec3d originalColor, float partialTicks) {
		if (tile.getBlockState().getValue(VARIANT).isLava())
			return new Vec3d(0.6F, 0.1F, 0.0F);
		return ISpecialBlockHandler.super.getFogColor(list, tile, entity, originalColor, partialTicks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canBeRenderCombined(LittleTile thisTile, LittleTile tile) {
		if (EnumType.values()[thisTile.getMeta()].isLava())
			return tile.getBlock() == LittleTiles.flowingLava;
		return false;
	}
	
}
