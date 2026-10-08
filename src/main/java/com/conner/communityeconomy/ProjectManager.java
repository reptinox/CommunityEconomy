package com.conner.communityeconomy;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ProjectManager {

    private final CommunityEconomy plugin;
    private final List<Project> projects = new ArrayList<>();
    private final File projectsFile;
    private final YamlConfiguration projectsConfig;

    public ProjectManager(CommunityEconomy plugin) {
        this.plugin = plugin;
        projectsFile = new File(plugin.getDataFolder(), "projects.yml");
        projectsConfig = YamlConfiguration.loadConfiguration(projectsFile);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public Project getProject(String id) {

        for (Project project : projects) {
            if (project.getId().equalsIgnoreCase(id)) {
                return project;
            }
        }

        return null;
    }

    // Save current state of the project
    public void saveProjects() {

        for (Project project : projects) {
            String path = "projects." + project.getId();
            projectsConfig.set(path + ".name", project.getName());
            projectsConfig.set(path + ".reward", project.getReward());
            projectsConfig.set(path + ".displayItem", project.getDisplayItem().name());
            projectsConfig.set(path + ".completed", project.isCompleted());
            projectsConfig.set(path + ".rewardClaimed", project.isRewardClaimed());

            if (project.hasLocation()) {
                projectsConfig.set(path + ".location.world", project.getWorld());
                projectsConfig.set(path + ".location.x", project.getX());
                projectsConfig.set(path + ".location.y", project.getY());
                projectsConfig.set(path + ".location.z", project.getZ());
            }
        }

        try {
            projectsConfig.save(projectsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save projects.yml!", e);
        }
    }

    // Create project for menu
    public void loadProjects() {
        projects.clear();

        if (!projectsConfig.contains("projects")) {
            return;
        }

        for (String id : Objects.requireNonNull(projectsConfig.getConfigurationSection("projects")).getKeys(false)) {
            String path = "projects." + id;
            String name = projectsConfig.getString(path + ".name");
            int reward = projectsConfig.getInt(path + ".reward");
            String materialName = projectsConfig.getString(path + ".displayItem", "PAPER");
            Material displayItem = Material.matchMaterial(materialName);

            if (displayItem == null) {
                displayItem = Material.PAPER;
            }

            Project project = new Project(id, name, reward, displayItem);
            project.setCompleted(projectsConfig.getBoolean(path + ".completed", false));
            project.setRewardClaimed(projectsConfig.getBoolean(path + ".rewardClaimed", false));

            if (projectsConfig.contains(path + ".location")) {
                String world = projectsConfig.getString(path + ".location.world");
                int x = projectsConfig.getInt(path + ".location.x");
                int y = projectsConfig.getInt(path + ".location.y");
                int z = projectsConfig.getInt(path + ".location.z");
                project.setLocation(world, x, y, z);
            }

            projects.add(project);
        }
    }

    public void removeProject(Project project) {
        projects.remove(project);
        projectsConfig.set("projects." + project.getId(), null);
        saveProjects();
    }

}