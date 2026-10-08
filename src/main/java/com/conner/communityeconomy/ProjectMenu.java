package com.conner.communityeconomy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;

public class ProjectMenu {

    // Main /projects menu
    public static void openMain(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, Component.text("Community Projects"));

        // Tasks
        ItemStack tasks = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta tasksMeta = tasks.getItemMeta();
        tasksMeta.displayName(Component.text("Tasks"));
        tasks.setItemMeta(tasksMeta);
        menu.setItem(11, tasks);

        // Completed
        ItemStack completed = new ItemStack(Material.BOOK);
        ItemMeta completedMeta = completed.getItemMeta();
        completedMeta.displayName(Component.text("Completed"));
        completed.setItemMeta(completedMeta);
        menu.setItem(15, completed);

        player.openInventory(menu);
    }

    // Open tasks
    public static void openTasks(Player player, List<Project> projects, int page) {
        Inventory menu = Bukkit.createInventory(null, 54, Component.text("Community Projects - Tasks - " + (page + 1)));
        addBorder(menu);

        // Get incomplete projects
        List<Project> incompleteProjects = new ArrayList<>();

        for (Project project : projects) {
            if (!project.isCompleted()) {
                incompleteProjects.add(project);
            }
        }

        // Interior slots
        List<Integer> projectSlots = new ArrayList<>();

        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 7; column++) {
                projectSlots.add(row * 9 + column);
            }
        }

        int projectsPerPage = projectSlots.size(); // 28
        int startIndex = page * projectsPerPage;
        int endIndex = Math.min(startIndex + projectsPerPage, incompleteProjects.size());
        int menuIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Project project = incompleteProjects.get(i);
            ItemStack projectItem = new ItemStack(project.getDisplayItem());
            ItemMeta meta = projectItem.getItemMeta();
            meta.displayName(Component.text(project.getName()).decoration(TextDecoration.ITALIC, false));

            List<String> lore = new ArrayList<>();
            lore.add("§c✗ Incomplete");
            String talentWord = project.getReward() == 1 ? "Talent" : "Talents";
            lore.add("§6Reward: §e" + project.getReward() + " " + talentWord);
            meta.lore(lore.stream().map(line -> LegacyComponentSerializer.legacySection().deserialize(line)).toList());
            projectItem.setItemMeta(meta);
            menu.setItem(projectSlots.get(menuIndex), projectItem);
            menuIndex++;
        }

        // Previous page
        if (page > 0) {
            ItemStack previous = new ItemStack(Material.ARROW);
            ItemMeta meta = previous.getItemMeta();
            meta.displayName(Component.text("§fPrevious Page"));
            previous.setItemMeta(meta);
            menu.setItem(48, previous);
        }

        // Page indicator
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();

        int totalPages = Math.max(
                1,
                (int) Math.ceil(incompleteProjects.size() / 28.0)
        );

        pageMeta.displayName(Component.text("Page " + (page + 1) + " / " + totalPages, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        pageItem.setItemMeta(pageMeta);
        menu.setItem(49, pageItem);

        // Next page
        if (endIndex < incompleteProjects.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.displayName(Component.text("§fNext Page"));
            next.setItemMeta(meta);
            menu.setItem(50, next);
        }

        // Back
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(Component.text("§cBack"));
        back.setItemMeta(backMeta);
        menu.setItem(45, back);
        player.openInventory(menu);
    }

    // Completed page
    public static void openCompleted(Player player, List<Project> projects, int page) {
        Inventory menu = Bukkit.createInventory(null, 54, Component.text("Community Projects - Completed - " + (page + 1)));
        addBorder(menu);

        // Get completed projects
        List<Project> completedProjects = new ArrayList<>();

        for (Project project : projects) {
            if (project.isCompleted()) {
                completedProjects.add(project);
            }
        }

        // Interior project slots
        List<Integer> projectSlots = new ArrayList<>();

        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 7; column++) {
                projectSlots.add(row * 9 + column);
            }
        }

        int projectsPerPage = projectSlots.size();
        int startIndex = page * projectsPerPage;
        int endIndex = Math.min(startIndex + projectsPerPage, completedProjects.size());
        int menuIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Project project = completedProjects.get(i);
            ItemStack projectItem = new ItemStack(project.getDisplayItem());
            ItemMeta meta = projectItem.getItemMeta();
            meta.displayName(Component.text(project.getName()).decoration(TextDecoration.ITALIC, false));

            List<String> lore = new ArrayList<>();
            lore.add("§a✓ Completed");
            String talentWord = project.getReward() == 1 ? "Talent" : "Talents";
            lore.add("§7Earned: " + project.getReward() + " " + talentWord);
            lore.add("");

            if (project.hasLocation()) {
                lore.add("§eLocation");
                lore.add("§7X: " + project.getX());
                lore.add("§7Y: " + project.getY());
                lore.add("§7Z: " + project.getZ());
                lore.add("§7World: " + getDisplayWorldName(project.getWorld()));
            } else {
                lore.add("§cLocation not set");
            }

            meta.lore(lore.stream().map(line -> LegacyComponentSerializer.legacySection().deserialize(line)).toList());
            projectItem.setItemMeta(meta);
            menu.setItem(projectSlots.get(menuIndex), projectItem);
            menuIndex++;
        }

        // Previous page
        if (page > 0) {
            ItemStack previous = new ItemStack(Material.ARROW);
            ItemMeta meta = previous.getItemMeta();
            meta.displayName(Component.text("§fPrevious Page"));
            previous.setItemMeta(meta);
            menu.setItem(48, previous);
        }

        // Page indicator
        int totalPages = Math.max(1, (int) Math.ceil(completedProjects.size() / 28.0));
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.displayName(Component.text("Page " + (page + 1) + " / " + totalPages, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        pageItem.setItemMeta(pageMeta);
        menu.setItem(49, pageItem);

        // Next page
        if (endIndex < completedProjects.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.displayName(Component.text("§fNext Page"));
            next.setItemMeta(meta);
            menu.setItem(50, next);
        }

        // Back
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(Component.text("§cBack"));
        back.setItemMeta(backMeta);
        menu.setItem(45, back);
        player.openInventory(menu);
    }

    public static void openConfirmation(Player player, Project project) {
        Inventory menu = Bukkit.createInventory(null, 27, Component.text("Complete: " + project.getName()));

        // Project being completed
        ItemStack projectItem = new ItemStack(project.getDisplayItem());
        ItemMeta projectMeta = projectItem.getItemMeta();
        projectMeta.displayName(Component.text(project.getName()).decoration(TextDecoration.ITALIC, false));
        projectItem.setItemMeta(projectMeta);
        menu.setItem(13, projectItem);

        // Yes
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(Component.text("Yes, I completed this"));
        confirm.setItemMeta(confirmMeta);
        menu.setItem(11, confirm);

        // No
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(Component.text("Cancel"));
        cancel.setItemMeta(cancelMeta);
        menu.setItem(15, cancel);
        player.openInventory(menu);
    }

    // Create gray border in menus
    private static void addBorder(Inventory menu) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.displayName(Component.text(" "));
        border.setItemMeta(borderMeta);
        int rows = menu.getSize() / 9;

        for (int slot = 0; slot < menu.getSize(); slot++) {
            int row = slot / 9;
            int column = slot % 9;

            if (row == 0 || row == rows - 1 || column == 0 || column == 8) {
                menu.setItem(slot, border);
            }
        }
    }

    // Change world names for viewing
    private static String getDisplayWorldName(String worldName) {
        return switch (worldName) {
            case "world" -> "Overworld";
            case "world_nether" -> "Nether";
            case "world_the_end" -> "End";
            default -> worldName;
        };
    }

}