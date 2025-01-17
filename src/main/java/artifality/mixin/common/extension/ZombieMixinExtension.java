package artifality.mixin.common.extension;

import artifality.list.CrystalElements;
import artifality.extension.ElementalExtension;
import artifality.list.element.CrystalElement;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieMixinExtension extends HostileEntity implements ElementalExtension {
    @Shadow public abstract boolean isBaby();

    protected ZombieMixinExtension(EntityType<? extends HostileEntity> entityType, World world) {super(entityType, world);}

    private static final TrackedData<Boolean> artifality$ELEMENTAL = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> artifality$CRYSTAL_ELEMENT = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    void initDataTracker(CallbackInfo ci){
        getDataTracker().startTracking(artifality$CRYSTAL_ELEMENT, 0);
        getDataTracker().startTracking(artifality$ELEMENTAL, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci){
        nbt.putBoolean("ArtifalityIsElemental", artifality$isElemental());
        nbt.putInt("ArtifalityCrystalElement", this.getDataTracker().get(artifality$CRYSTAL_ELEMENT));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci){
        this.getDataTracker().set(artifality$ELEMENTAL, nbt.getBoolean("ArtifalityIsElemental"));
        this.getDataTracker().set(artifality$CRYSTAL_ELEMENT, nbt.getInt("ArtifalityCrystalElement"));
    }

    @Override
    public boolean artifality$isElemental() {
        return this.getDataTracker().get(artifality$ELEMENTAL);
    }

    @Override
    public CrystalElement artifality$getElement() {
        try {
            return CrystalElements.ELEMENTS.get(this.getDataTracker().get(artifality$CRYSTAL_ELEMENT));
        }catch (IndexOutOfBoundsException e){
            return CrystalElements.ELEMENTS.get(0);
        }
    }

    @Override
    public void artifality$setElement(int element) {
        getDataTracker().set(artifality$ELEMENTAL, true);
        getDataTracker().set(artifality$CRYSTAL_ELEMENT, element);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    void tick(CallbackInfo ci){
        if(!getEntityWorld().isClient()){
            if(artifality$isElemental()){
                artifality$getElement().tick(this, this.getEntityWorld());
            }
        }
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        if(!spawnReason.equals(SpawnReason.SPAWNER) && !spawnReason.equals(SpawnReason.CHUNK_GENERATION) && !isBaby()){
            if(this.world.random.nextFloat() > 0.7F){
                getDataTracker().set(artifality$ELEMENTAL, true);
                getDataTracker().set(artifality$CRYSTAL_ELEMENT, this.world.random.nextInt(4));
                artifality$getElement().onInit(this, this.world);
            }
        }
    }

    @Inject(method = "tryAttack", at = @At("RETURN"))
    void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir){
        if(target instanceof LivingEntity livingEntity){
            if(artifality$isElemental()){
                artifality$getElement().onAttack(livingEntity, this.getEntityWorld());
            }
        }
    }
}
