package artifality.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class IncrementalLensBlock extends BaseBlock{

    private static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 8, 16);

    public IncrementalLensBlock(Settings settings, String name) {
        super(settings, "custom_model", name);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public String getDescription() {
        return "Increases the level of effect\ngiven by the beacon if placed\non top of the beacon.";
    }
}