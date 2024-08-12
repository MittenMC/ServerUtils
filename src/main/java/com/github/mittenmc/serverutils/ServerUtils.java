package com.github.mittenmc.serverutils;

import com.github.mittenmc.serverutils.command.ExecCommand;
import com.github.mittenmc.serverutils.command.GenericCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class ServerUtils extends JavaPlugin {

    @Getter
    private static ServerUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new PlayerNameCache(), this);

        new GenericCommandManager(getCommand("serverutils"));
        Objects.requireNonNull(getCommand("exec")).setExecutor(new ExecCommand());
    }

}
