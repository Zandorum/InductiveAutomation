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

import cd4017be.automation.Config;
import cd4017be.automation.TileEntity.ESU;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.TileContainer;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.GuiMachine;

/**
 *
 * @author CD4017BE
 */
public class GuiESU extends GuiMachine
{
    protected ESU tileEntity;
    
    public GuiESU(ESU tileEntity, EntityPlayer player)
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
        double I = tileEntity.netData.floats[1] / (tileEntity.netData.floats[1] <= 0 ? -(float)tileEntity.netData.ints[0] : (float)Math.sqrt((double)tileEntity.netData.ints[0] * (double)tileEntity.netData.ints[0] + (double)tileEntity.netData.floats[1]));
        this.drawInfo(8, 46, 160, 4, "Energy Transfer:", String.format("%+.1f kW @ %.0f A", tileEntity.netData.floats[1] / 1000F, I));
        this.drawInfo(28, 16, 30, 16, "\\i", "gui.voltage");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/ESU.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int n = tileEntity.getStorageScaled(160);
        this.drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 52, 0, 240, n, 16);
        n = tileEntity.getDiff(80);
        if (n > 0){
            this.drawTexturedModalRect(this.guiLeft + 88, this.guiTop + 46, 80, 236, n, 4);
        } else {
            this.drawTexturedModalRect(this.guiLeft + 88 + n, this.guiTop + 46, 80 + n, 236, -n, 4);
        }
        this.drawItemConfig(tileEntity, -27, 7);
        this.drawEnergyConfig(tileEntity, -45, 7);
        this.drawStringCentered(tileEntity.getInventoryName(), this.guiLeft + this.xSize / 2, this.guiTop + 4, 0x404040);
        this.drawStringCentered("Inventory", this.guiLeft + this.xSize / 2, this.guiTop + 72, 0x404040);
        this.drawStringCentered(tileEntity.netData.ints[0] + "V", this.guiLeft + 43, this.guiTop + 20, 0x404040);
        this.drawStringCentered((int)(tileEntity.netData.floats[0] / 1000F) + "/" + tileEntity.getMaxStorage() + " kJ", this.guiLeft + 88, this.guiTop + 56, 0x404040);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) 
    {
        byte a = -1;
        this.clickItemConfig(tileEntity, x - this.guiLeft + 27, y - this.guiTop - 7);
        this.clickEnergyConfig(tileEntity, x - this.guiLeft + 45, y - this.guiTop - 7);
        if (this.func_146978_c(8, 16, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] -= b == 0 ? 10 : 1000;
            a = 0;
        } else
        if (this.func_146978_c(18, 16, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] -= b == 0 ? 1 : 100;
            a = 0;
        } else
        if (this.func_146978_c(58, 16, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] += b == 0 ? 1 : 100;
            a = 0;
        } else
        if (this.func_146978_c(68, 16, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] += b == 0 ? 10 : 1000;
            a = 0;
        }
        if (a >= 0)
        {
            if (tileEntity.netData.ints[0] < 0) tileEntity.netData.ints[0] = 0;
            if (tileEntity.netData.ints[0] > Config.Umax[tileEntity.type]) tileEntity.netData.ints[0] = Config.Umax[tileEntity.type];
            try {
            ByteArrayOutputStream bos = tileEntity.getPacketTargetData();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(AutomatedTile.CmdOffset);
            dos.writeInt(tileEntity.netData.ints[0]);
            BlockGuiHandler.sendPacketToServer(bos);
            } catch (IOException e){}
        }
        super.mouseClicked(x, y, b);
    }
    
}