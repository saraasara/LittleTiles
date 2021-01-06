package com.creativemd.littletiles.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.littletiles.LittleTiles;
import com.creativemd.littletiles.client.gui.handler.LittleTileGuiHandler;
import com.creativemd.littletiles.common.block.BlockTile;
import com.creativemd.littletiles.common.block.BlockTile.TEResult;
import com.creativemd.littletiles.common.entity.EntityAnimation;
import com.creativemd.littletiles.common.packet.LittleBlockPacket;
import com.creativemd.littletiles.common.packet.LittleBlockPacket.BlockPacketAction;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLittleWrench extends Item {
	
	public ItemLittleWrench() {
		setCreativeTab(LittleTiles.littleTab);
		setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityLittleTiles) {
			
			if (world.isRemote) {
				TEResult result = BlockTile.loadTeAndTile(world, pos, player);
				if (player.isSneaking()) {
					if (result.isComplete() && result.parent.isStructure())
						LittleTileGuiHandler.openGui("structureoverview", new NBTTagCompound(), player, result.parent, result.tile);
					else
						PacketHandler.sendPacketToServer(new LittleBlockPacket(world, pos, player, BlockPacketAction.WRENCH));
				} else
					PacketHandler.sendPacketToServer(new LittleBlockPacket(world, pos, player, BlockPacketAction.WRENCH_INFO));
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	public static void rightClickAnimation(EntityAnimation animation, EntityPlayer player) {
		if (player.world.isRemote) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("uuid", animation.getCachedUniqueIdString());
			GuiHandler.openGui("diagnose", nbt, player);
		}
	}
}
