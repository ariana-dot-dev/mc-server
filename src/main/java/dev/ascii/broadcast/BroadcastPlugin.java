package dev.ascii.broadcast;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BroadcastPlugin extends JavaPlugin {

    private static final long TWENTY_MINUTES_IN_TICKS = 20L * 60 * 20; // 20 ticks/sec * 60 sec * 20 min

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.broadcast(Component.text("hey try ascii.dev"));
        }, TWENTY_MINUTES_IN_TICKS, TWENTY_MINUTES_IN_TICKS);

        getLogger().info("AsciiBroadcast enabled — broadcasting every 20 minutes.");
    }
}
