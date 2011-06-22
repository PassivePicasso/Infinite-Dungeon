package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BlockMatrixNode;
import me.passivepicasso.util.Box;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.*;


public class RoomPopulator extends BlockPopulator {

    private static final int COORIDORE_CHANCE = 50;
    private static final int ROOM_CHANCE = 30;
    private static final int DOUBLE_HEIGHT_CHANCE = 10;

    private static HashMap<Box, BlockMatrixNode> Rooms = new HashMap<Box, BlockMatrixNode>();
    private static HashMap<Chunk, Box> existingRooms = new HashMap<Chunk, Box>();

    @Override
    public void populate(World world, Random random, Chunk source) {
        int x, z, halfWidth = random.nextInt(8) + 3, halfLength = random.nextInt(8) + 3;

        if (Rooms.isEmpty()) {
            x = (source.getX() * 16);
            z = (source.getZ() * 16);
            int level = 1, y_base = 127 - ((4 * level) + 1), y_boost = 0;

            Box startRoom = new Box(x, 4, z, y_base, halfLength * 2, halfWidth * 2);

            BlockMatrixNode roomMatrix = new BlockMatrixNode(world.getBlockAt(x, y_base, z), new HashMap<Block, BlockMatrixNode>());
            RoomPopulator.Rooms.put(startRoom, roomMatrix);
            for (int bx = (x - halfWidth); bx <= (x + halfWidth); bx++) {
                for (int bz = (z - halfLength); bz <= (z + halfLength); bz++) {
                    for (int y = y_base + 1; y <= y_base + 4 + y_boost; y++) {
                        new BlockMatrixNode(world.getBlockAt(bx, y, bz), roomMatrix.getMatrixNodes());
                    }
                }
            }

            RoomPopulator.existingRooms.put(source, startRoom);
        } else if (existingRooms.containsKey(source)) {

        } else if (random.nextInt(100) < ROOM_CHANCE) {
            if (source.getX() >= 0) {
                x = (source.getX() * 16) + (4 - random.nextInt(8));
            } else {
                x = (source.getX() * 16) + (4 - random.nextInt(8));
            }

            if (source.getZ() >= 0) {
                z = 7 + (source.getZ() * 16) + (4 - random.nextInt(8));
            } else {

                z = (source.getZ() * 16) - 7 + (4 - random.nextInt(8));
            }
            int level = 1;
            int y_base = 127 - ((4 * level) + 1), y_boost = 0;

            if (random.nextInt(100) < DOUBLE_HEIGHT_CHANCE) {
                y_boost = 4;
                y_base -= 4;
            }

            halfWidth = random.nextInt(6) + 3;
            halfLength = random.nextInt(6) + 3;


            HashMap<Block, BlockMatrixNode> roomShape = new HashMap<Block, BlockMatrixNode>();
            BlockMatrixNode thisRoomMatrix = new BlockMatrixNode(world.getBlockAt(x, y_base, z), roomShape);
            Box thisRoom = new Box(x, y_base, z, 4 + y_boost, 1 + (halfWidth * 2), 1 + (halfLength * 2));
            RoomPopulator.Rooms.put(thisRoom, thisRoomMatrix);

            for (int bx = thisRoom.getMinX(); bx <= thisRoom.getMaxX(); bx++) {
                for (int bz = thisRoom.getMinZ(); bz <= thisRoom.getMaxZ(); bz++) {
                    for (int y = thisRoom.getMinY(); y <= thisRoom.getMaxY(); y++) {
                        new BlockMatrixNode(world.getBlockAt(bx, y, bz), roomShape);
                    }
                }
            }

            ArrayList<Box> neighbors = new ArrayList<Box>();
            Box north = null, east = null, south = null, west = null;
            Chunk northChunk, eastChunk, southChunk, westChunk;


            //Make random neighbors.

            //check and reference Neighbors
            if (world.isChunkLoaded(source.getX() - 1, source.getZ()) && RoomPopulator.existingRooms.containsKey(world.getChunkAt(source.getX() - 1, source.getZ()))) {
                northChunk = world.getChunkAt(source.getX() - 1, source.getZ());
                north = RoomPopulator.existingRooms.get(northChunk);
                neighbors.add(north);
            }
            if (world.isChunkLoaded(source.getX(), source.getZ() - 1) && RoomPopulator.existingRooms.containsKey(world.getChunkAt(source.getX(), source.getZ() - 1))) {
                eastChunk = world.getChunkAt(source.getX(), source.getZ() - 1);
                east = RoomPopulator.existingRooms.get(eastChunk);
                neighbors.add(east);
            }
            if (world.isChunkLoaded(source.getX() + 1, source.getZ()) && RoomPopulator.existingRooms.containsKey(world.getChunkAt(source.getX() + 1, source.getZ()))) {
                southChunk = world.getChunkAt(source.getX() + 1, source.getZ());
                south = RoomPopulator.existingRooms.get(southChunk);
                neighbors.add(south);
            }
            if (world.isChunkLoaded(source.getX(), source.getZ() + 1) && RoomPopulator.existingRooms.containsKey(world.getChunkAt(source.getX(), source.getZ() + 1))) {
                westChunk = world.getChunkAt(source.getX(), source.getZ() + 1);
                west = RoomPopulator.existingRooms.get(westChunk);
                neighbors.add(west);
            }

            if (!neighbors.isEmpty()) {
                ArrayList<Box> halls = new ArrayList<Box>();
                //North
                if (north != null && RoomPopulator.COORIDORE_CHANCE > random.nextInt(100)) {
                    int hallLength = thisRoom.getMinX() - north.getMaxX();
                    int hallOffset = random.nextInt(Math.min(thisRoom.getMaxZ(), north.getMaxZ()) - Math.max(thisRoom.getMinZ(), north.getMinZ()));
                    int hallWidth = 1 + random.nextInt((Math.max(thisRoom.getMaxZ(), north.getMaxZ()) - Math.min(thisRoom.getMinZ(), north.getMinZ())) - 1);
                    hallLength = hallLength == 0 ? 1 : hallLength;

                    Box hall = new Box(thisRoom.getMinX() - hallLength, thisRoom.getMinY(), thisRoom.getMinZ() + hallOffset, thisRoom.getMaxY() - thisRoom.getMinY(), hallWidth, hallLength);
                    halls.add(hall);
                }
                //East
                if (east != null && RoomPopulator.COORIDORE_CHANCE > random.nextInt(100)) {
                    int hallLength = (thisRoom.getMinZ() - east.getMaxZ());
                    int hallOffset = random.nextInt(Math.min(thisRoom.getMaxX(), east.getMaxX()) - Math.max(thisRoom.getMinX(), east.getMinX()));
                    int hallWidth = 1 + random.nextInt((Math.max(thisRoom.getMaxX(), east.getMaxX()) - Math.min(thisRoom.getMinX(), east.getMinX())) - 1);
                    hallLength = hallLength == 0 ? 1 : hallLength;

                    Box hall = new Box(thisRoom.getMinX() + hallOffset, thisRoom.getMinY(), thisRoom.getMinZ() - hallLength, thisRoom.getMaxY() - thisRoom.getMinY(), hallWidth, hallLength);
                    halls.add(hall);
                }
                //South
                if (south != null && RoomPopulator.COORIDORE_CHANCE > random.nextInt(100)) {
                    int hallLength = south.getMinX() - thisRoom.getMaxX();
                    int hallOffset = random.nextInt(Math.min(south.getMaxZ(), thisRoom.getMaxZ()) - Math.max(south.getMinZ(), thisRoom.getMinZ()));
                    int hallWidth = 1 + random.nextInt((Math.max(south.getMaxZ(), thisRoom.getMaxZ()) - Math.min(south.getMinZ(), thisRoom.getMinZ())) - 1);
                    hallLength = hallLength == 0 ? 1 : hallLength;

                    Box hall = new Box(south.getMinX() - hallLength, thisRoom.getMinY(), south.getMinZ() + hallOffset, thisRoom.getMaxY() - thisRoom.getMinY(), hallWidth, hallLength);
                    halls.add(hall);
                }

                //West
                if (west != null && RoomPopulator.COORIDORE_CHANCE > random.nextInt(100)) {
                    int hallLength = west.getMinZ() - thisRoom.getMaxZ();
                    int hallOffset = random.nextInt(Math.min(west.getMaxX(), thisRoom.getMaxX()) - Math.max(west.getMinX(), thisRoom.getMinX()));
                    int hallWidth = 1 + random.nextInt((Math.max(west.getMaxX(), thisRoom.getMaxX()) - Math.min(west.getMinX(), thisRoom.getMinX())) - 1);
                    hallLength = hallLength == 0 ? 1 : hallLength;

                    Box hall = new Box(west.getMinX() - hallLength, thisRoom.getMinY(), west.getMinZ() + hallOffset, thisRoom.getMaxY() - thisRoom.getMinY(), hallWidth, hallLength);
                    halls.add(hall);

                }

                for (Box hall : halls) {
                    roomShape = new HashMap<Block, BlockMatrixNode>();
                    for (int bx = hall.getMinX(); bx <= hall.getMaxX(); bx++) {
                        for (int bz = hall.getMinZ(); bz <= hall.getMaxZ(); bz++) {
                            for (int y = hall.getMinY(); y <= hall.getMaxY(); y++) {
                                Block block = world.getBlockAt(bx, y, bz);
                                block.setType(Material.AIR);
                                new BlockMatrixNode(block, roomShape);
                            }
                        }
                    }
                }
            } else {
                for (int i = random.nextInt(9); i < 10; i++) {
                    int sX = source.getX(), sZ = source.getZ();
                    List<Chunk> chunks = Arrays.asList((Chunk) world.getChunkAt(sX + 1, sZ), world.getChunkAt(sX - 1, sZ), world.getChunkAt(sX, sZ - 1), world.getChunkAt(sX, sZ + 1));
                }

            }
        }
    }
}

//    for(
//    Box room
//    :new ArrayList<Box>(RoomPopulator.Rooms.keySet()))
//
//    {
//        if (!thisRoom.equals(room) && thisRoom.intersects(room) && RoomPopulator.Rooms.containsKey(room)) {
//            thisRoomMatrix.union(RoomPopulator.Rooms.remove(room));
//        }
//    }
//
//    for(
//    Box room
//    :thisRoom.getRoomParts())
//
//    {
//        for (Box other : thisRoom.getRoomParts()) {
//            if (room.getMinY() < other.getMinY()) {
//                if (room.getMinZ() < other.getMaxZ()) {
//                    BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
//                    if (highRoom == null) {
//                        continue;
//                    }
//                    highRoom.setFilter(EnumSet.of(Material.AIR));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getDown() != null) {
//                            highRoom = highRoom.getDown();
//                        }
//                    }
//                    highRoom.setFilter(EnumSet.of(Material.STONE));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getWest() != null) {
//                            highRoom = highRoom.getEast();
//                        }
//                    }
//                    highRoom = highRoom.getDown();
//                    while (Material.AIR.equals(highRoom.getBlock().getType())) {
//                        highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                        highRoom.getBlock().setData((byte) 0x3);
//                        BlockMatrixNode getNorth = highRoom;
//                        BlockMatrixNode getSouth = highRoom;
//                        while (getNorth.getBlock().getX() <= other.getMaxX()) {
//                            getNorth = getNorth.getNorth();
//                            getNorth.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getNorth.getBlock().setData((byte) 0x3);
//                        }
//                        while (getSouth.getBlock().getX() >= other.getMinX()) {
//                            getSouth = getSouth.getSouth();
//                            getSouth.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getSouth.getBlock().setData((byte) 0x3);
//                        }
//                        highRoom = highRoom.getEast().getDown();
//                    }
//                } else if (room.getMaxZ() > other.getMinZ()) {
//                    BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
//                    highRoom.setFilter(EnumSet.of(Material.AIR));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getDown() != null) {
//                            highRoom = highRoom.getDown();
//                        }
//                    }
//                    highRoom.setFilter(EnumSet.of(Material.STONE));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getEast() != null) {
//                            highRoom = highRoom.getWest();
//                        }
//                    }
//                    highRoom = highRoom.getDown();
//                    while (Material.AIR.equals(highRoom.getBlock().getType())) {
//                        highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                        highRoom.getBlock().setData((byte) 0x3);
//                        BlockMatrixNode getNorth = highRoom;
//                        BlockMatrixNode getSouth = highRoom;
//                        while (getNorth.getBlock().getX() <= other.getMaxX()) {
//                            getNorth = getNorth.getNorth();
//                            getNorth.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getNorth.getBlock().setData((byte) 0x3);
//                        }
//                        while (getSouth.getBlock().getX() >= other.getMinX()) {
//                            getSouth = getSouth.getSouth();
//                            getSouth.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getSouth.getBlock().setData((byte) 0x3);
//                        }
//                        highRoom = highRoom.getWest().getDown();
//                    }
//                } else if (room.getMaxX() > other.getMinX()) {
//                    BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
//                    highRoom.setFilter(EnumSet.of(Material.AIR));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getDown() != null) {
//                            highRoom = highRoom.getDown();
//                        }
//                    }
//                    highRoom.setFilter(EnumSet.of(Material.STONE));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getNorth() != null) {
//                            highRoom = highRoom.getNorth();
//                        }
//                    }
//                    highRoom = highRoom.getDown();
//                    while (Material.AIR.equals(highRoom.getBlock().getType())) {
//                        highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                        highRoom.getBlock().setData((byte) 0x3);
//                        BlockMatrixNode getWest = highRoom;
//                        BlockMatrixNode getEast = highRoom;
//                        while (getWest.getBlock().getZ() <= other.getMaxZ()) {
//                            getWest = getWest.getWest();
//                            getWest.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getWest.getBlock().setData((byte) 0x3);
//                        }
//                        while (getEast.getBlock().getZ() >= other.getMinZ()) {
//                            getEast = getEast.getEast();
//                            getEast.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getEast.getBlock().setData((byte) 0x3);
//                        }
//                        highRoom = highRoom.getNorth().getDown();
//                    }
//                } else if (room.getMinX() < other.getMaxX()) {
//                    BlockMatrixNode highRoom = thisRoomMatrix.getMatrixNode(world.getBlockAt(other.getX(), other.getY(), other.getZ()));
//                    highRoom.setFilter(EnumSet.of(Material.AIR));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getDown() != null) {
//                            highRoom = highRoom.getDown();
//                        }
//                    }
//                    highRoom.setFilter(EnumSet.of(Material.STONE));
//                    while (highRoom.hasFilteredDown()) {
//                        if (highRoom.getSouth() != null) {
//                            highRoom = highRoom.getSouth();
//                        }
//                    }
//                    highRoom = highRoom.getDown();
//                    while (Material.AIR.equals(highRoom.getBlock().getType())) {
//                        highRoom.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                        highRoom.getBlock().setData((byte) 0x3);
//                        BlockMatrixNode getWest = highRoom;
//                        BlockMatrixNode getEast = highRoom;
//                        while (getWest.getBlock().getX() <= other.getMaxX()) {
//                            getWest = getWest.getWest();
//                            getWest.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getWest.getBlock().setData((byte) 0x3);
//                        }
//                        while (getEast.getBlock().getX() >= other.getMinX()) {
//                            getEast = getEast.getEast();
//                            getEast.getBlock().setType(Material.COBBLESTONE_STAIRS);
//                            getEast.getBlock().setData((byte) 0x3);
//                        }
//                        highRoom = highRoom.getSouth().getDown();
//                    }
//                }
//            }
//        }
//    }

