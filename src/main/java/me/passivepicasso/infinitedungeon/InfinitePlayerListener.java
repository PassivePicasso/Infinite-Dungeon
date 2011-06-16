package me.passivepicasso.infinitedungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Created by IntelliJ IDEA.
 * User: Tobias
 * Date: 6/12/11
 * Time: 7:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class InfinitePlayerListener extends PlayerListener {

    private World dungeonWorld;

    public InfinitePlayerListener(World dungeonWorld){
        this.dungeonWorld = dungeonWorld;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World normal = player.getServer().getWorlds().get(0);
        if (player.getWorld().equals(normal)) {
            event.getPlayer().teleport(new Location(dungeonWorld, 0, 5, 0));
        }
    }
}
