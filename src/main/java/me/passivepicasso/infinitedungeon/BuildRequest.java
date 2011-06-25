package me.passivepicasso.infinitedungeon;

import org.bukkit.block.BlockFace;
import me.passivepicasso.util.Box;

public class BuildRequest {
    int targetX, targetZ;
    Box requester;
    BlockFace direction;

    public BuildRequest(int targetX, int targetZ, Box requester, BlockFace direction) {
        this.targetX = targetX;
        this.targetZ = targetZ;
        this.direction = direction;
        this.requester = requester;
    }

    public int getTargetX() {
        return targetX;
    }

    public BlockFace getDirection() {
        return direction;
    }


    public Box getRequester() {
        return requester;
    }


    public int getTargetZ() {
        return targetZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuildRequest)) return false;

        BuildRequest that = (BuildRequest) o;

        if (targetX != that.targetX) return false;
        return targetZ == that.targetZ;
    }

    @Override
    public int hashCode() {
        int result = targetX;
        result = 31 * result + targetZ;
        return result;
    }
}
