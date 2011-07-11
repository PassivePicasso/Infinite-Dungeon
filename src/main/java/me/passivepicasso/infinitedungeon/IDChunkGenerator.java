package me.passivepicasso.infinitedungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class IDChunkGenerator extends ChunkGenerator {

    public static final byte[] ChunkGenArray = new byte[32768];
    public static Location fixedSpawnLocation;
    private static List<BlockPopulator> defaultPopulators = Arrays.asList((BlockPopulator) new RoomPopulator());

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return ChunkGenArray.clone();
    }

    public Location getFixedSpawnLocation(World world) {
        return getFixedSpawnLocation(world, new Random());
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        if (fixedSpawnLocation == null) {
            fixedSpawnLocation = new Location(world, 0, 0, 0);
        }
        return fixedSpawnLocation;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return defaultPopulators;
    }
}
