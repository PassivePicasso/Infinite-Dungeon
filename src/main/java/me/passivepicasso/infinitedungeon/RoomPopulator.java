package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BoxRoom;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.*;
import java.util.Map.Entry;


public class RoomPopulator extends BlockPopulator {

    public static List<BuildRequest> requestList = new ArrayList<BuildRequest>();
    public static Map<BuildRequest, BoxRoom> ROOMS = new HashMap<BuildRequest, BoxRoom>();
    private static final int ADDITIONAL_HEIGHT_CHANCE = 20;
    private static final int LIT_ROOM_CHANCE = 15;
    private static final int CORRIDOR_ROOM_CHANCE = 40;
    public static BoxRoom homeRoom;

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (Math.abs(source.getX()) > 16 || Math.abs(source.getZ()) > 16) {
            return;
        }

        BuildRequest buildRequest = new BuildRequest(source.getX(), source.getZ());

        //Create Starting room for which all other rooms will be connected to.  Creating the starting room creates at minimum a single request for an additional room.
        if (homeRoom == null) {
            BoxRoom room = createRoom(world, random, source);
            ROOMS.put(new BuildRequest(source.getX(), source.getZ()), room);
            homeRoom = room;
            IDChunkGenerator.fixedSpawnLocation = new Location(world, homeRoom.getMinX(), homeRoom.getMinY(), homeRoom.getMinZ());
        }
        //If this chunk has a request to construct a room...
        if (requestList.contains(buildRequest)) {

            //create room with at minimum a single request to a neighboring chunk.
            BoxRoom room = createRoom(world, random, source);

            //remove this chunks request from the request list.
            buildRequest = requestList.remove(requestList.indexOf(buildRequest));

            //Store a reference to the request and generated room, for backwards awareness;
            ROOMS.put(buildRequest, room);

            BoxRoom requester = buildRequest.getRequester();
            BlockFace direction = buildRequest.getDirection(); // direction leading back towards the chunk that made the request

            //Get the block from corner of the generated room, select the next block towards direction until we hit a solid block (non air)
            Block current = world.getBlockAt(room.getMinX(), room.getMinY(), room.getMinZ());
            while (current.getTypeId() == 0) {
                current = current.getFace(direction);
            }

            //prepare hall direction awareness.
            BlockFace nextDir = null;

            while (Material.AIR.equals(current.getFace(BlockFace.DOWN).getType())) {
                current = current.getFace(BlockFace.DOWN);
            }
            HallTunneler tunneler = new HallTunneler(nextDir, requester, current);
            switch (direction) {
                case NORTH:
                case SOUTH:
                    //if we are traveling to the north/south, run north/south first hall gen.
                    createVerticalHall(tunneler);
                    break;
                case EAST:
                case WEST:
                    //if we are traveling to the East/West, run East/West first hall gen.
                    createHorizontalHall(tunneler);
                    break;
            }
        }
    }

    private static void createVerticalHall(HallTunneler hallTunneler) {
        hallTunneler.setNextDir(getAxialDirection(hallTunneler.getRequester().getMinZ(), hallTunneler.getRequester().getMaxZ(), hallTunneler.getCurrent().getZ(), BlockFace.WEST, BlockFace.EAST));
        hallTunneler.tunnel(hallTunneler.getRequester().getMinZ(), hallTunneler.getRequester().getMaxZ(), new XorZRetriever() {
            @Override
            public int get(Block block) {
                return block.getZ();
            }
        });
        hallTunneler.setNextDir(getAxialDirection(hallTunneler.getRequester().getMinX(), hallTunneler.getRequester().getMaxX(), hallTunneler.getCurrent().getX(), BlockFace.SOUTH, BlockFace.NORTH));
        hallTunneler.tunnel(hallTunneler.getRequester().getMinX(), hallTunneler.getRequester().getMaxX(), new XorZRetriever() {
            @Override
            public int get(Block block) {
                return block.getX();
            }
        });
    }


    private static void createHorizontalHall(HallTunneler hallTunneler) {
        hallTunneler.setNextDir(getAxialDirection(hallTunneler.getRequester().getMinX(), hallTunneler.getRequester().getMaxX(), hallTunneler.getCurrent().getX(), BlockFace.SOUTH, BlockFace.NORTH));
        hallTunneler.tunnel(hallTunneler.getRequester().getMinX(), hallTunneler.getRequester().getMaxX(), new XorZRetriever() {
            @Override
            public int get(Block block) {
                return block.getX();
            }
        });
        hallTunneler.setNextDir(getAxialDirection(hallTunneler.getRequester().getMinZ(), hallTunneler.getRequester().getMaxZ(), hallTunneler.getCurrent().getZ(), BlockFace.WEST, BlockFace.EAST));
        hallTunneler.tunnel(hallTunneler.getRequester().getMinZ(), hallTunneler.getRequester().getMaxZ(), new XorZRetriever() {
            @Override
            public int get(Block block) {
                return block.getZ();
            }
        });
    }

    private static BlockFace getAxialDirection(int min, int max, int currentValue, BlockFace lesserDirection, BlockFace greaterDirection) {
        if (currentValue < min) {
            return lesserDirection;
        } else if (currentValue > max) {
            return greaterDirection;
        }
        return null;
    }

    public static BoxRoom createRoom(World world, Random random, Chunk source) {
        int x, z, level, y_base, y_boost = 0, xOffset, zOffset, halfWidth = random.nextInt(9),
                halfLength = random.nextInt(9);
        xOffset = halfWidth == 0 ? 0 : (16 % (halfWidth * 2)) / 2;
        zOffset = halfLength == 0 ? 0 : (16 % (halfLength * 2)) / 2;
        x = (source.getX() * 16);
        z = (source.getZ() * 16);
        level = 1;
        y_base = 126 - (4 * level);
        while (random.nextInt(100) < ADDITIONAL_HEIGHT_CHANCE) {
            y_boost += 4;
        }
        if (homeRoom != null && random.nextInt(100) < CORRIDOR_ROOM_CHANCE) {
            halfWidth = 0;
            halfLength = 0;
            y_boost = -2;
        }

        BuildRequest buildRequest;

        BoxRoom room = new BoxRoom(x + (x < 0 ? xOffset : -xOffset), y_base - y_boost, z + (z < 0 ? zOffset : -zOffset), 4 + y_boost, halfLength * 2, halfWidth * 2);
        room.setLit(random.nextInt(100) < LIT_ROOM_CHANCE);

        //Create atleast 1 request in the target direction.
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        BlockFace direction = faces[random.nextInt(4)];
        buildRequest = new BuildRequest(source.getX() + direction.getModX(), source.getZ() + direction.getModZ(), room, direction.getOppositeFace());
        requestList.add(buildRequest);

        ArrayList<BlockFace> usedDirections = new ArrayList<BlockFace>();
        usedDirections.add(direction);
        for (int i = random.nextInt(3); i >= 0; i--) {
            BlockFace next = faces[random.nextInt(4)];
            if (usedDirections.size() == 4) {
                break;
            }
            while (usedDirections.contains(next)) {
                next = faces[random.nextInt(4)];
            }
            usedDirections.add(next);

            buildRequest = new BuildRequest(source.getX() + next.getModX(), source.getZ() + next.getModZ(), room, next);
            requestList.add(buildRequest);
        }

        //find all rooms that intersect with this room and connect them
        outer:
        for (Entry<BuildRequest, BoxRoom> other : new ArrayList<Entry<BuildRequest, BoxRoom>>(RoomPopulator.ROOMS.entrySet())) {
            if (!room.equals(other) && room.intersects(other.getValue())) {
                RoomPopulator.ROOMS.remove(other.getKey());
            }
        }
        for (BoxRoom part : room.getRoomParts()) {
            fillRoom(world, room.isLit(), part);
        }
        return room;
    }

    private static void fillRoom(World world, boolean litRoom, BoxRoom room) {
        for (int bx = room.getMinX(); bx <= (room.getMaxX()); bx++) {
            for (int bz = room.getMinZ(); bz <= room.getMaxZ(); bz++) {
                for (int y = room.getMinY(); y < room.getMaxY(); y++) {
                    int typeId = 0;
                    if (litRoom) {
                        if (y % 4 == 2 && (((bx == room.getMinX() || bx == room.getMaxX()) && bz % 4 == 0) || ((bz == room.getMinZ() || bz == room.getMaxZ()) && bx % 4 == 0))) {
                            typeId = 50;
                        }
                    }
                    world.getBlockAt(bx, y, bz).setTypeId(typeId);
                }
            }
        }
    }


}