package com.conner.communityeconomy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Listeners implements Listener {

    private final ProjectManager projectManager;
    private final TalentManager talentManager;

    public Listeners(
            ProjectManager projectManager,
            TalentManager talentManager
    ) {
        this.projectManager = projectManager;
        this.talentManager = talentManager;
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {

        AnvilInventory anvil = event.getInventory();
        ItemStack item = anvil.getFirstItem();

        if (item == null || item.getType() != Material.GOLD_NUGGET) {
            return;
        }

        String renameText = event.getView().getRenameText();

        if (renameText == null) {
            return;
        }

        if (renameText.equalsIgnoreCase("Talent")
                && !talentManager.isTalent(item)) {

            event.setResult(null);
            anvil.setFirstItem(null);

            Player player =
                    (Player) event.getView().getPlayer();

            player.sendMessage(
                    "§6Nice try. §7Counterfeit Talents are not permitted."
            );

            player.sendMessage(
                    "§e\"You shall not steal, nor deal falsely, nor lie to one another.\""
            );

            player.sendMessage(
                    "§7— Leviticus 19:11"
            );
        }
    }

    @EventHandler
    public void onProjectMenuClick(InventoryClickEvent event) {

        String title = event.getView().getTitle();

        // Only handle our menus
        if (!title.startsWith("Community Projects")
                && !title.startsWith("Complete:")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();

        List<Project> projects =
                projectManager.getProjects();

        // --------------------
        // MAIN MENU
        // --------------------
        if (title.equals("Community Projects")) {

            // Tasks
            if (slot == 11) {
                ProjectMenu.openTasks(
                        player,
                        projects,
                        0
                );

                return;
            }

            // Completed
            if (slot == 15) {
                ProjectMenu.openCompleted(
                        player,
                        projects,
                        0
                );

                return;
            }

            return;
        }

        // --------------------
        // TASKS PAGE
        // --------------------
        if (title.startsWith(
                "Community Projects - Tasks - "
        )) {

            String pageText = title.substring(
                    "Community Projects - Tasks - ".length()
            );

            int page;

            try {
                page = Integer.parseInt(pageText) - 1;
            } catch (NumberFormatException e) {
                return;
            }

            // BACK
            if (slot == 45) {
                ProjectMenu.openMain(player);
                return;
            }

            // PREVIOUS PAGE
            if (slot == 48 && page > 0) {

                ProjectMenu.openTasks(
                        player,
                        projects,
                        page - 1
                );

                return;
            }

            // NEXT PAGE
            if (slot == 50) {

                int incompleteCount = 0;

                for (Project project : projects) {
                    if (!project.isCompleted()) {
                        incompleteCount++;
                    }
                }

                int totalPages = Math.max(
                        1,
                        (int) Math.ceil(
                                incompleteCount / 28.0
                        )
                );

                if (page + 1 < totalPages) {

                    ProjectMenu.openTasks(
                            player,
                            projects,
                            page + 1
                    );
                }

                return;
            }

            // All slots where projects are allowed
            List<Integer> projectSlots =
                    new ArrayList<>();

            for (int row = 1; row <= 4; row++) {

                for (int column = 1;
                     column <= 7;
                     column++) {

                    projectSlots.add(
                            row * 9 + column
                    );
                }
            }

            // Did the player click one of those slots?
            int clickedIndex =
                    projectSlots.indexOf(slot);

            if (clickedIndex == -1) {
                return;
            }

            // Get only incomplete projects
            List<Project> incompleteProjects =
                    new ArrayList<>();

            for (Project project : projects) {

                if (!project.isCompleted()) {
                    incompleteProjects.add(project);
                }
            }

            // Figure out which project this slot represents
            int projectsPerPage =
                    projectSlots.size();

            int projectIndex =
                    (page * projectsPerPage)
                            + clickedIndex;

            if (projectIndex
                    >= incompleteProjects.size()) {

                return;
            }

            Project selectedProject =
                    incompleteProjects.get(
                            projectIndex
                    );

            ProjectMenu.openConfirmation(
                    player,
                    selectedProject
            );

            return;
        }

        // --------------------
        // COMPLETED PAGE
        // --------------------
        if (title.startsWith(
                "Community Projects - Completed - "
        )) {

            String pageText = title.substring(
                    "Community Projects - Completed - ".length()
            );

            int page;

            try {
                page = Integer.parseInt(pageText) - 1;
            } catch (NumberFormatException e) {
                return;
            }

            // BACK
            if (slot == 45) {
                ProjectMenu.openMain(player);
                return;
            }

            // PREVIOUS PAGE
            if (slot == 48 && page > 0) {

                ProjectMenu.openCompleted(
                        player,
                        projects,
                        page - 1
                );

                return;
            }

            // NEXT PAGE
            if (slot == 50) {

                int completedCount = 0;

                for (Project project : projects) {

                    if (project.isCompleted()) {
                        completedCount++;
                    }
                }

                int totalPages = Math.max(
                        1,
                        (int) Math.ceil(
                                completedCount / 28.0
                        )
                );

                if (page + 1 < totalPages) {

                    ProjectMenu.openCompleted(
                            player,
                            projects,
                            page + 1
                    );
                }

                return;
            }

            return;
        }

        // --------------------
        // PROJECT CONFIRMATION
        // --------------------
        if (title.startsWith("Complete: ")) {

            String projectName =
                    title.substring(
                            "Complete: ".length()
                    );

            Project selectedProject = null;

            for (Project project : projects) {

                if (project.getName()
                        .equals(projectName)) {

                    selectedProject = project;
                    break;
                }
            }

            if (selectedProject == null) {

                player.closeInventory();

                player.sendMessage(
                        "§cCould not find that project."
                );

                return;
            }

            // YES
            if (slot == 11) {

                selectedProject.setCompleted(true);

                projectManager.saveProjects();

                player.closeInventory();

                player.sendMessage(
                        "§a"
                                + selectedProject.getName()
                                + " completed!"
                );

                player.sendMessage(
                        "§eStand at the project and type:"
                );

                player.sendMessage(
                        "§6/project location "
                                + selectedProject.getId()
                );

                return;
            }

            // CANCEL
            if (slot == 15) {

                ProjectMenu.openTasks(
                        player,
                        projects,
                        0
                );
            }

            return;
        }
    }
}