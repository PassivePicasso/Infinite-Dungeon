package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BoxRoom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class HallTunneler {

    public void setNextDir(BlockFace nextDir) {
        this.nextDir = nextDir;
    }

    private BlockFace nextDir;
    private BoxRoom requester;
    private Block current;
    private int by;

    public HallTunneler(BlockFace nextDir, BoxRoom requester, Block current) {
        this.nextDir = nextDir;
        this.requester = requester;
        this.current = current;
        by = current.getY();
    }

    public BoxRoom getRequester() {
        return requester;
    }

    public BlockFace getNextDir() {
        return nextDir;
    }

    public Block getCurrent() {
        return current;
    }

    public int getBy() {
        return by;
    }

    private static Byte getByteDirection(BlockFace direction) {
        Byte dir = null;
        switch (direction) {
            case SOUTH:
                dir = 0x0;
                break;
            case NORTH:
                dir = 0x1;
                break;
            case WEST:
                dir = 0x2;
                break;
            case EAST:
                dir = 0x3;
                break;
        }
        return dir;
    }

    public HallTunneler tunnel(int min, int max, XorZRetriever currentAxisValue) {
        int cVal = currentAxisValue.get(current);
        if (nextDir != null) {
            Byte dir = getByteDirection(nextDir);
            Byte opDir = getByteDirection(nextDir.getOppositeFace());
            while (!(cVal >= min && cVal <= max)) {
                current = current.getFace(nextDir);
                current.setTypeId(0);
                current.getFace(BlockFace.UP).setTypeId(0);
                if (by < requester.getMinY()) {
                    if (dir != null) {
                        current.setTypeIdAndData(67, dir, false);
                    }
                    current = current.getFace(BlockFace.UP);
                    current.getFace(BlockFace.UP).setTypeId(0);
                } else if (by > requester.getMinY()) {
                    current = current.getFace(BlockFace.DOWN);
                    current.setTypeId(0);
                    if (by + BlockFace.DOWN.getModY() >= requester.getMinY()) {
                        current.getFace(BlockFace.DOWN).setTypeIdAndData(67, opDir, false);
                    }
                    if (Material.AIR.equals(current.getFace(nextDir).getType())) {
                        current = current.getFace(nextDir);
                        while (Material.AIR.equals(current.getFace(BlockFace.DOWN).getType())) {
                            current = current.getFace(BlockFace.DOWN);
                            current.setTypeIdAndData(Material.LADDER.getId(), opDir, false);
                        }
                        break;
                    }
                }
                cVal = currentAxisValue.get(current);
                by = current.getY();
            }
            nextDir = null;
        }
        return this;
    }
}
