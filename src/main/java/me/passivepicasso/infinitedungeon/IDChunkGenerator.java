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

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return ChunkGenArray.clone();
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        if (RoomPopulator.homeRoom != null) {
            System.out.print("found homeRoom");
            return new Location(world, RoomPopulator.homeRoom.getMinX(), RoomPopulator.homeRoom.getMinY(), RoomPopulator.homeRoom.getMinZ());
        } else {
            return new Location(world, 0, 0, 0);
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator) new RoomPopulator());
    }
}
