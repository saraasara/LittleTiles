package com.creativemd.littletiles.common.gui.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.creativemd.creativecore.common.world.FakeWorld;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.littletiles.common.action.block.LittleActionPlaceStack;
import com.creativemd.littletiles.common.api.ILittleTile;
import com.creativemd.littletiles.common.entity.EntityAnimation;
import com.creativemd.littletiles.common.events.LittleDoorHandler;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.tiles.place.PlacePreviewTile;
import com.creativemd.littletiles.common.tiles.place.PlacePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittleAbsolutePreviewsStructure;
import com.creativemd.littletiles.common.tiles.preview.LittlePreviews;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.littletiles.common.tiles.vec.LittleTilePos;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;
import com.creativemd.littletiles.common.utils.grid.LittleGridContext;
import com.creativemd.littletiles.common.utils.placing.PlacementHelper;
import com.creativemd.littletiles.common.utils.placing.PlacementMode;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GuiAnimationViewer extends GuiControl {
	
	protected ItemStack stack;
	public EntityAnimation animation;
	public LittleTileBox entireBox;
	public LittleGridContext context;
	public AxisAlignedBB box;
	public double rotX = 0;
	public double rotY = 0;
	public double rotZ = 0;
	public double distance = 0;
	protected LoadingThread loadingThread;
	
	public boolean grabbed = false;
	public int grabX;
	public int grabY;
	
	public GuiAnimationViewer(String name, int x, int y, int width, int height, ItemStack stack) {
		super(name, x, y, width, height);
		this.marginWidth = 0;
		setStack(stack);
	}
	
	public void setStack(ItemStack stack) {
		if (loadingThread != null)
			loadingThread.interrupt();
		
		this.stack = stack;
		loadingThread = new LoadingThread();
	}
	
	@Override
	public boolean hasMouseOverEffect() {
		return false;
	}
	
	@Override
	public boolean canOverlap() {
		return false;
	}
	
	@Override
	public void mouseMove(int x, int y, int button) {
		super.mouseMove(x, y, button);
		if (grabbed) {
			rotY += x - grabX;
			rotX += y - grabY;
			grabX = x;
			grabY = y;
		}
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		if (button == 0) {
			grabbed = true;
			grabX = x;
			grabY = y;
			return true;
		}
		return false;
	}
	
	@Override
	public void mouseReleased(int x, int y, int button) {
		if (button == 0)
			grabbed = false;
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled) {
		distance = Math.max(distance + scrolled * -1, 0);
		return true;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		if (loadingThread != null)
			return;
		Vec3d center = box.getCenter();
		GlStateManager.disableDepth();
		
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.translate(width / 2D, height / 2D, 0);
		
		GlStateManager.pushMatrix();
		
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		int x = getPixelOffsetX();
		int y = getPixelOffsetY();
		int scale = getGuiScale();
		GlStateManager.viewport(x * scale, y * scale, width * scale, height * scale);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		Project.gluPerspective(90, (float) width / (float) height, 0.05F, 16 * 16);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		//GlStateManager.matrixMode(5890);
		GlStateManager.translate(0, 0, -distance);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableDepth();
		
		GL11.glRotated(rotX, 1, 0, 0);
		GL11.glRotated(rotY, 0, 1, 0);
		GL11.glRotated(rotZ, 0, 0, 1);
		
		GlStateManager.pushMatrix();
		
		LittleDoorHandler.client.render.doRender(animation, 0, 0, 0, 0, 1.0F);
		
		GlStateManager.popMatrix();
		
		GlStateManager.matrixMode(5888);
		
		GlStateManager.popMatrix();
		
		GlStateManager.disableLighting();
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.disableDepth();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		mc.entityRenderer.setupOverlayRendering();
	}
	
	public class LoadingThread extends Thread {
		
		public LoadingThread() {
			start();
		}
		
		@Override
		public void run() {
			ILittleTile iTile = PlacementHelper.getLittleInterface(stack);
			if (iTile != null && iTile.hasLittlePreview(stack)) {
				LittlePreviews previews = iTile.getLittlePreview(stack);
				entireBox = previews.getSurroundingBox();
				context = previews.context;
				box = entireBox.getBox(context);
				distance = context.toVanillaGrid(entireBox.getLongestSide()) / 2D + 2;
				BlockPos pos = new BlockPos(0, 75, 0);
				FakeWorld fakeWorld = FakeWorld.createFakeWorld("animationViewer", true);
				
				List<PlacePreviewTile> placePreviews = new ArrayList<>();
				previews.getPlacePreviews(placePreviews, null, true, LittleTileVec.ZERO);
				
				HashMap<BlockPos, PlacePreviews> splitted = LittleActionPlaceStack.getSplittedTiles(previews.context, placePreviews, pos);
				ArrayList<TileEntityLittleTiles> blocks = new ArrayList<>();
				LittleActionPlaceStack.placeTilesWithoutPlayer(fakeWorld, previews.context, splitted, previews.getStructure(), PlacementMode.all, pos, null, null, null, null);
				for (Iterator iterator = fakeWorld.loadedTileEntityList.iterator(); iterator.hasNext();) {
					TileEntity te = (TileEntity) iterator.next();
					if (te instanceof TileEntityLittleTiles)
						blocks.add((TileEntityLittleTiles) te);
				}
				
				animation = new EntityAnimation(fakeWorld, fakeWorld, blocks, new LittleAbsolutePreviewsStructure(previews.getStructureData(), pos, previews), UUID.randomUUID(), new LittleTilePos(pos, previews.context, entireBox.getCenter()), new LittleTileVec(0, 0, 0)) {
					
					@Override
					protected void copyExtra(EntityAnimation animation) {
						
					}
					
					@Override
					protected void entityInit() {
						
					}
					
					@Override
					public boolean shouldAddDoor() {
						return false;
					}
				};
			}
			
			loadingThread = null;
		}
		
	}
}
