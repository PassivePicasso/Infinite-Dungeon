package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.Box;
import org.bukkit.Material;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.*;


public class RoomPopulator extends BlockPopulator {

    private static final int ROOM_CHANCE = 20;

    private static List<BuildRequest> requestList = new ArrayList<BuildRequest>();
    public static List<Box> ROOMS = new ArrayList<Box>();

    @Override
    public void populate(World world, Random random, Chunk source) {

        BuildRequest buildRequest = new BuildRequest(source.getX(), source.getZ(), new Box(), BlockFace.SELF);
        if (ROOMS.isEmpty()) {
            Box room = createRoom(world, random, source);
        }
        if (requestList.contains(buildRequest)) {
            Box room = createRoom(world, random, source);
            buildRequest = requestList.remove(requestList.indexOf(buildRequest));
            Box requester = buildRequest.getRequester();
            BlockFace direction = buildRequest.getDirection();

            Block current = world.getBlockAt(room.getMinX(), room.getMinY(), room.getMinZ());
            while (current.getTypeId() == 0) {
                current = current.getFace(direction);
            }
            BlockFace nextDir = null;

            switch (direction) {
                case NORTH:
                case SOUTH:
                    nextDir = createVerticalHall(nextDir, requester, current);
                    break;
                case EAST:
                case WEST:
                    nextDir = createHorizontalHall(nextDir, requester, current);
                    break;
            }

            int by = current.getY();
            if (nextDir != null) {
                if (by < requester.getMinY()) {
                    byte dirByte = 0x2;
                    switch (nextDir) {
                        case WEST:
                            dirByte = 0x2;
                            break;
                        case EAST:
                            dirByte = 0x3;
                            break;
                        case SOUTH:
                            dirByte = 0x4;
                            break;
                        case NORTH:
                            dirByte = 0x5;
                            break;
                    }
                    current.setTypeIdAndData(Material.LADDER.getId(), dirByte, false);
                    while (by <= requester.getMinY()) {
                        current = current.getFace(BlockFace.UP);
                        current.setTypeIdAndData(Material.LADDER.getId(), dirByte, false);
                        by = current.getY();
                    }
                }
            }
        }
    }

    private static BlockFace createVerticalHall(BlockFace nextDir, Box requester, Block current) {
        int by = current.getY();
        int bx = current.getX();
        if (bx < requester.getMinX() && bx < requester.getMaxX()) {
            nextDir = BlockFace.SOUTH;
        } else if (bx > requester.getMaxX() && bx > requester.getMinX()) {
            nextDir = BlockFace.NORTH;
        }
        if (nextDir != null) {
            current.setType(Material.WOODEN_DOOR);
            current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
            while (!(bx >= requester.getMinX() && bx <= requester.getMaxX())) {
                current = current.getFace(nextDir);
                bx = current.getX();
                current.setTypeId(0);
                current.getFace(BlockFace.UP).setTypeId(0);
            }
            nextDir = null;
        }
        int bz = current.getZ();
        if (bz < requester.getMinZ() && bz < requester.getMaxZ()) {
            nextDir = BlockFace.WEST;
        } else if (bz > requester.getMaxZ() && bz > requester.getMinZ()) {
            nextDir = BlockFace.EAST;
        }

        if (nextDir != null) {
            while (!(bz >= requester.getMinZ() && bz <= requester.getMaxZ())) {
                current = current.getFace(nextDir);
                bz = current.getZ();
                current.setTypeId(0);
                current.getFace(BlockFace.UP).setTypeId(0);
                if (by < requester.getMinY()) {
                    current = current.getFace(BlockFace.UP);
                    current.getFace(BlockFace.UP).setTypeId(0);
                    by = current.getY();
                } else if (by > requester.getMinY()) {
                    current = current.getFace(BlockFace.DOWN);
                    current.setTypeId(0);
                    by = current.getY();
                }
            }
            current.setType(Material.WOODEN_DOOR);
            current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
        }
        return nextDir;
    }

    private static BlockFace createHorizontalHall(BlockFace nextDir, Box requester, Block current) {
        int bz = current.getZ();
        int by = current.getY();
        if (bz < requester.getMinZ() && bz < requester.getMaxZ()) {
            nextDir = BlockFace.WEST;
        } else if (bz > requester.getMaxZ() && bz > requester.getMinZ()) {
            nextDir = BlockFace.EAST;
        }
        if (nextDir != null) {
            current.setType(Material.WOODEN_DOOR);
            current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
            while (!(bz > requester.getMinZ() && bz < requester.getMaxZ())) {
                current = current.getFace(nextDir);
                bz = current.getZ();
                current.setTypeId(0);
                current.getFace(BlockFace.UP).setTypeId(0);
            }
            nextDir = null;
        }

        int bx = current.getX();
        if (bx < requester.getMinX() && bx < requester.getMaxX()) {
            nextDir = BlockFace.SOUTH;
        } else if (bx > requester.getMaxX() && bx > requester.getMinX()) {
            nextDir = BlockFace.NORTH;
        }

        if (nextDir != null) {
            current.setTypeId(0);
            current.getFace(BlockFace.UP).setTypeId(0);
            while (!(bx > requester.getMinX() && bx < requester.getMaxX())) {
                current = current.getFace(nextDir);
                bx = current.getX();
                current.setTypeId(0);
                current.getFace(BlockFace.UP).setTypeId(0);
                if (by < requester.getMinY()) {
                    current = current.getFace(BlockFace.UP);
                    current.getFace(BlockFace.UP).setTypeId(0);
                    by = current.getY();
                } else if (by > requester.getMinY()) {
                    current = current.getFace(BlockFace.DOWN);
                    current.setTypeId(0);
                    by = current.getY();
                }
            }
            current.setType(Material.WOODEN_DOOR);
            current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
        }
        return nextDir;
    }

    private static Box createRoom(World world, Random random, Chunk source) {
        int x, z, level, y_base, y_boost = 0, xOffset, zOffset, halfWidth = random.nextInt(9), halfLength = random.nextInt(9), val;
        xOffset = halfWidth == 0 ? 0 : (16 % (halfWidth * 2)) / 2;//random.nextInt(11 - (halfWidth * source.getX() >= 0 ? 1 : -1));
        zOffset = halfLength == 0 ? 0 : (16 % (halfLength * 2)) / 2;//random.nextInt(11 - (halfLength * source.getZ() >= 0 ? 1 : -1));
        x = (source.getX() * 16);
        z = (source.getZ() * 16);
        level = 1;
        y_base = 127 - (4 * level);
        while (random.nextInt(100) < 10) {
            y_boost += 4;
        }
        BuildRequest buildRequest;

        Box room = new Box(x + (x < 0 ? -xOffset : xOffset), y_base - y_boost, z + (z < 0 ? -zOffset : zOffset), 4 + y_boost, halfLength * 2, halfWidth * 2);

        BlockFace direction = BlockFace.values()[random.nextInt(4)];
        BuildRequest request = new BuildRequest(source.getX() + direction.getModX(), source.getZ() + direction.getModZ(), room, direction.getOppositeFace());
        requestList.add(request);

        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        ArrayList<BlockFace> usedDirections = new ArrayList<BlockFace>();
        usedDirections.add(direction);
        for (int i = random.nextInt(3); i >= 0; i--) {
            BlockFace next = faces[random.nextInt(4)];
            while (usedDirections.contains(next)) {
                next = faces[random.nextInt(4)];
            }
            usedDirections.add(next);

            buildRequest = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);

            if (!requestList.contains(buildRequest)) {
                if (random.nextInt(100) < ROOM_CHANCE || random.nextInt(100) < ROOM_CHANCE) {
                    request = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);
                    requestList.add(request);
                }
            }
        }

        for (int bx = room.getMinX(); bx <= (room.getMaxX()); bx++) {
            for (int bz = room.getMinZ(); bz <= room.getMaxZ(); bz++) {
                for (int y = room.getMinY(); y < room.getMaxY(); y++) {
                    world.getBlockAt(bx, y, bz).setTypeId(0);
                }
            }
        }
        ROOMS.add(room);
        return room;
    }
}