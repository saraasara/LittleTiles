package com.creativemd.littletiles.common.world;

import java.util.Collection;
import java.util.HashSet;

import com.creativemd.creativecore.common.world.NeighborUpdateCollector;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LittleNeighborUpdateCollector extends NeighborUpdateCollector {
    
    public LittleNeighborUpdateCollector(World world, Collection<BlockPos> positions) {
        super(world, positions);
    }
    
    public LittleNeighborUpdateCollector(World world) {
        super(world);
    }
    
    @Override
    protected void processPosition(BlockPos pos, HashSet<BlockPos> notifiedBlocks) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityLittleTiles)
            ((TileEntityLittleTiles) te).updateTiles(false);
        super.processPosition(pos, notifiedBlocks);
    }
    
}
