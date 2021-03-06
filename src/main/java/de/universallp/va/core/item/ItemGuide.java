package de.universallp.va.core.item;

import de.universallp.va.VanillaAutomation;
import de.universallp.va.client.ClientProxy;
import de.universallp.va.client.gui.screen.VisualRecipe;
import de.universallp.va.core.util.libs.LibGuiIDs;
import de.universallp.va.core.util.libs.LibLocalization;
import de.universallp.va.core.util.libs.LibNames;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by universallp on 21.03.2016 15:43.
 */
public class ItemGuide extends ItemVA {

    private static VisualRecipe recipe;

    public ItemGuide() {
        super(LibNames.ITEM_GUIDE);
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.openGui(VanillaAutomation.instance, LibGuiIDs.GUI_GUIDE, worldIn, 0, 0, 0);
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!VanillaAutomation.proxy.isServer() && ClientProxy.hoveredEntry != null) {
            player.openGui(VanillaAutomation.instance, LibGuiIDs.GUI_GUIDE, world, 0, 0, 0);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(TextFormatting.GRAY + I18n.format(LibLocalization.GUIDE_DESC));
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @Override
    public VisualRecipe getRecipe() {
        if (recipe != null)
            return recipe;

        ItemStack book = new ItemStack(Items.BOOK, 1);
        ItemStack piston = new ItemStack(Blocks.PISTON, 1);

        recipe = new VisualRecipe(new ItemStack[]{book, piston}, new ItemStack(this, 1), VisualRecipe.EnumRecipeType.SHAPELESS);
        return recipe;
    }
}
