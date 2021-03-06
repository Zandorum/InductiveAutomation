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

import cd4017be.automation.TileEntity.Builder;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.TileContainer;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.GuiMachine;

/**
 *
 * @author CD4017BE
 */
public class GuiBuilder extends GuiMachine
{
    private static final String[] steps = {"Inactive", "Frame Y", "Frame Z", "Frame X", "Bottom", "Top", "North", "South", "West", "East", "Filling"};
    private static final String[] dirs = {"XZ", "XY", "ZY"};
    private Builder tileEntity;

    public GuiBuilder(Builder tileEntity, EntityPlayer player)
    {
        super(new TileContainer(tileEntity, player));
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() 
    {
        this.xSize = 176;
        this.ySize = 240;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) 
    {
        super.drawGuiContainerForegroundLayer(mx, my);
        this.drawInfo(116, 16, 52, 16, "\\i", "builder.frame");
        this.drawInfo(116, 34, 52, 16, "\\i", "builder.wall");
        this.drawInfo(152, 52, 16, 16, "\\i", "builder.stack");
        this.drawInfo(7, 75, 144, 6, "\\i", "builder.size");
        this.drawInfo(152, 70, 16, 16, "\\i", "builder.dir");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/builder.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        for (int i = 0; i < 8; i++)
	        this.drawStringCentered("" + tileEntity.netData.ints[i], this.guiLeft + 16 + 18 * i, this.guiTop + 74, 0x404040);
        this.drawStringCentered(dirs[tileEntity.netData.ints[9]], this.guiLeft + 160, this.guiTop + 74, 0x404040);
        this.drawStringCentered(steps[tileEntity.netData.ints[8]], this.guiLeft + 34, this.guiTop + 20, 0x404040);
        this.drawStringCentered("< Frame", this.guiLeft + 142, this.guiTop + 20, 0x404040);
        this.drawStringCentered("< Walls", this.guiLeft + 142, this.guiTop + 38, 0x404040);
        this.drawStringCentered(tileEntity.getName(), this.guiLeft + this.xSize / 2, this.guiTop + 6, 0x404040);
        this.drawStringCentered(I18n.translateToLocal("container.inventory"), this.guiLeft + this.xSize / 2, this.guiTop + 145, 0x404040);
        super.drawGuiContainerBackgroundLayer(var1, var2, var3);
    }

    @Override
    protected void mouseClicked(int x, int y, int b) throws IOException 
    {
        if (this.isPointInRegion(7, 69, 144, 5, x, y))
        {
            sendClick((((x - this.guiLeft - 7) / 18) << 1) | 1);
        } else
        if (this.isPointInRegion(7, 82, 144, 5, x, y))
        {
            sendClick(((x - this.guiLeft - 7) / 18) << 1);
        } else
        if (this.isPointInRegion(151, 69, 18, 18, x, y))
        {
            sendClick(16);
        } else
        if (this.isPointInRegion(7, 15, 54, 18, x, y))
        {
            sendClick(17);
        }
        super.mouseClicked(x, y, b);
    }
    
    private void sendClick(int n) throws IOException
    {
        PacketBuffer dos = tileEntity.getPacketTargetData();
        dos.writeByte(n + AutomatedTile.CmdOffset);
        BlockGuiHandler.sendPacketToServer(dos);
    }
    
}
