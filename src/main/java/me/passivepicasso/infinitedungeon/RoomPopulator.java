package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BlockMatrixNode;
import me.passivepicasso.util.Box;
import org.bukkit.Material;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.Ladder;

import java.util.*;

import static net.minecraft.server.Material.*;


public class RoomPopulator extends BlockPopulator {

    private static final int CORRIDOR_CHANCE = 20;
    private static final int ROOM_CHANCE = 20;
    private static final int DOUBLE_HEIGHT_CHANCE = 10;

    private static List<BuildRequest> requestList = new ArrayList<BuildRequest>();

    @Override
    public void populate(World world, Random random, Chunk source) {
        int x, z, level, y_base, y_boost, xOffset, zOffset, halfWidth = random.nextInt(8) + 3, halfLength = random.nextInt(8) + 3, val;
        xOffset = random.nextInt(11 - (halfWidth * source.getX() >= 0 ? 1 : -1));
        zOffset = random.nextInt(11 - (halfLength * source.getZ() >= 0 ? 1 : -1));
        Box room;
        x = (source.getX() * 16);
        z = (source.getZ() * 16);
        level = 1;
        y_base = 127 - (4 * level);
        y_boost = (random.nextInt(100) < 10 ? 4 : 0);
        BuildRequest buildRequest;

        if (ROOM_CHANCE > random.nextInt(100)) {
            room = new Box(x + xOffset, y_base - y_boost, z + zOffset, 4 + y_boost, halfLength * 2, halfWidth * 2);
        } else {
            room = new Box(x + xOffset, y_base - y_boost, z + zOffset, 4 + y_boost, halfLength, halfWidth);
        }
        for (int bx = room.getMinX(); bx <= (room.getMaxX()); bx++) {
            for (int bz = room.getMinZ(); bz <= room.getMaxZ(); bz++) {
                for (int y = room.getMinY(); y < room.getMaxY(); y++) {
                    world.getBlockAt(bx, y, bz).setTypeId(0);
                }
            }
        }

        BlockFace direction = BlockFace.values()[random.nextInt(4)];
        BuildRequest request = new BuildRequest(source.getX() + direction.getModX(), source.getZ() + direction.getModZ(), room, direction.getOppositeFace());
        requestList.add(request);

        BlockFace[] faces = BlockFace.values();
        for (int i = random.nextInt(3); i >= 0; i--) {
            BlockFace next = faces[random.nextInt(4)];
            while (next.equals(direction)) {
                next = faces[random.nextInt(4)];
            }

            buildRequest = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);

            if (!requestList.contains(buildRequest)) {
                if (random.nextInt(100) < ROOM_CHANCE || random.nextInt(100) < ROOM_CHANCE) {
                    request = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);
                    requestList.add(request);
                }
            }
        }

        buildRequest = new BuildRequest(source.getX(), source.getZ(), room, BlockFace.NORTH);
        if (requestList.contains(buildRequest)) {
            buildRequest = requestList.remove(requestList.indexOf(buildRequest));
            Box requester = buildRequest.getRequester();
            direction = buildRequest.getDirection();

            Block current = world.getBlockAt(room.getMinX(), room.getMinY(), room.getMinZ());
            while (current.getTypeId() == 0) {
                current = current.getFace(direction);
            }
            Box hall = null;
            int hallLength = 1, hallOffset = 0, hallWidth = 1;
            int bx, bz, by;
            BlockFace nextDir = null;
            bx = current.getX();
            bz = current.getZ();
            by = current.getY();
            int modifier = 0;
            boolean doubleWide = random.nextBoolean();
            switch (direction) {
                case NORTH:
                case SOUTH:
                    if (bx < requester.getMinX() && bx < requester.getMaxX()) {
                        nextDir = BlockFace.SOUTH;
                    } else if (bx > requester.getMaxX() && bx > requester.getMinX()) {
                        nextDir = BlockFace.NORTH;
                    }
                    if (nextDir != null) {
                        current.setType(Material.WOODEN_DOOR);
                        current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
                         if(doubleWide){
                                current.getFace(BlockFace.EAST).setTypeId(0);
                                current.getFace(BlockFace.WEST).setTypeId(0);
                            }
                        while (!(bx >= requester.getMinX() && bx <= requester.getMaxX())) {
                            current = current.getFace(nextDir);
                            bx = current.getX();
                            current.setTypeId(0);
                            current.getFace(BlockFace.UP).setTypeId(0);
                        }
                        nextDir = null;
                    }
                    bz = current.getLocation().getBlockZ();
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
                            if(doubleWide){
                                current.getFace(BlockFace.NORTH).setTypeId(0);
                                current.getFace(BlockFace.SOUTH).setTypeId(0);
                            }
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
                    break;
                case EAST:
                case WEST:
                    if (bz < requester.getMinZ() && bz < requester.getMaxZ()) {
                        nextDir = BlockFace.WEST;
                    } else if (bz > requester.getMaxZ() && bz > requester.getMinZ()) {
                        nextDir = BlockFace.EAST;
                    }
                    if (nextDir != null) {
                        current.setType(Material.WOODEN_DOOR);
                        current.getFace(BlockFace.UP).setType(Material.WOODEN_DOOR);
                        while (!(bz >= requester.getMinZ() && bz <= requester.getMaxZ())) {
                            current = current.getFace(nextDir);
                            bz = current.getZ();
                            current.setTypeId(0);
                            current.getFace(BlockFace.UP).setTypeId(0);
                            if(doubleWide){
                                current.getFace(BlockFace.NORTH).setTypeId(0);
                                current.getFace(BlockFace.SOUTH).setTypeId(0);
                            }
                        }
                        nextDir = null;
                    }

                    bx = current.getX();
                    if (bx < requester.getMinX() && bx < requester.getMaxX()) {
                        nextDir = BlockFace.SOUTH;
                    } else if (bx > requester.getMaxX() && bx > requester.getMinX()) {
                        nextDir = BlockFace.NORTH;
                    } else if (bx == requester.getMaxX() || bz == requester.getMinX()) {
                        nextDir = BlockFace.SELF;
                    }

                    if (nextDir != null) {
                        current.setTypeId(0);
                        current.getFace(BlockFace.UP).setTypeId(0);
                        while (!(bx >= requester.getMinX() && bx <= requester.getMaxX())) {
                            current = current.getFace(nextDir);
                            bx = current.getX();
                            current.setTypeId(0);
                            current.getFace(BlockFace.UP).setTypeId(0);
                            if(doubleWide){
                                current.getFace(BlockFace.EAST).setTypeId(0);
                                current.getFace(BlockFace.WEST).setTypeId(0);
                            }
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
                    break;
            }

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
}