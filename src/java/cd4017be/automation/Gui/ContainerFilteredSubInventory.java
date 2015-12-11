package cd4017be.automation.Gui;

import java.io.DataInputStream;
import java.io.IOException;

import cd4017be.api.automation.InventoryItemHandler;
import cd4017be.api.automation.InventoryItemHandler.ItemInventory;
import cd4017be.lib.BlockItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ContainerFilteredSubInventory extends Container 
{

	public static class FilterInventory implements IInventory {
		private ItemStack[] items = new ItemStack[2];
		
		public void load(ItemStack item)
		{
			items = new ItemStack[2];
			if (item.stackTagCompound == null) return;
			if (item.stackTagCompound.hasKey("fin")) {
				items[0] = BlockItemRegistry.stack("item.itemUpgrade", 1);
				items[0].stackTagCompound = item.stackTagCompound.getCompoundTag("fin");
			}
			if (item.stackTagCompound.hasKey("fout")) {
				items[1] = BlockItemRegistry.stack("item.itemUpgrade", 1);
				items[1].stackTagCompound = item.stackTagCompound.getCompoundTag("fout");
			}
		}
		
		public void save(ItemStack item)
		{
			if (item.stackTagCompound == null) item.stackTagCompound = new NBTTagCompound();
			if (items[0] != null && items[0].getItem() == BlockItemRegistry.itemId("item.itemUpgrade")) {
				if (items[0].stackTagCompound == null) items[0].stackTagCompound = new NBTTagCompound();
				item.stackTagCompound.setTag("fin", items[0].stackTagCompound);
			} else item.stackTagCompound.removeTag("fin");
			if (items[1] != null && items[1].getItem() == BlockItemRegistry.itemId("item.itemUpgrade")) {
				if (items[1].stackTagCompound == null) items[1].stackTagCompound = new NBTTagCompound();
				item.stackTagCompound.setTag("fout", items[1].stackTagCompound);
			} else item.stackTagCompound.removeTag("fout");
		}
		
		@Override
		public int getSizeInventory() {
			return items.length;
		}

		@Override
		public ItemStack getStackInSlot(int s) {
			return items[s];
		}

		@Override
		public ItemStack decrStackSize(int s, int n) {
			if (n > 0) {
				ItemStack item = items[s];
				items[s] = null;
				return item;
			} else return null;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int p_70304_1_) 
		{
			return null;
		}

		@Override
		public void setInventorySlotContents(int s, ItemStack stack) {
			items[s] = stack;
		}

		@Override
		public String getInventoryName() {
			return "Filter Slots";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void markDirty() {}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return !player.isDead;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}

		@Override
		public boolean isItemValidForSlot(int s, ItemStack stack) {
			return stack == null || stack.getItem() == BlockItemRegistry.getItem("item.itemUpgrade");
		}
		
	}
	
	public ItemInventory inventory;
    public FilterInventory filters;
    public final EntityPlayer player;
	
	public ContainerFilteredSubInventory(EntityPlayer player) 
	{
		this.player = player;
		ItemStack item = player.getCurrentEquippedItem();
		this.filters = new FilterInventory();
		this.filters.load(item);
		this.inventory = InventoryItemHandler.getInventory(item);
	}
	
	protected void addPlayerInventory(int x, int y)
	{
		for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(player.inventory, i, x + i * 18, y + 58));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(player.inventory, i * 9 + j + 9, x + j * 18, y + i * 18));
            }
        }
	}
	
	public void save(ItemStack item)
	{
		filters.save(item);
		inventory.save(item);
	}
	
	public void update(ItemStack item)
	{
		filters.load(item);
		inventory.load(item);
	}
    
    @Override
    public boolean canInteractWith(EntityPlayer player) 
    {
        return inventory != null && inventory.isUseableByPlayer(player) && InventoryItemHandler.isInventoryItem(player.getCurrentEquippedItem());
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int id) 
    {
    	Slot slot = (Slot)this.inventorySlots.get(id);
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack item = stack.copy();
        int[] t = this.stackTransferTarget(item, id);
        if (t != null) {
            if (!this.mergeItemStack(stack, t[0], t[1], true)) return null;
            slot.onSlotChange(stack, item);
        } else return null;
        if (stack.stackSize == 0)
        {
            slot.putStack((ItemStack)null);
        }
        else
        {
            slot.onSlotChanged();
        }
        if (stack.stackSize == item.stackSize)
        {
            return null;
        }
        slot.onPickupFromSlot(player, stack);
        return item;
    }

    protected int[] stackTransferTarget(ItemStack item, int id) {
		return null;
	}

	@Override
    public ItemStack slotClick(int s, int b, int m, EntityPlayer par4EntityPlayer)
    {   
		Slot slot;
		if (s >= 0 && s < this.inventorySlots.size() && (slot = (Slot)this.inventorySlots.get(s)) != null && slot.inventory instanceof InventoryPlayer && slot.getSlotIndex() == par4EntityPlayer.inventory.currentItem) return null;
		else return super.slotClick(s, b, m, par4EntityPlayer);
    }
    
    @Override //prevents client crash IndexOutOfBoundsException sometimes caused by incorrect netdata
    public void putStackInSlot(int par1, ItemStack bItemStack) 
    {
        if (par1 >= 0 && par1 < this.inventorySlots.size()) super.putStackInSlot(par1, bItemStack);
    }

    @Override //prevents client crash IndexOutOfBoundsException sometimes caused by incorrect netdata
    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) 
    {
        if (par1ArrayOfItemStack.length <= this.inventorySlots.size())super.putStacksInSlots(par1ArrayOfItemStack);
    }

	@Override
	public void onContainerClosed(EntityPlayer player) 
	{
		ItemStack item = player.getCurrentEquippedItem();
		if (item != null) this.save(item);
		super.onContainerClosed(player);
	}
	
	public boolean isFilterOn(int s)
	{
		ItemStack stack = filters.getStackInSlot(s);
		if (stack != null && stack.getItem() == BlockItemRegistry.getItem("item.itemUpgrade") && stack.stackTagCompound != null) {
			byte m = stack.stackTagCompound.getByte("mode");
			return (m & 128) != 0;
		} else return false;
	}
	
	public void onPlayerCommand(World world, EntityPlayer player, DataInputStream dis) throws IOException
	{
		byte cmd = dis.readByte();
		if (cmd >= 0 && cmd < 2) {
			ItemStack stack = filters.getStackInSlot(cmd);
			if (stack != null && stack.getItem() == BlockItemRegistry.getItem("item.itemUpgrade") && stack.stackTagCompound != null) {
				byte m = stack.stackTagCompound.getByte("mode");
				m |= 64;
				m ^= 128;
				stack.stackTagCompound.setByte("mode", m);
			}
		}
	}
    
}