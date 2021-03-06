package cd4017be.automation.pipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import cd4017be.lib.templates.SharedNetwork;
import cd4017be.lib.util.Utils;

public class WarpPipePhysics extends SharedNetwork<BasicWarpPipe, WarpPipePhysics> {

	public HashMap<Long, ConComp> connectors = new HashMap<Long, ConComp>();
	public HashSet<ITickable> activeCon = new HashSet<ITickable>();
	public ArrayList<IItemDest> itemDest = new ArrayList<IItemDest>();
	public ArrayList<IFluidDest> fluidDest = new ArrayList<IFluidDest>();
	public boolean sortItemDest = false;
	public boolean sortFluidDest = false;

	public WarpPipePhysics(BasicWarpPipe core) {
		super(core);
	}
	
	protected WarpPipePhysics(HashMap<Long, BasicWarpPipe> comps) {
		super(comps);
	}
	
	public void addConnector(BasicWarpPipe pipe, ConComp con) {
		this.addConnector(SharedNetwork.SidedPosUID(pipe.Uid, con.side), con);
	}
	
	public ConComp remConnector(BasicWarpPipe pipe, byte side) {
		return this.remConnector(SharedNetwork.SidedPosUID(pipe.Uid, side));
	}
	
	public void reorder(ConComp con) {
		if (con instanceof IItemDest) sortItemDest = true;
		if (con instanceof IFluidDest) sortFluidDest = true;
	}
	
	private void addConnector(long pos, ConComp con) {
		if (con == null) return;
		connectors.put(pos, con);
		if (con instanceof ITickable) activeCon.add((ITickable)con);
		if (con instanceof IItemDest) {
			itemDest.add((IItemDest)con);
			sortItemDest = true;
		}
		if (con instanceof IFluidDest) {
			fluidDest.add((IFluidDest)con);
			sortFluidDest = true;
		}
	}
	
	private ConComp remConnector(long pos) {
		ConComp con = connectors.remove(pos);
		if (con instanceof ITickable) activeCon.remove(con);
		if (con instanceof IItemDest) itemDest.remove(con);
		if (con instanceof IFluidDest) fluidDest.remove(con);
		return con;
	}
	
	@Override
	public void onMerged(WarpPipePhysics network) {
		super.onMerged(network);
		for (Entry<Long, ConComp> e : network.connectors.entrySet())
			this.addConnector(e.getKey(), e.getValue());
	}

	@Override
	public WarpPipePhysics onSplit(HashMap<Long, BasicWarpPipe> comps) {
		WarpPipePhysics physics = new WarpPipePhysics(comps);
		long l;
		for (Entry<Long, BasicWarpPipe> e : comps.entrySet())
			for (int i = 0; i < 6; i++)
				if (e.getValue().con[i] >= 2) {
					l = SidedPosUID(e.getKey(), i);
					physics.addConnector(l, this.remConnector(l));
				}
		return physics;
	}

	@Override
	public void remove(BasicWarpPipe comp) {
		super.remove(comp);
		long l;
		for (int i = 0; i < 6; i++)
			if (comp.con[i] >= 2) {
				l = SharedNetwork.SidedPosUID(comp.Uid, i);
				this.remConnector(l);
			}
	}
	
	public void updateLink(BasicWarpPipe pipe) {
		TileEntity te;
		ConComp con;
		for (byte i = 0; i < 6; i++) 
			if (pipe.canConnect(i) && (te = Utils.getTileOnSide(pipe.pipe, i)) != null && te instanceof IWarpPipe) {
				BasicWarpPipe p = ((IWarpPipe)te).getWarpPipe();
				if (p.canConnect((byte)(i^1))) add(p);
			} else if (pipe.con[i] >= 2) {
				con = connectors.get(SharedNetwork.SidedPosUID(pipe.Uid, i));
				if (con instanceof IObjLink) ((IObjLink)con).updateLink();
			}
	}

	@Override
	public void updatePhysics() {
		if (sortItemDest) {
			Collections.sort(itemDest, destSort);
			sortItemDest = false;
		}
		if (sortFluidDest) {
			Collections.sort(fluidDest, destSort);
			sortFluidDest = false;
		}
		for (ITickable con : activeCon) con.update();
	}
	
	/**
	 * Insert an item stack into valid destinations
	 * @param item
	 * @return the result if not possible
	 */
	public ItemStack insertItem(ItemStack item, byte pr)
	{
		if (item == null) return null;
		for (IItemDest dest : itemDest) {
			if (dest.getPriority() <= pr && dest.isValid()) {
				item = dest.insertItem(item);
				if (item == null) return null;
				if (dest.blockItem(item)) return item;
			}
		}
		return item;
	}
	
	/**
	 * Insert an fluid stack into valid destinations
	 * @param fluid
	 * @return the result if not possible
	 */
	public FluidStack insertFluid(FluidStack fluid, byte pr)
	{
		if (fluid == null) return null;
		for (IFluidDest dest : fluidDest) {
			if (dest.getPriority() <= pr && dest.isValid()) {
				fluid = dest.insertFluid(fluid);
				if (fluid == null) return null;
				if (dest.blockFluid(fluid)) return fluid;
			}
		}
		return fluid;
	}
	
	public static interface IObjLink {
		public boolean isValid();
		public void updateLink();
	}
	
	public static interface IPrioritySorted {
		public byte getPriority();
	}
	
	public static interface IItemDest extends IObjLink, IPrioritySorted {
		public ItemStack insertItem(ItemStack item);
		public boolean blockItem(ItemStack item);
	}
	
	public static interface IFluidDest extends IObjLink, IPrioritySorted {
		public FluidStack insertFluid(FluidStack fluid);
		public boolean blockFluid(FluidStack fluid);
	}
	
	public static final Comparator<IPrioritySorted> destSort = new Comparator<IPrioritySorted>(){
		@Override
		public int compare(IPrioritySorted arg0, IPrioritySorted arg1) {
			return arg1.getPriority() - arg0.getPriority();
		}
	};
	public static final Comparator<IPrioritySorted> srcSort = new Comparator<IPrioritySorted>(){
		@Override
		public int compare(IPrioritySorted arg0, IPrioritySorted arg1) {
			return arg0.getPriority() - arg1.getPriority();
		}
	};

}
