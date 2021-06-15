package artifality.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class LensBlock extends BaseBlock {

    private static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 8, 16);

    public LensBlock(Settings settings, String name) {
        super(settings, "custom_model", name);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
