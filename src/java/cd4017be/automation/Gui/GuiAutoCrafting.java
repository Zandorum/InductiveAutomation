/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cd4017be.automation.Gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cd4017be.automation.TileEntity.AutoCrafting;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.TileContainer;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.GuiMachine;

/**
 *
 * @author CD4017BE
 */
public class GuiAutoCrafting extends GuiMachine
{
    
    private final AutoCrafting tileEntity;
    
    public GuiAutoCrafting(AutoCrafting tileEntity, EntityPlayer player)
    {
        super(new TileContainer(tileEntity, player));
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() 
    {
        this.xSize = 176;
        this.ySize = 168;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) 
    {
        super.drawGuiContainerForegroundLayer(mx, my);
        this.drawInfo(152, 16, 16, 16, "\\i", "gui.rstCtr");
        this.drawInfo(67, 31, 7, 8, "\\i", "gui.autoCrafting");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/autoCraft.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 151, this.guiTop + 15, 176 + tileEntity.getRef(9) * 18, 0, 18, 18);
        this.drawItemConfig(tileEntity, -54, 7);
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                this.drawStringCentered(getSlotRef(i + j * 3), this.guiLeft + 88 + i * 18, this.guiTop + 20 + j * 18, 0x404040);
        this.drawStringCentered(tileEntity.getInventoryName(), this.guiLeft + this.xSize / 2, this.guiTop + 4, 0x404040);
        this.drawStringCentered("Inventory", this.guiLeft + this.xSize / 2, this.guiTop + 72, 0x404040);
    }
    
    private String getSlotRef(int i)
    {
        int s = tileEntity.getRef(i);
        return s < 9 ? "" + s : "--";
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) 
    {
        int cmd = -1;
        this.clickItemConfig(tileEntity, x - this.guiLeft + 54, y - this.guiTop - 7);
        if (this.func_146978_c(151, 15, 18, 18, x, y))
        {
            tileEntity.setRef(9, (byte)((tileEntity.getRef(9) + 1) % 3));
            cmd = 0;
        } else
        for (int j = 0; j < 3; j++)
        for (int i = 0; i < 3; i++)
        if (this.func_146978_c(79 + i * 18, 15 + j * 18, 18, 5, x, y)) {
            tileEntity.setRef(i + j * 3, (byte)((tileEntity.getRef(i + j * 3) + 1) % 10));
            cmd = 0;
        } else if (this.func_146978_c(79 + i * 18, 28 + j * 18, 18, 5, x, y)) {
            tileEntity.setRef(i + j * 3, (byte)((tileEntity.getRef(i + j * 3) + 9) % 10));
            cmd = 0;
        }
        if (cmd != -1)
        {
            try {
            ByteArrayOutputStream bos = tileEntity.getPacketTargetData();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(cmd + AutomatedTile.CmdOffset);
            if (cmd == 0) dos.writeLong(tileEntity.netData.longs[1]);
            BlockGuiHandler.sendPacketToServer(bos);
            } catch (IOException e){}
        }
        super.mouseClicked(x, y, b);
    }
    
}