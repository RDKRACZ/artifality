package artifality.mixin.common.extension;

import artifality.list.CrystalElement;
import artifality.extension.ElementalExtension;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkeletonEntity.class)
public abstract class WitherSkeletonMixinExtension extends AbstractSkeletonEntity implements ElementalExtension {
    protected WitherSkeletonMixinExtension(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {super(entityType, world);}

    private static final TrackedData<Boolean> ELEMENTAL = DataTracker.registerData(WitherSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> CRYSTAL_ELEMENT = DataTracker.registerData(WitherSkeletonEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        getDataTracker().startTracking(CRYSTAL_ELEMENT, 0);
        getDataTracker().startTracking(ELEMENTAL, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsElemental", artifality$isElemental());
        nbt.putInt("CrystalElement", this.getDataTracker().get(CRYSTAL_ELEMENT));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.getDataTracker().set(ELEMENTAL, nbt.getBoolean("IsElemental"));
        this.getDataTracker().set(CRYSTAL_ELEMENT, nbt.getInt("CrystalElement"));
    }

    @Override
    public boolean artifality$isElemental() {
        return this.getDataTracker().get(ELEMENTAL);
    }

    @Override
    public CrystalElement artifality$getElement() {
        try {
            return CrystalElement.ELEMENTS.get(this.getDataTracker().get(CRYSTAL_ELEMENT));
        }catch (IndexOutOfBoundsException e){
            return CrystalElement.ELEMENTS.get(0);
        }
    }

    @Override
    public void artifality$setElement(int element) {
        getDataTracker().set(ELEMENTAL, true);
        getDataTracker().set(CRYSTAL_ELEMENT, element);
    }

    @Override
    public void tick() {
        super.tick();
        if(!getEntityWorld().isClient()){
            if(artifality$isElemental()){
                artifality$getElement().tick(this, this.getEntityWorld());
            }
        }
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        if(!spawnReason.equals(SpawnReason.SPAWNER) && !spawnReason.equals(SpawnReason.CHUNK_GENERATION)){
            if(this.world.random.nextFloat() > 0.7F){
                getDataTracker().set(ELEMENTAL, true);
                getDataTracker().set(CRYSTAL_ELEMENT, this.world.random.nextInt(4));
                artifality$getElement().onInit(this, this.world);
            }
        }
    }
}
