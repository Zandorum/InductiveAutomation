/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cd4017be.automation.Gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.templates.GuiMachine;

/**
 *
 * @author CD4017BE
 */
public class GuiItemUpgrade extends GuiMachine
{
    private final ContainerItemUpgrade container;
    
    public GuiItemUpgrade(ContainerItemUpgrade container)
    {
        super(container);
        this.container = container;
    }
    
    @Override
    public void initGui() 
    {
        this.xSize = 176;
        this.ySize = 150;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) 
    {
        super.drawGuiContainerForegroundLayer(mx, my);
        this.drawInfo(43, 15, 9, 18, "\\i", "gui.filter.try");
        this.drawInfo(52, 15, 9, 18, "\\i", "gui.filter.invert");
        this.drawInfo(61, 15, 18, 18, "\\i", "gui.filter.ore");
        this.drawInfo(79, 15, 18, 9, "\\i", "gui.filter.nbt");
        this.drawInfo(79, 24, 18, 9, "\\i", "gui.filter.meta");
        this.drawInfo(97, 15, 18, 18, "\\i", "gui.filter.targetI");
        this.drawInfo(115, 15, 18, 18, "\\i", "gui.rstCtr");
        this.drawInfo(133, 20, 18, 8, "\\i", "gui.filter.priority");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/itemUpgrade.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if ((this.container.inventory.upgrade.mode & 1) != 0) this.drawTexturedModalRect(this.guiLeft + 52, this.guiTop + 15, 185, 0, 9, 18);
        if ((this.container.inventory.upgrade.mode & 2) != 0) this.drawTexturedModalRect(this.guiLeft + 43, this.guiTop + 15, 176, 0, 9, 18);
        if ((this.container.inventory.upgrade.mode & 4) != 0) this.drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 24, 212, 9, 18, 9);
        if ((this.container.inventory.upgrade.mode & 8) != 0) this.drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 15, 212, 0, 18, 9);
        if ((this.container.inventory.upgrade.mode & 16) != 0) this.drawTexturedModalRect(this.guiLeft + 61, this.guiTop + 15, 194, 0, 18, 18);
        if ((this.container.inventory.upgrade.mode & 32) != 0) this.drawTexturedModalRect(this.guiLeft + 97, this.guiTop + 15, 212, 18, 18, 18);
        if ((this.container.inventory.upgrade.mode & 64) != 0) this.drawTexturedModalRect(this.guiLeft + 115, this.guiTop + 15, (this.container.inventory.upgrade.mode & 128) != 0 ? 194 : 176, 18, 18, 18);
        this.drawStringCentered("" + this.container.inventory.upgrade.priority, this.guiLeft + 142, this.guiTop + 20, 0x404040);
        this.drawStringCentered(this.container.inventory.getInventoryName(), this.guiLeft + this.xSize / 2, this.guiTop + 4, 0x404040);
        this.drawStringCentered("Inventory", this.guiLeft + this.xSize / 2, this.guiTop + 54, 0x404040);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) 
    {
        byte a = -1;
        if (this.func_146978_c(43, 15, 9, 18, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 2;
        } else
        if (this.func_146978_c(52, 15, 9, 18, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 1;
        } else
        if (this.func_146978_c(79, 24, 18, 9, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 4;
        } else
        if (this.func_146978_c(79, 15, 18, 9, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 8;
        } else
        if (this.func_146978_c(61, 15, 18, 18, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 16;
        } else
        if (this.func_146978_c(97, 15, 18, 18, x, y)) {
            a = 0;
            this.container.inventory.upgrade.mode ^= 32;
        } else 
        if (this.func_146978_c(115, 15, 18, 18, x, y))
        {
            a = 0;
            byte m = this.container.inventory.upgrade.mode;
            if ((m & 192) != 64) this.container.inventory.upgrade.mode ^= 64;
            if ((m & 64) != 0) this.container.inventory.upgrade.mode ^= 128;
        } else
        if (this.func_146978_c(133, 15, 18, 5, x, y)) {
        	a = 1;
        	this.container.inventory.upgrade.priority += b == 0 ? 1 : 8;
        } else
        if (this.func_146978_c(133, 28, 18, 5, x, y)) {
            a = 1;
            this.container.inventory.upgrade.priority -= b == 0 ? 1 : 8;
        }
        if (a >= 0)
        {
            try {
            ByteArrayOutputStream bos = BlockGuiHandler.getPacketTargetData(0, -1, 0);
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(a);
            if (a == 0) dos.writeByte(this.container.inventory.upgrade.mode);
            if (a == 1) dos.writeByte(this.container.inventory.upgrade.priority);
            BlockGuiHandler.sendPacketToServer(bos);
            } catch (IOException e){}
        }
        super.mouseClicked(x, y, b);
    }
}