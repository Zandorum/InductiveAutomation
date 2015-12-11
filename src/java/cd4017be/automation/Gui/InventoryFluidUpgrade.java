/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cd4017be.automation.Gui;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import cd4017be.automation.Item.ItemFluidDummy;
import cd4017be.automation.Item.PipeUpgradeFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 *
 * @author CD4017BE
 */
public class InventoryFluidUpgrade implements IInventory
{
    public final ItemStack[] inventory = new ItemStack[4];
    public final PipeUpgradeFluid upgrade;
    
    public InventoryFluidUpgrade(ItemStack item)
    {
        if (item.stackTagCompound == null) item.stackTagCompound = new NBTTagCompound();
        upgrade = PipeUpgradeFluid.load(item.stackTagCompound);
        for (int i = 0; i < upgrade.list.length && i < inventory.length; i++)
        {
            if (upgrade.list[i] != null) inventory[i] = ItemFluidDummy.getFluidContainer(upgrade.list[i].fluidID, 1);
        }
    }
    
    public void onCommand(DataInputStream dis) throws IOException
    {
        byte cmd = dis.readByte();
        if (cmd == 0) upgrade.mode = dis.readByte();
        else if (cmd == 1) {
            upgrade.maxAmount = dis.readInt();
            if (upgrade.maxAmount < 0) upgrade.maxAmount = 0;
        }
    }
    
    @Override
    public int getSizeInventory() 
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) 
    {
        return inventory[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int n) 
    {
        if (inventory[i] == null) return null;
        ItemStack item = inventory[i].stackSize <= n ? this.getStackInSlotOnClosing(i) : inventory[i].splitStack(n);
        return item;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) 
    {
        ItemStack item = inventory[i];
        inventory[i] = null;
        return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack item) 
    {
        inventory[i] = item;
    }

    @Override
    public int getInventoryStackLimit() 
    {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) 
    {
        return !player.isDead;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) 
    {
        return true;
    }
    
    public void onClose(ItemStack item)
    {
        if (item == null) return;
        FluidStack[] list = new FluidStack[4];
        int n = 0;
        for (int i = 0; i < inventory.length; i++) {
            ItemStack stack = inventory[i];
            list[n] = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (list[n] == null && stack != null) {
                if (stack.getItem() instanceof IFluidContainerItem) list[n] = ((IFluidContainerItem)stack.getItem()).getFluid(stack);
                else if (upgrade.list.length > i) list[n] = upgrade.list[i];
            }
            if (list[n] != null) n++;
        }
        upgrade.list = Arrays.copyOf(list, n);
        item.stackTagCompound = PipeUpgradeFluid.save(upgrade);
    }

	@Override
	public String getInventoryName() {
		return "Fluid Pipe Filter";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public void markDirty() {}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}
    
}