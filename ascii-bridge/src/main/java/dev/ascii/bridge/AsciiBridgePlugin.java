package dev.ascii.bridge;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AsciiBridgePlugin extends JavaPlugin {

    private static final long COOLDOWN_MS = 30_000;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("AsciiBridge enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "ascii" -> handleAscii(sender, args);
            case "ascii-say" -> handleAsciiSay(sender, args);
            default -> { return false; }
        }
        return true;
    }

    private void handleAscii(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /ascii <message>"));
            return;
        }

        long now = System.currentTimeMillis();
        Long lastUsed = cooldowns.get(player.getUniqueId());
        if (lastUsed != null && (now - lastUsed) < COOLDOWN_MS) {
            long remaining = (COOLDOWN_MS - (now - lastUsed)) / 1000;
            player.sendMessage(Component.text("Please wait " + remaining + "s before using /ascii again."));
            return;
        }
        cooldowns.put(player.getUniqueId(), now);

        String message = String.join(" ", args);
        String playerName = player.getName();
        Bukkit.broadcast(Component.text("§d[Ascii] §7" + playerName + ": §f" + message));

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                new ProcessBuilder(
                    "ascii", "agent", "prompt",
                    "9c5855ee-9206-48d2-bf13-afb2006c6334",
                    "internal server message by " + playerName + ": " + message,
                    "--interrupt"
                ).inheritIO().start();
            } catch (IOException e) {
                getLogger().severe("Failed to run ascii command: " + e.getMessage());
                Bukkit.getScheduler().runTask(this, () ->
                    player.sendMessage(Component.text("§cFailed to send message to Ascii."))
                );
            }
        });
    }

    private void handleAsciiSay(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /ascii-say <message>"));
            return;
        }

        String message = String.join(" ", args);
        Bukkit.broadcast(Component.text("§d[Ascii] §f" + message));
    }
}
