package me.passivepicasso.infinitedungeon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InfiniteDungeon extends JavaPlugin {

    private InfinitePlayerListener playerListener;
    private World infiniteDungeon;

    public World getDungeonWorld() {
        return infiniteDungeon;
    }

    public void onDisable() {
    }

    public void onEnable() {
        infiniteDungeon = getServer().createWorld("InfiniteDungeon", World.Environment.NORMAL, new IDChunkGenerator());


        playerListener = new InfinitePlayerListener(infiniteDungeon);

        registerEvents();
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        try {
            pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        for (int cx = 0; cx < 16; cx++) {
            for (int cz = 0; cz < 16; cz++) {
                for (int y = 0; y < 128; y++) {
                    //TODO modify second check to 127 to create the ceiling
                    if (y == 0 /*|| y == 0*/) {
                        IDChunkGenerator.ChunkGenArray[(cx * 16 + cz) * 128 + y] = (byte) Material.BEDROCK.getId();
                    } else {
                        IDChunkGenerator.ChunkGenArray[(cx * 16 + cz) * 128 + y] = (byte) Material.STONE.getId();
                    }
                }
            }
        }
    }
}
