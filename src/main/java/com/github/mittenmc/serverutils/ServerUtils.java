package com.github.mittenmc.serverutils;

import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerNameCache(), this);
    }

}
