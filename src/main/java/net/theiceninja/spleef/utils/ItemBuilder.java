package net.theiceninja.spleef.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    public static ItemStack createItem(Material material, int amount, String displayName, List<String> lore, int customModelData) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setAmount(amount);
        itemMeta.setDisplayName(ColorUtils.color(displayName));
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public static ItemStack createItem(Material material, int amount, String displayName) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setAmount(amount);
        itemMeta.setDisplayName(ColorUtils.color(displayName));
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public static ItemStack createItem(Material material, int amount, String displayName, Integer customModelDate) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setAmount(amount);
        itemMeta.setDisplayName(ColorUtils.color(displayName));
        itemMeta.setCustomModelData(customModelDate);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
