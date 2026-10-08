package com.conner.communityeconomy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final CommunityEconomy plugin;
    private final ProjectManager projectManager;
    private final TalentManager talentManager;

    public Commands(CommunityEconomy plugin, ProjectManager projectManager, TalentManager talentManager) {
        this.plugin = plugin;
        this.projectManager = projectManager;
        this.talentManager = talentManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, Command command, @NonNull String label, String @NonNull [] args) {

        // Project
        if (command.getName().equalsIgnoreCase("project")) {

            // Allow input from only in game players
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            // Add project
            if (args.length >= 5 && args[0].equalsIgnoreCase("add")) {

                // Verify player is an operator
                if (!player.isOp()) {
                    player.sendMessage("§cYou do not have permission to do that.");
                    return true;
                }

                String id = args[1].toLowerCase();

                if (projectManager.getProject(id) != null) {
                    player.sendMessage("§cA project with that ID already exists.");
                    return true;
                }

                int reward;

                // Verify correct input
                try {
                    reward = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cReward must be a number.");
                    return true;
                }

                if (reward <= 0) {
                    player.sendMessage("§cReward must be greater than 0.");
                    return true;
                }

                Material displayItem = Material.matchMaterial(args[3].toUpperCase());

                if (displayItem == null || !displayItem.isItem()) {
                    player.sendMessage("§cThat is not a valid Minecraft item.");
                    return true;
                }

                StringBuilder nameBuilder = new StringBuilder();

                for (int i = 4; i < args.length; i++) {

                    if (i > 4) {
                        nameBuilder.append(" ");
                    }

                    nameBuilder.append(args[i]);
                }

                // Good project
                String name = nameBuilder.toString();
                Project project = new Project(id, name, reward, displayItem);
                projectManager.getProjects().add(project);
                projectManager.saveProjects();
                player.sendMessage("§aProject added!");
                player.sendMessage("§7" + name + " - " + reward + " Talents");

                return true;
            }

            // Remove project
            if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {

                // Verify player is an operator
                if (!player.isOp()) {
                    player.sendMessage("§cYou do not have permission to do that.");
                    return true;
                }

                Project project = projectManager.getProject(args[1]);

                if (project == null) {
                    player.sendMessage("§cThat project does not exist.");
                    return true;
                }

                projectManager.removeProject(project);
                player.sendMessage("§aRemoved project: §7" + project.getName());

                return true;
            }

            // Marking project location
            if (args.length != 2 || !args[0].equalsIgnoreCase("location")) {
                player.sendMessage("§cUsage:");
                player.sendMessage("§7/project location <project>");

                if (player.isOp()) {
                    player.sendMessage("§7/project add <id> <reward> <item> <name>");
                    player.sendMessage("§7/project remove <id>");
                }

                return true;
            }

            Project project = projectManager.getProject(args[1]);

            // Verify correct input
            if (project == null) {
                player.sendMessage("§cThat project does not exist.");
                return true;
            }

            if (!project.isCompleted()) {
                player.sendMessage("§cThat project has not been completed yet.");
                return true;
            }

            // Good project - save location
            // Give reward if it's the first time
            project.setLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            boolean shouldGiveReward = !project.isRewardClaimed();
            player.sendMessage("§aLocation saved for " + project.getName() + "!");
            player.sendMessage("§7X: " + project.getX() + ", Y: " + project.getY() + ", Z: " + project.getZ());

            if (shouldGiveReward) {
                talentManager.giveTalents(player, project.getReward());
                project.setRewardClaimed(true);
                player.sendMessage("§6You received " + project.getReward() + " Talents!");
            }

            projectManager.saveProjects();

            return true;
        }

        // Projects
        if (command.getName().equalsIgnoreCase("projects")) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            ProjectMenu.openMain(player);

            return true;
        }

        // Talents
        if (command.getName().equalsIgnoreCase("talents")) {

            // Verify player is an operator
            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage("§cUsage: /talents <amount> <player>");
                return true;
            }

            int amount;

            // Verify correct input
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cAmount must be a number.");
                return true;
            }

            if (amount <= 0) {
                sender.sendMessage("§cAmount must be greater than 0.");
                return true;
            }

            Player target = plugin.getServer().getPlayerExact(args[1]);

            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }

            // Good talents
            talentManager.giveTalents(target, amount);
            sender.sendMessage("§aGave §e" + amount + (amount == 1 ? " Talent" : " Talents") + " §ato §f" + target.getName() + "§a.");
            target.sendMessage("§6You received §e" + amount + (amount == 1 ? " Talent" : " Talents") + "§6.");

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, Command command, @NonNull String alias, String @NonNull [] args) {
        List<String> suggestions = new ArrayList<>();

        // /talents <amount> <player>
        if (command.getName().equalsIgnoreCase("talents")) {

            if (!sender.isOp()) {
                return suggestions;
            }

            if (args.length == 2) {
                String typed = args[1].toLowerCase();

                for (Player player : plugin.getServer().getOnlinePlayers()) {

                    if (player.getName().toLowerCase().startsWith(typed)) {
                        suggestions.add(player.getName());
                    }
                }
            }

            return suggestions;
        }

        if (!command.getName().equalsIgnoreCase("project")) {
            return suggestions;
        }

        // /project <tab>
        if (args.length == 1) {
            suggestions.add("location");

            if (sender.isOp()) {
                suggestions.add("add");
                suggestions.add("remove");
            }

            return suggestions;
        }

        // /project location <tab>
        if (args.length == 2 && args[0].equalsIgnoreCase("location")) {
            String typed = args[1].toLowerCase();

            for (Project project :
                    projectManager.getProjects()) {

                if (project.isCompleted() && project.getId().toLowerCase().startsWith(typed)) {
                    suggestions.add(project.getId());
                }
            }

            return suggestions;
        }

        // /project remove <tab>
        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.isOp()) {
            String typed = args[1].toLowerCase();

            for (Project project : projectManager.getProjects()) {

                if (project.getId().toLowerCase().startsWith(typed)) {
                    suggestions.add(project.getId());
                }
            }

            return suggestions;
        }

        // /project add <id> <reward> <item>
        if (args.length == 4 && args[0].equalsIgnoreCase("add") && sender.isOp()) {
            String typed = args[3].toUpperCase();

            for (Material material : Material.values()) {

                if (material.isItem() && material.name().startsWith(typed)) {
                    suggestions.add(material.name());
                }
            }

            return suggestions;
        }

        return suggestions;
    }
}