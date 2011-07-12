package me.passivepicasso.infinitedungeon;

import joptsimple.util.KeyValuePair;
import me.passivepicasso.util.Box;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.*;


public class RoomPopulator extends BlockPopulator {

    private static final int ROOM_CHANCE = 25;

    public static List<BuildRequest> requestList = new ArrayList<BuildRequest>();
    public static Map<BuildRequest, Box> ROOMS = new HashMap<BuildRequest, Box>();

    public static Box getHomeRoom() {
        if (homeRoom != null) {

        }
        return homeRoom;
    }

    public static Box homeRoom;


    @Override
    public void populate(World world, Random random, Chunk source) {

        BuildRequest buildRequest = new BuildRequest(source.getX(), source.getZ());

        //Create Starting room for which all other rooms will be connected to.  Creating the starting room creates at minimum a single request for an additional room.
        if (homeRoom == null) {
            Box room = createRoom(world, random, source);
            ROOMS.put(new BuildRequest(source.getX(), source.getZ()), room);
            homeRoom = room;
            IDChunkGenerator.fixedSpawnLocation = new Location(world, homeRoom.getMinX(), homeRoom.getMinY(), homeRoom.getMinZ());
        }
        //If this chunk has a request to construct a room...
        if (requestList.contains(buildRequest)) {

            //create room with at minimum a single request to a neighboring chunk.
            Box room = createRoom(world, random, source);

            //remove this chunks request from the request list.
            buildRequest = requestList.remove(requestList.indexOf(buildRequest));

            //Store a reference to the request and generated room, for backwards awareness;
            ROOMS.put(buildRequest, room);

            Box requester = buildRequest.getRequester();
            BlockFace direction = buildRequest.getDirection(); // direction leading back towards the chunk that made the request

            //Get the block from corner of the generated room, select the next block towards direction until we hit a solid block (non air)
            Block current = world.getBlockAt(room.getMinX(), room.getMinY(), room.getMinZ());
            while (current.getTypeId() == 0) {
                current = current.getFace(direction);
            }

            //prepare hall direction awareness.
            BlockFace nextDir = null;

            switch (direction) {
                case NORTH:
                case SOUTH:
                    //if we are traveling to the north/south, run north/south first hall gen.
                    nextDir = createVerticalHall(nextDir, requester, current);
                    break;
                case EAST:
                case WEST:
                    //if we are traveling to the East/West, run East/West first hall gen.
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

            while (!(bx >= requester.getMinX() && bx <= requester.getMaxX())) {
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
        }
        return nextDir;
    }

    private static BlockFace createHorizontalHall(BlockFace nextDir, Box requester, Block current) {
        int bz = current.getZ();
        int by = current.getY();
        //Orrient against negative/positive location along the z axis
        if (bz < requester.getMinZ() && bz < requester.getMaxZ()) {
            nextDir = BlockFace.WEST;
        } else if (bz > requester.getMaxZ() && bz > requester.getMinZ()) {
            nextDir = BlockFace.EAST;
        }


        if (nextDir != null) {
            //while current is not within the z bounds of the room...

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
            nextDir = null;
        }

        int bx = current.getX();
        if (bx < requester.getMinX()) {
            nextDir = BlockFace.SOUTH;
        } else if (bx > requester.getMaxX()) {
            nextDir = BlockFace.NORTH;
        }

        if (nextDir != null) {
            current.setTypeId(0);
            current.getFace(BlockFace.UP).setTypeId(0);
            while (!(bx >= requester.getMinX() && bx <= requester.getMaxX())) {
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
        }
        return nextDir;
    }

    public static Box createRoom(World world, Random random, Chunk source) {
        int x, z, level, y_base, y_boost = 0, xOffset, zOffset, halfWidth = random.nextInt(10), halfLength = random.nextInt(10), val;
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

        Box room = new Box(x + (x < 0 ? xOffset : -xOffset), y_base - y_boost, z + (z < 0 ? zOffset : -zOffset), 4 + y_boost, halfLength * 2, halfWidth * 2);

        //Create atleast 1 request in the target direction.
        BlockFace direction = BlockFace.values()[random.nextInt(4)];
        buildRequest = new BuildRequest(source.getX() + direction.getModX(), source.getZ() + direction.getModZ(), room, direction.getOppositeFace());
        requestList.add(buildRequest);

        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        ArrayList<BlockFace> usedDirections = new ArrayList<BlockFace>();
        usedDirections.add(direction);
        for (int i = random.nextInt(2); i >= 0; i--) {
            BlockFace next = faces[random.nextInt(4)];
            while (usedDirections.contains(next)) {
                next = faces[random.nextInt(4)];
            }
            usedDirections.add(next);

            buildRequest = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);
            if (!requestList.contains(buildRequest)) {
                requestList.add(buildRequest);
            }
        }

        for (int bx = room.getMinX(); bx <= (room.getMaxX()); bx++) {
            for (int bz = room.getMinZ(); bz <= room.getMaxZ(); bz++) {
                for (int y = room.getMinY(); y < room.getMaxY(); y++) {
                    world.getBlockAt(bx, y, bz).setTypeId(0);
                }
            }
        }
        return room;
    }
}