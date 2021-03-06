/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cd4017be.automation.Gui;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import org.lwjgl.opengl.GL11;

import cd4017be.automation.TileEntity.ELink;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.TileContainer;
import cd4017be.lib.TooltipInfo;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.GuiMachine;

/**
 *
 * @author CD4017BE
 */
public class GuiELink extends GuiMachine
{
    
    private final ELink tileEntity;
    
    public GuiELink(ELink tileEntity, EntityPlayer player)
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
        this.drawInfo(8, 16, 160, 8, "\\i", "link.link");
        this.drawInfo(8, 24, 160, 8, "\\i", "link.int");
        this.drawInfo(7, 51, 36, 18, "\\i", "rstCtr");
        this.drawInfo(118, 52, 30, 16, "\\i", "link.ref0");
        this.drawInfo(118, 34, 30, 16, "\\i", "link.ref1");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/ELink.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int n = tileEntity.getStorageScaled(160);
        this.drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 16, 0, 240, n, 8);
        n = tileEntity.getVoltageScaled(160);
        this.drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 24, 0, 248, n, 8);
        this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 51, tileEntity.isRedstoneEnabled() ? 194 : 176, 18, 18, 18);
        this.drawTexturedModalRect(this.guiLeft + 25, this.guiTop + 51, 176 + tileEntity.netData.ints[2] * 18, 0, 18, 18);
        this.drawStringCentered(tileEntity.netData.ints[0] + "V", this.guiLeft + 133, this.guiTop + 56, 0x404040);
        this.drawStringCentered(tileEntity.netData.ints[1] + "V", this.guiLeft + 133, this.guiTop + 38, 0x404040);
        this.drawStringCentered(String.format("%+.1f %s", tileEntity.netData.floats[3] / 1000F, TooltipInfo.getPowerUnit()), this.guiLeft + 34, this.guiTop + 38, 0x404040);
        this.drawStringCentered(String.format("%.0f / %.0f %s", tileEntity.netData.floats[1] / 1000F, tileEntity.netData.floats[2] / 1000F, TooltipInfo.getEnergyUnit()), this.guiLeft + 88, this.guiTop + 16, 0x404040);
        this.drawStringCentered(String.format("%.2f V", tileEntity.netData.floats[0]), this.guiLeft + 88, this.guiTop + 24, 0x404040);
        this.drawStringCentered("Umin=", this.guiLeft + 79, this.guiTop + 56, 0x404040);
        this.drawStringCentered("Umax=", this.guiLeft + 79, this.guiTop + 38, 0x404040);
        this.drawStringCentered(tileEntity.getName(), this.guiLeft + this.xSize / 2, this.guiTop + 4, 0x404040);
        this.drawStringCentered(I18n.translateToLocal("container.inventory"), this.guiLeft + this.xSize / 2, this.guiTop + 74, 0x404040);
        super.drawGuiContainerBackgroundLayer(var1, var2, var3);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) throws IOException 
    {
        byte cmd = -1;
        if (this.isPointInRegion(7, 51, 18, 18, x, y))
        {
        	tileEntity.netData.ints[2] ^= 2;
            cmd = 2;
        } else
        if (this.isPointInRegion(25, 51, 18, 18, x, y))
        {
        	tileEntity.netData.ints[2] ^= 1;
            cmd = 2;
        } else
        if (this.isPointInRegion(98, 52, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] -= b == 0 ? 10 : 1000;
            cmd = 0;
        } else
        if (this.isPointInRegion(108, 52, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] -= b == 0 ? 1 : 100;
            cmd = 0;
        } else
        if (this.isPointInRegion(148, 52, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] += b == 0 ? 1 : 100;
            cmd = 0;
        } else
        if (this.isPointInRegion(158, 52, 10, 16, x, y))
        {
            tileEntity.netData.ints[0] += b == 0 ? 10 : 1000;
            cmd = 0;
        } else
        if (this.isPointInRegion(98, 34, 10, 16, x, y))
        {
            tileEntity.netData.ints[1] -= b == 0 ? 10 : 1000;
            cmd = 1;
        } else
        if (this.isPointInRegion(108, 34, 10, 16, x, y))
        {
            tileEntity.netData.ints[1] -= b == 0 ? 1 : 100;
            cmd = 1;
        } else
        if (this.isPointInRegion(148, 34, 10, 16, x, y))
        {
            tileEntity.netData.ints[1] += b == 0 ? 1 : 100;
            cmd = 1;
        } else
        if (this.isPointInRegion(158, 34, 10, 16, x, y))
        {
            tileEntity.netData.ints[1] += b == 0 ? 10 : 1000;
            cmd = 1;
        }
        if (cmd >= 0)
        {
            if (tileEntity.netData.ints[0] < 0) tileEntity.netData.ints[0] = 0;
            if (tileEntity.netData.ints[0] > tileEntity.energy.Umax) tileEntity.netData.ints[0] = tileEntity.energy.Umax;
            if (tileEntity.netData.ints[1] < 0) tileEntity.netData.ints[1] = 0;
            if (tileEntity.netData.ints[1] > tileEntity.energy.Umax) tileEntity.netData.ints[1] = tileEntity.energy.Umax;
            PacketBuffer dos = tileEntity.getPacketTargetData();
            dos.writeByte(AutomatedTile.CmdOffset + cmd);
            dos.writeInt(tileEntity.netData.ints[cmd]);
            BlockGuiHandler.sendPacketToServer(dos);
        }
        super.mouseClicked(x, y, b);
    }
    
}
