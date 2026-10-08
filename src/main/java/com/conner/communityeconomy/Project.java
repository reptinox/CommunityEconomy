package com.conner.communityeconomy;

import org.bukkit.Material;

public class Project {

    private final String id;
    private final String name;
    private final int reward;
    private String world;
    private int x;
    private int y;
    private int z;
    private boolean hasLocation;
    private boolean completed;
    private boolean rewardClaimed;
    private final Material displayItem;

    public Project(String id, String name, int reward, Material displayItem) {
        this.id = id;
        this.name = name;
        this.reward = reward;
        this.displayItem = displayItem;
        this.completed = false;
        this.hasLocation = false;
        this.rewardClaimed = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReward() {
        return reward;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.hasLocation = true;
    }

    public boolean hasLocation() {
        return hasLocation;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isRewardClaimed() {
        return rewardClaimed;
    }

    public void setRewardClaimed(boolean rewardClaimed) {
        this.rewardClaimed = rewardClaimed;
    }

    public Material getDisplayItem() {
        return displayItem;
    }
}