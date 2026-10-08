package com.conner.communityeconomy;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TalentManager {

    private final NamespacedKey talentKey;

    public TalentManager(CommunityEconomy plugin) {
        talentKey = new NamespacedKey(plugin, "talent");
    }

    // Verify talent
    public boolean isTalent(ItemStack item) {

        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(talentKey, PersistentDataType.BYTE);
    }

    // Helper method to mint talent currency
    private ItemStack createTalent(int amount) {
        ItemStack talent = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta meta = talent.getItemMeta();
        meta.displayName(Component.text("Talent"));
        meta.setItemModel(new NamespacedKey("communityeconomy", "talent"));
        meta.getPersistentDataContainer().set(talentKey, PersistentDataType.BYTE, (byte) 1);
        talent.setItemMeta(meta);

        return talent;
    }

    // OP command to give talents to players
    public void giveTalents(Player player, int amount) {
        int remaining = amount;
        boolean droppedTalents = false;

        while (remaining > 0) {
            int stackSize = Math.min(remaining, 64);
            ItemStack talents = createTalent(stackSize);
            var leftovers = player.getInventory().addItem(talents);

            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                droppedTalents = true;
            }

            remaining -= stackSize;
        }

        if (droppedTalents) {
            player.sendMessage("§eYour inventory was full. §6Some Talents were dropped at your feet.");
        }
    }

}