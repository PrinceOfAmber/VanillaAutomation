package de.universallp.va.core.network;

import de.universallp.va.client.ClientProxy;
import de.universallp.va.client.gui.*;
import de.universallp.va.core.container.ContainerAdvancedAnvil;
import de.universallp.va.core.container.ContainerFilteredHopper;
import de.universallp.va.core.container.ContainerXPHopper;
import de.universallp.va.core.network.messages.MessageSetFieldClient;
import de.universallp.va.core.tile.TileAdvancedAnvil;
import de.universallp.va.core.tile.TileFilteredHopper;
import de.universallp.va.core.tile.TilePlacer;
import de.universallp.va.core.tile.TileXPHopper;
import de.universallp.va.core.util.libs.LibGuiIDs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by universallp on 19.03.2016 13:54.
 */
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));


        if (ID == LibGuiIDs.GUI_PLACER) {
            PacketHandler.syncFieldClient(player, te, 0, 1);
            return new ContainerDispenser(player.inventory, (IInventory) te);
        } else if (ID == LibGuiIDs.GUI_XPHOPPER) {
            PacketHandler.syncFieldClient(player, te, 0, 0);
            PacketHandler.sendTo(new MessageSetFieldClient(0, ((TileXPHopper) te).getName(), te.getPos()), (EntityPlayerMP) player);
            return new ContainerXPHopper(player.inventory, (IInventory) te);
        } else if (ID == LibGuiIDs.GUI_FILTEREDHOPPER) {
            TileFilteredHopper teF = (TileFilteredHopper) te;
            PacketHandler.syncFieldClient(player, te, 0, 3);
            PacketHandler.sendTo(new MessageSetFieldClient(0, teF.getName(), te.getPos()), (EntityPlayerMP) player);
            return new ContainerFilteredHopper(player.inventory, (IInventory) te);
        } else if (ID == LibGuiIDs.GUI_ADVANCEDANVIL) {
            TileAdvancedAnvil teAnvil = (TileAdvancedAnvil) te;
            return new ContainerAdvancedAnvil(player.inventory, (IInventory) te);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (ID == LibGuiIDs.GUI_GUIDE)
            if (ClientProxy.hoveredEntry == null)
                return new GuiGuide();
            else
                return new GuiGuide(ClientProxy.hoveredEntry);


        if (ID == LibGuiIDs.GUI_PLACER)
            return new GuiPlacer(player.inventory, (TilePlacer) te, ((TilePlacer) te).reachDistance, ((TilePlacer) te).placeFace);
        else if (ID == LibGuiIDs.GUI_XPHOPPER)
            return new GuiXPHopper(player.inventory, (IInventory) te);
        else if (ID == LibGuiIDs.GUI_FILTEREDHOPPER)
            return new GuiFilteredHopper(player.inventory, (IInventory) te);
        else if (ID == LibGuiIDs.GUI_ADVANCEDANVIL)
            return new GuiAdvancedAnvil(player.inventory, (IInventory) te);
        return null;
    }
}
