package com.conner.communityeconomy;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class CommunityEconomy extends JavaPlugin {

    private ProjectManager projectManager;

    // For start-up of the plugin
    @Override
    public void onEnable() {

        // Create managers for projects and talents
        projectManager = new ProjectManager(this);
        TalentManager talentManager = new TalentManager(this);

        // Load saved projects
        projectManager.loadProjects();

        // Create command handlers
        Commands commands = new Commands(this, projectManager, talentManager);

        // Register commands
        Objects.requireNonNull(getCommand("project")).setExecutor(commands);
        Objects.requireNonNull(getCommand("project")).setTabCompleter(commands);
        Objects.requireNonNull(getCommand("projects")).setExecutor(commands);
        Objects.requireNonNull(getCommand("talents")).setExecutor(commands);
        Objects.requireNonNull(getCommand("talents")).setTabCompleter(commands);

        // Register listeners
        getServer().getPluginManager().registerEvents(new Listeners(projectManager, talentManager), this);

        getLogger().info("Community Economy enabled!");
    }

    // For shutdown of the plugin
    @Override
    public void onDisable() {

        if (projectManager != null) {
            projectManager.saveProjects();
        }

        getLogger().info("Community Economy disabled!");
    }

}