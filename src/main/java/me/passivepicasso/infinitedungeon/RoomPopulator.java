package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BlockMatrixNode;
import me.passivepicasso.util.Box;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.*;


public class RoomPopulator extends BlockPopulator {

    private static final int COORIDORE_CHANCE = 50;
    private static final int ROOM_CHANCE = 30;
    private static final int DOUBLE_HEIGHT_CHANCE = 10;

    private static HashMap<Box, BlockMatrixNode> Rooms = new HashMap<Box, BlockMatrixNode>();

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (random.nextInt(100) < ROOM_CHANCE) {
            int center_x = 0;
            if (source.getX() >= 0) {
                center_x = 7 + (source.getX() * 16) + (4 - random.nextInt(8));
            } else {
                center_x = (source.getX() * 16) - 7 + (4 - random.nextInt(8));
            }

            int center_z = 0;
            if (source.getZ() >= 0) {
                center_z = 7 + (source.getZ() * 16) + (4 - random.nextInt(8));
            } else {
                center_z = (source.getZ() * 16) - 7 + (4 - random.nextInt(8));
            }
            int level = 1;
            int y_base = 127 - ((4 * level) + 1);
            int y_boost = 0;
            if (random.nextInt(100) < DOUBLE_HEIGHT_CHANCE) {
                y_boost = 4;
                y_base -= 4;
            }

            int halfWidth = random.nextInt(8) + 3;
            int halfLength = random.nextInt(8) + 3;
            HashMap<Block, BlockMatrixNode> roomShape = new HashMap<Block, BlockMatrixNode>();
            BlockMatrixNode thisRoomMatrix = new BlockMatrixNode(world.getBlockAt(center_x, y_base, center_z), roomShape);
            Box thisRoom = new Box(center_x, y_base, center_z, 4 + y_boost, 1 + (halfWidth * 2), 1 + (halfLength * 2));
            RoomPopulator.Rooms.put(thisRoom, thisRoomMatrix);

            for (int x = (center_x - halfWidth); x <= (center_x + halfWidth); x++) {
                for (int z = (center_z - halfLength); z <= (center_z + halfLength); z++) {
                    for (int y = y_base + 1; y <= y_base + 4 + y_boost; y++) {
                        new BlockMatrixNode(world.getBlockAt(x, y, z), roomShape);
                    }
                }
            }

            for (Box room : new ArrayList<Box>(RoomPopulator.Rooms.keySet())) {
                if (!thisRoom.equals(room) && thisRoom.intersects(room) && RoomPopulator.Rooms.containsKey(room)) {
                    thisRoomMatrix.union(RoomPopulator.Rooms.remove(room));
                }
            }

            for (Box room : thisRoom.getRoomParts()) {
                for (Box other : thisRoom.getRoomParts()) {
                    if (room.getMinY() < other.getMinY()) {
                        if (room.getMinZ() < other.getMaxZ()) {
                            BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
                            if (highRoom == null) {
                                continue;
                            }
                            highRoom.setFilter(EnumSet.of(Material.AIR));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getDown() != null) {
                                    highRoom = highRoom.getDown();
                                }
                            }
                            highRoom.setFilter(EnumSet.of(Material.STONE));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getWest() != null) {
                                    highRoom = highRoom.getEast();
                                }
                            }
                            highRoom = highRoom.getDown();
                            while (Material.AIR.equals(highRoom.getBlock().getType())) {
                                highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                highRoom.getBlock().setData((byte) 0x3);
                                BlockMatrixNode getNorth = highRoom;
                                BlockMatrixNode getSouth = highRoom;
                                while (getNorth.getBlock().getX() <= other.getMaxX()) {
                                    getNorth = getNorth.getNorth();
                                    getNorth.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getNorth.getBlock().setData((byte) 0x3);
                                }
                                while (getSouth.getBlock().getX() >= other.getMinX()) {
                                    getSouth = getSouth.getSouth();
                                    getSouth.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getSouth.getBlock().setData((byte) 0x3);
                                }
                                highRoom = highRoom.getEast().getDown();
                            }
                        } else if (room.getMaxZ() > other.getMinZ()) {
                            BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
                            highRoom.setFilter(EnumSet.of(Material.AIR));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getDown() != null) {
                                    highRoom = highRoom.getDown();
                                }
                            }
                            highRoom.setFilter(EnumSet.of(Material.STONE));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getEast() != null) {
                                    highRoom = highRoom.getWest();
                                }
                            }
                            highRoom = highRoom.getDown();
                            while (Material.AIR.equals(highRoom.getBlock().getType())) {
                                highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                highRoom.getBlock().setData((byte) 0x3);
                                BlockMatrixNode getNorth = highRoom;
                                BlockMatrixNode getSouth = highRoom;
                                while (getNorth.getBlock().getX() <= other.getMaxX()) {
                                    getNorth = getNorth.getNorth();
                                    getNorth.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getNorth.getBlock().setData((byte) 0x3);
                                }
                                while (getSouth.getBlock().getX() >= other.getMinX()) {
                                    getSouth = getSouth.getSouth();
                                    getSouth.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getSouth.getBlock().setData((byte) 0x3);
                                }
                                highRoom = highRoom.getWest().getDown();
                            }
                        } else if (room.getMaxX() > other.getMinX()) {
                            BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
                            highRoom.setFilter(EnumSet.of(Material.AIR));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getDown() != null) {
                                    highRoom = highRoom.getDown();
                                }
                            }
                            highRoom.setFilter(EnumSet.of(Material.STONE));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getNorth() != null) {
                                    highRoom = highRoom.getNorth();
                                }
                            }
                            highRoom = highRoom.getDown();
                            while (Material.AIR.equals(highRoom.getBlock().getType())) {
                                highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                highRoom.getBlock().setData((byte) 0x3);
                                BlockMatrixNode getWest = highRoom;
                                BlockMatrixNode getEast = highRoom;
                                while (getWest.getBlock().getZ() <= other.getMaxZ()) {
                                    getWest = getWest.getWest();
                                    getWest.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getWest.getBlock().setData((byte) 0x3);
                                }
                                while (getEast.getBlock().getZ() >= other.getMinZ()) {
                                    getEast = getEast.getEast();
                                    getEast.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getEast.getBlock().setData((byte) 0x3);
                                }
                                highRoom = highRoom.getNorth().getDown();
                            }
                        } else if (room.getMinX() < other.getMaxX()) {
                            BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
                            highRoom.setFilter(EnumSet.of(Material.AIR));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getDown() != null) {
                                    highRoom = highRoom.getDown();
                                }
                            }
                            highRoom.setFilter(EnumSet.of(Material.STONE));
                            while (highRoom.hasFilteredDown()) {
                                if (highRoom.getSouth() != null) {
                                    highRoom = highRoom.getSouth();
                                }
                            }
                            highRoom = highRoom.getDown();
                            while (Material.AIR.equals(highRoom.getBlock().getType())) {
                                highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                highRoom.getBlock().setData((byte) 0x3);
                                BlockMatrixNode getWest = highRoom;
                                BlockMatrixNode getEast = highRoom;
                                while (getWest.getBlock().getX() <= other.getMaxX()) {
                                    getWest = getWest.getWest();
                                    getWest.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getWest.getBlock().setData((byte) 0x3);
                                }
                                while (getEast.getBlock().getX() >= other.getMinX()) {
                                    getEast = getEast.getEast();
                                    getEast.getBlock().setType(Material.COBBLESTONE_STAIRS);
                                    getEast.getBlock().setData((byte) 0x3);
                                }
                                highRoom = highRoom.getSouth().getDown();
                            }
                        }
                    }
                }
            }

//            Block roomCenter = world.getBlockAt(center_x, y_base + 1, center_z);
//            Block northWall = roomCenter.getFace(BlockFace.NORTH);
//            while (northWall.getType().equals(Material.AIR)) {
//                northWall = northWall.getFace(BlockFace.NORTH);
//            }
//            Block eastWall = roomCenter.getFace(BlockFace.EAST);
//            while (eastWall.getType().equals(Material.AIR)) {
//                eastWall = eastWall.getFace(BlockFace.EAST);
//            }
//            Block southWall = roomCenter.getFace(BlockFace.SOUTH);
//            while (southWall.getType().equals(Material.AIR)) {
//                southWall = southWall.getFace(BlockFace.SOUTH);
//            }
//            Block westWall = roomCenter.getFace(BlockFace.WEST);
//            while (westWall.getType().equals(Material.AIR)) {
//                westWall = westWall.getFace(BlockFace.WEST);
//            }
//            huntingCooridor(northWall, random);
//            huntingCooridor(eastWall, random);
//            huntingCooridor(southWall, random);
//            huntingCooridor(westWall, random);
        }
    }


    public Box getRandomHall(Box box, Random random) {
        int direction = random.nextInt(4), modX = 0, modY = 0, modZ = 0, width = 0, height = 0, length = 0;
        int run = 11;
        switch (direction) {
            case 0:
                modX = box.getWidth() + run / 2;
                length = random.nextInt(3) + 1;
                width = run;
                break;
            case 1:
                modZ = box.getLength() + run / 2;
                width = random.nextInt(3) + 1;
                length = run;
                break;
            case 2:
                modX = -box.getWidth() - run / 2;
                length = random.nextInt(3) + 1;
                width = run;
                break;
            case 3:
                modZ = -box.getLength() - run / 2;
                width = random.nextInt(3) + 1;
                length = run;
                break;
        }
        return new Box(box.getX() + modX, box.getY(), box.getZ(), random.nextInt(2) + 2, width, length);
    }

    private void addToBlockMatrix() {

    }

    //the passed block should be at the desired floor level, the path of blocks above this will be hollowed out as well
    private void huntingCooridor(Block block, Random random) {
        if (random.nextInt(100) < COORIDORE_CHANCE) {
            for (BlockFace face : EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH)) {
                if (block.getFace(face).getType().equals(Material.STONE)) {
                    Block topBlock = block.getFace(BlockFace.UP);
                    while (true) {
                        block.getFace(face).setType(Material.AIR);
                        topBlock.getFace(face).setType(Material.AIR);
                        switch (face) {
                            case NORTH:
                            case SOUTH:
                                if (scanDirection(BlockFace.EAST, block.getFace(BlockFace.EAST), Material.AIR, 20)) {
                                    while (!block.getFace(BlockFace.EAST).getType().equals(Material.AIR)) {
                                        block = block.getFace(BlockFace.EAST);
                                        block.setType(Material.AIR);
                                    }
                                    return;
                                }
                                if (scanDirection(BlockFace.WEST, block.getFace(BlockFace.WEST), Material.AIR, 20)) {
                                    while (!block.getFace(BlockFace.WEST).getType().equals(Material.AIR)) {
                                        block = block.getFace(BlockFace.WEST);
                                        block.setType(Material.AIR);
                                    }
                                    return;
                                }
                                break;
                            case EAST:
                            case WEST:
                                if (scanDirection(BlockFace.NORTH, block.getFace(BlockFace.NORTH), Material.AIR, 20)) {
                                    while (!block.getFace(BlockFace.NORTH).getType().equals(Material.AIR)) {
                                        block = block.getFace(BlockFace.NORTH);
                                        block.setType(Material.AIR);
                                    }
                                    return;
                                }
                                if (scanDirection(BlockFace.SOUTH, block.getFace(BlockFace.SOUTH), Material.AIR, 20)) {
                                    while (!block.getFace(BlockFace.SOUTH).getType().equals(Material.AIR)) {
                                        block = block.getFace(BlockFace.SOUTH);
                                        block.setType(Material.AIR);
                                    }
                                    return;
                                }
                                break;
                        }
                        block = block.getFace(face);
                        topBlock = topBlock.getFace(face);
                    }
                }
            }
        }
    }

    private boolean scanDirection(BlockFace face, Block block, Material targetType, int maxDistance) {
        while (!block.getFace(face).getType().equals(targetType)) {
            block = block.getFace(face);
            maxDistance--;
            if (maxDistance <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Always pass true for firstIteration except from within firstIteration.
     *
     * @param node
     * @param random
     * @param direction
     * @param firstIteration
     */
    private void randomCooridoor(BlockMatrixNode node, Random random, int direction, boolean firstIteration) {
        node.setFilter(new HashSet<Material>(EnumSet.of(Material.AIR)));
        int dir = random.nextInt(4);
        switch (dir) {
            case 0:
                while (node.hasFilteredNorth()) {
                    node = node.getNorth();
                }
                node.setFilter(new HashSet<Material>(EnumSet.of(Material.STONE)));
                int maxDistanceN = random.nextInt(12) + 4;
                while (node.hasFilteredNorth()) {
                    node.getBlock().setType(Material.AIR);
                    node = node.getNorth();
                    if (maxDistanceN == 0) {
                        break;
                    }
                    maxDistanceN--;
                }
                if (firstIteration) {
                    randomCooridoor(node, random, random.nextInt(4), false);
                }
                break;
            case 1:
                while (node.hasFilteredEast()) {
                    node = node.getEast();
                }
                node.setFilter(new HashSet<Material>(EnumSet.of(Material.STONE)));
                int maxDistanceE = random.nextInt(12) + 4;
                while (node.hasFilteredEast()) {
                    node.getBlock().setType(Material.AIR);
                    node = node.getEast();
                    if (maxDistanceE == 0) {
                        break;
                    }
                    maxDistanceE--;
                }
                if (firstIteration) {
                    randomCooridoor(node, random, random.nextInt(4), false);
                }
                break;
            case 2:
                while (node.hasFilteredSouth()) {
                    node = node.getSouth();
                }
                node.setFilter(new HashSet<Material>(EnumSet.of(Material.STONE)));
                int maxDistanceS = random.nextInt(12) + 4;
                while (node.hasFilteredSouth()) {
                    node.getBlock().setType(Material.AIR);
                    node = node.getSouth();
                    if (maxDistanceS == 0) {
                        break;
                    }
                    maxDistanceS--;
                }
                if (firstIteration) {
                    randomCooridoor(node, random, random.nextInt(4), false);
                }
                break;
            case 3:
                while (node.hasFilteredWest()) {
                    node = node.getWest();
                }
                node.setFilter(new HashSet<Material>(EnumSet.of(Material.STONE)));
                int maxDistanceW = random.nextInt(12) + 4;
                while (node.hasFilteredWest()) {
                    node.getBlock().setType(Material.AIR);
                    node = node.getWest();
                    if (maxDistanceW == 0) {
                        break;
                    }
                    maxDistanceW--;
                }
                if (firstIteration) {
                    randomCooridoor(node, random, random.nextInt(4), false);
                }
                break;
        }
    }
}
