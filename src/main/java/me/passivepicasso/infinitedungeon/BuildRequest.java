package me.passivepicasso.infinitedungeon;

import me.passivepicasso.util.BoxRoom;
import org.bukkit.block.BlockFace;

public class BuildRequest {
    int targetX, targetZ;
    BoxRoom requester;
    BlockFace direction;

    public BuildRequest(int targetX, int targetZ, BoxRoom requester, BlockFace direction) {
        this.targetX = targetX;
        this.targetZ = targetZ;
        this.direction = direction;
        this.requester = requester;
    }

    public BuildRequest(int targetX, int targetZ) {
        this.targetX = targetX;
        this.targetZ = targetZ;
    }

    public int getTargetX() {
        return targetX;
    }

    public BlockFace getDirection() {
        return direction;
    }


    public BoxRoom getRequester() {
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

        return targetX == that.targetX && targetZ == that.targetZ;
    }

    @Override
    public int hashCode() {
        int result = targetX;
        result = 31 * result + targetZ;
        return result;
    }
}
