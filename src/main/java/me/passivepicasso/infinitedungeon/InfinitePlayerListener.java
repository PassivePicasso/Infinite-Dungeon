package me.passivepicasso.infinitedungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.Random;

public class InfinitePlayerListener extends PlayerListener {

    private World dungeonWorld;
    private IDChunkGenerator idChunkGen;

    public InfinitePlayerListener(World dungeonWorld, IDChunkGenerator idChunkGen){
        this.dungeonWorld = dungeonWorld;
        this.idChunkGen = idChunkGen;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World normal = player.getServer().getWorlds().get(0);
        if (player.getWorld().equals(normal)) {
            event.getPlayer().teleport(idChunkGen.getFixedSpawnLocation(dungeonWorld));
        }
    }
}
