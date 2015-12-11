package cd4017be.automation.Gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cd4017be.automation.TileEntity.GraviCond;
import cd4017be.lib.TileContainer;
import cd4017be.lib.templates.GuiMachine;
import cd4017be.lib.util.Utils;

public class GuiGraviCond extends GuiMachine 
{
	
	private final GraviCond tileEntity;
	
	public GuiGraviCond(GraviCond tileEntity, EntityPlayer player) 
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
        this.drawInfo(133, 37, 18, 10, "Needed:", Utils.formatNumber((double)tileEntity.netData.ints[1] * 1000D, 4, 0) + "g");
        this.drawInfo(26, 16, 70, 12, "\\i", "gui.grav.energy");
        this.drawInfo(26, 34, 70, 12, "\\i", "gui.grav.trash");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) 
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/gravicompr.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int n = tileEntity.getEnergyScaled(70);
        this.drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 16, 176, 0, n, 12);
        n = tileEntity.getMatterScaled(70);
        this.drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 34, 176, 12, n, 12);
        n = tileEntity.getProgressScaled(18);
        this.drawTexturedModalRect(this.guiLeft + 133, this.guiTop + 37, 176, 24, n, 10);
        this.drawLiquidConfig(tileEntity, -18, 7);
        this.drawItemConfig(tileEntity, -54, 7);
        this.drawEnergyConfig(tileEntity, -72, 7);
        this.drawStringCentered("Matter:", this.guiLeft + 61, this.guiTop + 52, 0x404040);
        this.drawStringCentered(Utils.formatNumber((double)tileEntity.netData.ints[0] * 1000D, 4, 0) + "g", this.guiLeft + 61, this.guiTop + 60, 0x404040);
        this.drawStringCentered(String.format("%.0f kJ", tileEntity.netData.floats[0] / 1000F), this.guiLeft + 61, this.guiTop + 18, 0x404040);
        this.drawStringCentered(tileEntity.getMatterScaled(100) + " %", this.guiLeft + 61, this.guiTop + 36, 0x404040);
        this.drawStringCentered(tileEntity.getInventoryName(), this.guiLeft + this.xSize / 2, this.guiTop + 4, 0x404040);
        this.drawStringCentered("Inventory", this.guiLeft + this.xSize / 2, this.guiTop + 72, 0x404040);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) 
    {
        this.clickLiquidConfig(tileEntity, x - this.guiLeft + 18, y - this.guiTop - 7);
        this.clickItemConfig(tileEntity, x - this.guiLeft + 54, y - this.guiTop - 7);
        this.clickEnergyConfig(tileEntity, x - this.guiLeft + 72, y - this.guiTop - 7);
        super.mouseClicked(x, y, b);
    }

}