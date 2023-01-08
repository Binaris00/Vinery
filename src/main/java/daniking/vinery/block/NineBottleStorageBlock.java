package daniking.vinery.block;

import daniking.vinery.item.DrinkBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

public class NineBottleStorageBlock extends StorageBlock {


    public NineBottleStorageBlock(Settings settings) {
        super(settings);
    }
    @Override
    public int size(){
        return 9;
    }

    @Override
    public boolean canInsertStack(ItemStack stack) {
        return stack.getItem() instanceof DrinkBlockItem;
    }

    @Override
    public StorageType type() {
        return StorageType.NINE_BOTTLE;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.DOWN, Direction.UP};
    }

    @Override
    public int getSection(Pair<Float, Float> ff) {
        float f = ff.getLeft();
        float y = ff.getRight();
        float l = (float) 1/3;

        int nSection;
        if (f < 0.375F) {
            nSection = 0;
        } else {
            nSection = f < 0.6875F ? 1 : 2;
        }

        int i = y >= l*2 ? 0 : y >= l ? 1 : 2;
        return nSection + i * 3;
    }


}