package cd4017be.automation.Item;

import java.util.List;

import cd4017be.automation.Config;
import cd4017be.lib.TooltipInfo;
import cd4017be.lib.util.Obj2;
import cd4017be.lib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;

public class ItemPortablePump extends ItemEnergyCell implements IFluidContainerItem {

	public static int energyUse = 8;
	public static float reachDist = 16F;
	
	public ItemPortablePump(String id) 
	{
		super(id, Config.Ecap[0]);
		this.setMaxStackSize(1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean par4) 
    {
        FluidStack fluid = this.getFluid(item);
        if (fluid != null) {
            list.add(String.format("%s/%s %s %s", 
            		Utils.formatNumber((float)fluid.amount / 1000F, 3), 
            		Utils.formatNumber((float)this.getCapacity(item) / 1000F, 3),
            		TooltipInfo.getFluidUnit(),
            		fluid.getLocalizedName()));
        }
        super.addInformation(item, player, list, par4);
    }

    @Override
    public FluidStack getFluid(ItemStack item) 
    {
        if (item.getTagCompound() == null) return null;
        else return FluidStack.loadFluidStackFromNBT(item.getTagCompound().getCompoundTag("fluid"));
    }

    @Override
    public int getCapacity(ItemStack item) 
    {
        return 16000;
    }

    @Override
    public int fill(ItemStack item, FluidStack resource, boolean doFill) 
    {
        if (resource == null) return 0;
        if (doFill && item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
        int n;
        FluidStack fluid = this.getFluid(item);
        if (fluid != null) {
            if (!fluid.isFluidEqual(resource)) return 0;
            n = Math.min(this.getCapacity(item) - fluid.amount, resource.amount);
            if (doFill){
                fluid.amount += n;
                NBTTagCompound tag = new NBTTagCompound();
                fluid.writeToNBT(tag);
                item.getTagCompound().setTag("fluid", tag);
            }
            return n;
        } else {
            n = Math.min(this.getCapacity(item), resource.amount);
            if (doFill) {
                fluid = resource.copy();
                fluid.amount = n;
                NBTTagCompound tag = new NBTTagCompound();
                fluid.writeToNBT(tag);
                item.getTagCompound().setTag("fluid", tag);
            }
            return n;
        }
    }

    @Override
    public FluidStack drain(ItemStack item, int maxDrain, boolean doDrain) 
    {
        FluidStack fluid = this.getFluid(item);
        if (fluid == null) return null;
        FluidStack ret = fluid.copy();
        ret.amount = Math.min(fluid.amount, maxDrain);
        if (doDrain) {
            fluid.amount -= ret.amount;
            if (fluid.amount <= 0) item.getTagCompound().removeTag("fluid");
            else {
            	NBTTagCompound tag = new NBTTagCompound();
                fluid.writeToNBT(tag);
                item.getTagCompound().setTag("fluid", tag);
            }
        }
        return ret;
    }

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack item, World world, EntityPlayer player, EnumHand hand) 
	{
		if (world.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
		EnergyItem energy = new EnergyItem(item, this);
		if (energy.getStorageI() < energyUse) return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
		Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d dir = player.getLookVec();
        boolean sneak = player.isSneaking();
        RayTraceResult obj = world.rayTraceBlocks(pos, pos.addVector(dir.xCoord * reachDist, dir.yCoord * reachDist, dir.zCoord * reachDist), true, false, sneak);
        if (obj == null) {
        	return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, this.fillFluid(item, player.inventory));
        }
        FluidStack fluid = Utils.getFluid(world, obj.getBlockPos(), !sneak);
        if (fluid != null && this.fill(item, fluid, false) == fluid.amount) {
        	this.fill(item, fluid, true);
        	energy.addEnergyI(fluid.amount == 0 ? energyUse / -4 : -energyUse, -1);
        	if (sneak) world.setBlockState(obj.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
        	else world.setBlockToAir(obj.getBlockPos());
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, this.fillFluid(item, player.inventory));
	}
	
	private ItemStack fillFluid(ItemStack item, InventoryPlayer inv)
	{
		item = item.copy();
		FluidStack type = this.getFluid(item);
		if (type == null) {
			item.getTagCompound().removeTag("fluid");
			return item;
		}
		int[] list = new int[inv.mainInventory.length];
		int n = 0;
		for (int i = 0; i < inv.mainInventory.length && type.amount > 0; i++) {
			ItemStack stack = inv.mainInventory[i];
			if (stack != null && stack.getItem() instanceof IFluidContainerItem && !(stack.getItem() instanceof ItemPortablePump)) {
				IFluidContainerItem cont = (IFluidContainerItem)stack.getItem();
				FluidStack fluid = cont.getFluid(stack);
				if (fluid == null) list[n++] = i;
				else if (fluid.isFluidEqual(type)) {
					Obj2<ItemStack, Integer> obj = Utils.fillFluid(stack, type);
					type.amount -= obj.objB;
					inv.mainInventory[i] = obj.objA;
				}
			}
		}
		for (int i = 0; i < n && type.amount > 0; i++) {
			Obj2<ItemStack, Integer> obj = Utils.fillFluid(inv.mainInventory[list[i]], type);
			type.amount -= obj.objB;
			inv.mainInventory[list[i]] = obj.objA;
		}
		if (type.amount <= 0) {
			item.getTagCompound().removeTag("fluid");
		} else {
			NBTTagCompound tag = new NBTTagCompound();
			type.writeToNBT(tag);
			item.getTagCompound().setTag("fluid", tag);
		}
		return item;
	}

}
