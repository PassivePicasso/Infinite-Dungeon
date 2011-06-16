package me.passivepicasso.infinitedungeon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InfiniteDungeon extends JavaPlugin {

    private static byte[] chunkGenArray = new byte[32768];
    private static byte[] emptyChunk = new byte[32768];
    private InfinitePlayerListener playerListener;
    private World infiniteDungeon = null;

    public World getDungeonWorld() {
        return infiniteDungeon;
    }

    public void onDisable() {
    }

    public void onEnable() {
        infiniteDungeon = getServer().createWorld("InfiniteDungeon", World.Environment.NORMAL, new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                if (x == 0 && z == 0) {
                    return emptyChunk;
                }
                return chunkGenArray;
            }

            @Override
            public boolean canSpawn(World world, int x, int z) {
                return x == 0 && z == 0;
            }

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 5, 5, 5);
            }

            private ArrayList<BlockPopulator> populators = null;

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                if(populators == null){
                    populators = new ArrayList<BlockPopulator>();
                    populators.add(new RoomPopulator());
                }
                return populators;
            }
        });


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
                    if (y == 0) {
                        emptyChunk[(cx * 16 + cz) * 128 + y] = (byte) Material.BEDROCK.getId();
                    } else {
                        emptyChunk[(cx * 16 + cz) * 128 + y] = (byte) Material.AIR.getId();
                    }
                    if (y == 0 || y == 127) {
                        chunkGenArray[(cx * 16 + cz) * 128 + y] = (byte) Material.BEDROCK.getId();
                    } else {
                        chunkGenArray[(cx * 16 + cz) * 128 + y] = (byte) Material.STONE.getId();
                    }
                }
            }
        }
    }
}
