package de.universallp.va.core.tile;

import de.universallp.va.core.container.handler.FilteredItemHandler;
import de.universallp.va.core.util.ICustomField;
import de.universallp.va.core.util.libs.LibLocalization;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by universallp on 31.03.2016 15:12.
 */
public class TileFilteredHopper extends TileEntityHopper implements ICustomField {

    private EnumFilter filterMode = EnumFilter.BLACKLIST;

    public TileFilteredHopper() {
        setCustomName(I18n.format(LibLocalization.GUI_FILTEREDHOPPER));
        ReflectionHelper.setPrivateValue(TileEntityHopper.class, this, new ItemStack[10], "inventory");
    }

    public static boolean captureDroppedItems(TileFilteredHopper hopper) {
        if (net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper)) {
            return true;
        }
        IInventory iinventory = getHopperInventory(hopper);

        if (iinventory != null) {
            EnumFacing enumfacing = EnumFacing.DOWN;

            if (isInventoryEmpty(iinventory, enumfacing)) {
                return false;
            }

            if (iinventory instanceof ISidedInventory) {
                ISidedInventory isidedinventory = (ISidedInventory) iinventory;
                int[] aint = isidedinventory.getSlotsForFace(enumfacing);

                for (int i = 0; i < aint.length; ++i) {
                    if (pullItemFromSlot(hopper, iinventory, aint[i], enumfacing)) {
                        return true;
                    }
                }
            } else {
                int j = iinventory.getSizeInventory();

                for (int k = 0; k < j; ++k) {
                    if (pullItemFromSlot(hopper, iinventory, k, enumfacing)) {
                        return true;
                    }
                }
            }
        } else {
            for (EntityItem entityitem : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(), hopper.getZPos())) {
                if (putDropInInventoryAllSlots(hopper, entityitem)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean captureDrops(TileFilteredHopper hopper) {
        if (net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper)) {
            return true;
        }
        IInventory iinventory = getHopperInventory(hopper);

        if (iinventory != null) {
            EnumFacing enumfacing = EnumFacing.DOWN;

            if (isInventoryEmpty(iinventory, enumfacing))
                return false;

            if (iinventory instanceof ISidedInventory) {
                ISidedInventory isidedinventory = (ISidedInventory) iinventory;
                int[] aint = isidedinventory.getSlotsForFace(enumfacing);

                for (int i = 0; i < aint.length; ++i)
                    if (hopper.isItemValid(isidedinventory.getStackInSlot(aint[i])))
                        if (pullItemFromSlot(hopper, iinventory, aint[i], enumfacing))
                            return true;

            } else {
                int j = iinventory.getSizeInventory();

                for (int k = 0; k < j; ++k) {
                    if (pullItemFromSlot(hopper, iinventory, k, enumfacing)) {
                        return true;
                    }
                }
            }
        } else {
            for (EntityItem entityitem : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(), hopper.getZPos())) {
                System.out.println(hopper.getFilterMode());
                if (hopper.isItemValid(entityitem.getEntityItem()))
                    if (putDropInInventoryAllSlots(hopper, entityitem)) {
                        return true;
                    }
            }
        }

        return false;
    }

    private static boolean pullItemFromSlot(TileFilteredHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);

        if (itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction) && hopper.isItemValid(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing) null);

            if (itemstack2 == null || itemstack2.stackSize == 0) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }

    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
    }

    private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory) {
            ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int i = 0; i < aint.length; ++i)
                if (isidedinventory.getStackInSlot(aint[i]) != null)
                    return false;

        } else {
            int j = inventoryIn.getSizeInventory();

            for (int k = 0; k < j; ++k)
                if (inventoryIn.getStackInSlot(k) != null)
                    return false;

        }

        return true;
    }

    @Override
    protected IItemHandler createUnSidedHandler() {
        return new FilteredItemHandler(this);
    }

    @Override
    public boolean updateHopper() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
                boolean flag = false;

                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }

                if (!this.isFull()) {
                    flag = captureDrops(this) || flag;
                }

                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        } else
            return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (getHasItemFilter() && index < 5)
            return isItemValid(stack);
        return false;
    }

    /**
     * Returns true if an item is allowed by the current filter settings
     */
    public boolean isItemValid(ItemStack s) {
        if (s == null)
            return false;

        if (!getHasItemFilter())
            return true;

        switch (getFilterMode()) {
            case BLACKLIST:
                return !getItemFilter().contains(s);
            case WHITELIST:
                return getItemFilter().contains(s);
        }
        return false;
    }

    public List<ItemStack> getItemFilter() {
        List<ItemStack> filter = new ArrayList<ItemStack>();

        for (int i = TileXPHopper.hopperInv; i < getSizeInventory(); i++) {
            filter.add(getStackInSlot(i));
        }

        return filter;
    }

    public EnumFilter getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(EnumFilter filterMode) {
        this.filterMode = filterMode;
    }

    // Copied Vanilla code

    public boolean getHasItemFilter() {
        return getItemFilter() != null && getItemFilter().size() > 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0)
            filterMode = EnumFilter.values()[value];
        else
            super.setField(id, value);
    }

    @Override
    public void setStringField(int id, String val) {
        if (id == 0)
            setCustomName(val);
    }

    @Override
    public String getStringField(int id) {
        if (id == 0)
            return getName();
        return null;
    }

    private boolean isEmpty() {
        for (int i = 0; i < TileXPHopper.hopperInv; i++) {
            ItemStack itemstack = getStackInSlot(i);
            if (itemstack != null)
                return false;
        }
        return true;
    }

    private boolean isFull() {
        for (int i = 0; i < TileXPHopper.hopperInv; i++) {
            ItemStack itemstack = getStackInSlot(i);
            if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize())
                return false;
        }

        return true;
    }

    private boolean transferItemsOut() {
        if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this))
            return true;
        IInventory iinventory = this.getInventoryForHopperTransfer();

        if (iinventory == null) {
            return false;
        } else {
            EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();

            if (this.isInventoryFull(iinventory, enumfacing)) {
                return false;
            } else {
                for (int i = 0; i < TileXPHopper.hopperInv; ++i) {
                    if (this.getStackInSlot(i) != null) {
                        ItemStack itemstack = this.getStackInSlot(i).copy();
                        ItemStack itemstack1 = putStackInInventoryAllSlots(iinventory, this.decrStackSize(i, 1), enumfacing);

                        if (itemstack1 == null || itemstack1.stackSize == 0) {
                            iinventory.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory) {
            ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k = 0; k < aint.length; ++k) {
                ItemStack itemstack1 = isidedinventory.getStackInSlot(aint[k]);

                if (itemstack1 == null || itemstack1.stackSize != itemstack1.getMaxStackSize())
                    return false;
            }
        } else {
            int i = inventoryIn.getSizeInventory();

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventoryIn.getStackInSlot(j);

                if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    private IInventory getInventoryForHopperTransfer() {
        EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata());
        return getInventoryAtPosition(this.getWorld(), this.getXPos() + (double) enumfacing.getFrontOffsetX(), this.getYPos() + (double) enumfacing.getFrontOffsetY(), this.getZPos() + (double) enumfacing.getFrontOffsetZ());
    }


    public enum EnumFilter {
        WHITELIST,
        BLACKLIST
    }
}
