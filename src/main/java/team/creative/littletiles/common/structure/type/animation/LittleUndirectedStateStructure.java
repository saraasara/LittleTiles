package team.creative.littletiles.common.structure.type.animation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import team.creative.littletiles.common.block.little.tile.LittleTileContext;
import team.creative.littletiles.common.block.little.tile.parent.IStructureParentCollection;
import team.creative.littletiles.common.structure.animation.AnimationState;
import team.creative.littletiles.common.structure.animation.AnimationTimeline;
import team.creative.littletiles.common.structure.animation.AnimationTransition;
import team.creative.littletiles.common.structure.directional.StructureDirectional;
import team.creative.littletiles.common.structure.signal.output.InternalSignalOutput;

public abstract class LittleUndirectedStateStructure extends LittleStateStructure<AnimationState> {
    
    private static final AnimationState EMPTY = new AnimationState("");
    
    @StructureDirectional
    private List<AnimationTransition> transitions = new ArrayList<>();
    
    public LittleUndirectedStateStructure(LittleStateStructureType type, IStructureParentCollection mainBlock) {
        super(type, mainBlock);
    }
    
    @Override
    protected AnimationState createState(CompoundTag nbt) {
        return new AnimationState(nbt);
    }
    
    @Override
    protected AnimationState getEmptyState() {
        return EMPTY;
    }
    
    @Override
    protected boolean shouldStayAnimatedAfterTransitionEnd() {
        if (startTransitionIfNecessary(getOutput(0))) // Check if the state has changed already
            return true;
        return false;
    }
    
    protected AnimationTimeline getTransition(int start, int end) {
        for (AnimationTransition transition : transitions)
            if (transition.start == start && transition.end == end)
                return transition.timeline;
        return null;
    }
    
    protected boolean startTransitionIfNecessary(InternalSignalOutput output) {
        int state = output.getState().number();
        if (!isChanging() && hasState(state) && state != currentIndex()) {
            startTransition(currentIndex(), state, getTransition(currentIndex(), state));
            return true;
        }
        return false;
    }
    
    @Override
    public void performInternalOutputChange(InternalSignalOutput output) {
        if (output.component.is("state"))
            startTransitionIfNecessary(output);
    }
    
    @Override
    public boolean canInteract() {
        return canRightClick();
    }
    
    @Override
    public InteractionResult use(Level level, LittleTileContext context, BlockPos pos, Player player, BlockHitResult result) {
        if (canRightClick()) {
            if (!isClient())
                getOutput(0).toggle();
            return InteractionResult.SUCCESS;
        }
        return super.use(level, context, pos, player, result);
    }
    
}