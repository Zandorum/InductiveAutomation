/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cd4017be.automation.TileEntity;

import cd4017be.automation.Config;

/**
 *
 * @author CD4017BE
 */
public class SteamGenerator extends SteamEngine
{
    @Override
    protected int getTier() {return 1;}
    @Override
    public float getPower() {return Config.PsteamGen[1];}
    @Override
    public String getInventoryName() 
    {
        return "Steam Generator";
    }
}
