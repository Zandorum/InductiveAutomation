package cd4017be.automation.Item;

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import cd4017be.api.automation.TeslaNetwork;
import cd4017be.api.energy.EnergyAPI;
import cd4017be.automation.Automation;
import cd4017be.automation.Config;
import cd4017be.automation.Gui.ContainerPortableTesla;
import cd4017be.automation.Gui.GuiPortableTesla;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.IGuiItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPortableTesla extends DefaultItem implements IGuiItem
{
	
	public ItemPortableTesla(String id) 
	{
		super(id);
        this.setCreativeTab(Automation.tabAutomation);
        this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack item) 
    {
		return EnumRarity.UNCOMMON;
	}
	
	@Override
	public Container getContainer(World world, EntityPlayer player, int x, int y, int z) 
	{
		return new ContainerPortableTesla(player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(World world, EntityPlayer player, int x, int y, int z) 
	{
		return new GuiPortableTesla(new ContainerPortableTesla(player));
	}

	@Override
	public void onPlayerCommand(World world, EntityPlayer player, PacketBuffer dis) throws IOException 
	{
		ItemStack item = player.getHeldItemMainhand();
		if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
		byte cmd = dis.readByte();
		if (cmd == 0) item.getTagCompound().setShort("mode", dis.readShort());
		else if (cmd == 1) item.getTagCompound().setShort("freq", dis.readShort());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack item, World world, EntityPlayer player, EnumHand hand) 
	{
		BlockGuiHandler.openItemGui(player, world, 0, -1, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int s, boolean b) 
	{
		if (!(entity instanceof EntityPlayer) || world.isRemote || item.getTagCompound() == null) return;
		EntityPlayer player = (EntityPlayer)entity;
		TeslaNetwork.instance.transmittEnergy(new TeslaTransmitterItem(player, s, item, Config.Umax[3]));
		double Emax = Config.Umax[3] * Config.Umax[3];
		short mode = item.getTagCompound().getShort("mode");
		if ((mode & 0x7) != 0) {
			InventoryPlayer inv = player.inventory;
            double E0 = item.getTagCompound().getDouble("voltage");
			double E = (E0 *= E0);
			
			if ((mode & 0x0300) == 0x0100)
				for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
					E -= EnergyAPI.get(inv.mainInventory[i]).addEnergy(E, 0);
			else if ((mode & 0x0300) == 0x0200)
				for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
					E -= EnergyAPI.get(inv.mainInventory[i]).addEnergy(E - Emax, 0);
			
			if ((mode & 0x0c00) == 0x0400)
				for (int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.length; i++)
					E -= EnergyAPI.get(inv.mainInventory[i]).addEnergy(E, 0);
			else if ((mode & 0x0c00) == 0x0800)
				for (int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.length; i++)
					E -= EnergyAPI.get(inv.mainInventory[i]).addEnergy(E - Emax, 0);
			
			if ((mode & 0x3000) == 0x1000)
				for (int i = 0; i < inv.armorInventory.length; i++)
					E -= EnergyAPI.get(inv.armorInventory[i]).addEnergy(E, 0);
			else if ((mode & 0x3000) == 0x2000)
				for (int i = 0; i < inv.armorInventory.length; i++)
					E -= EnergyAPI.get(inv.armorInventory[i]).addEnergy(E - Emax, 0);
			
			if (E != E0) item.getTagCompound().setDouble("voltage", Math.sqrt(E));
		}
	}

}
