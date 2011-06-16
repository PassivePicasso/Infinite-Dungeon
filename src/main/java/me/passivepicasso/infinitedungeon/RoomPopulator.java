package me.passivepicasso.infinitedungeon;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Tobias
 * Date: 6/12/11
 * Time: 7:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoomPopulator extends BlockPopulator {


    @Override
    public void populate(World world, Random random, Chunk source) {
        random.setSeed(random.nextLong());
        int center_x = random.nextInt(16) + (16 * source.getX());
        int center_z = random.nextInt(16) + (16 * source.getZ());
        int level = 1;
        int y_base = 127 - ((4 * level) + 1);

        Vector startingPoint = new BlockVector(center_x, y_base, center_z);


        int halfWidth = random.nextInt(3) + 3;
        int halfLength = random.nextInt(3) + 3;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfLength; z <= halfLength; z++) {
                for (int y = 1; y < 5; y++) {
                    Vector target = startingPoint.clone().add(new Vector(x, y, z));
                    world.getBlockAt(target.toLocation(world)).setType(Material.AIR);
                }
            }
        }
    }
}
