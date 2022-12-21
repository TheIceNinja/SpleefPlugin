package net.theiceninja.spleef.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    public static ItemStack createItem(Material material, int amount, String displayName) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setAmount(amount);
        itemMeta.setDisplayName(ColorUtils.color(displayName));
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
