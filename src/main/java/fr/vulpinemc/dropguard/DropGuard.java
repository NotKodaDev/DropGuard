package fr.vulpinemc.dropguard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DropGuard extends JavaPlugin implements Listener {

    /** Timestamp du dernier appui (demande de confirmation) par joueur. */
    private final Map<UUID, Long> pendingConfirmations = new ConcurrentHashMap<>();

    private long confirmWindowMillis = 3_000L;
    private String confirmMessage = "";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DropGuard activé - drop confirmé en " + confirmWindowMillis / 1000L + "s.");
    }

    @Override
    public void onDisable() {
        pendingConfirmations.clear();
        getLogger().info("DropGuard désactivé.");
    }

    private void reloadConfigValues() {
        confirmWindowMillis = Math.max(1, getConfig().getLong("confirm-window-seconds", 3L)) * 1000L;
        confirmMessage = getConfig().getString("confirm-message", "");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        // Bypass : ceux qui ont la permission droppent directement.
        if (player.hasPermission("dropguard.bypass")) {
            pendingConfirmations.remove(uuid);
            return;
        }

        final long now = System.currentTimeMillis();
        final Long previous = pendingConfirmations.get(uuid);

        // Second appui dans la fenêtre : on confirme le drop.
        if (previous != null && now - previous <= confirmWindowMillis) {
            pendingConfirmations.remove(uuid);
            return;
        }

        // Premier appui (ou fenêtre expirée) : on bloque et on demande confirmation.
        event.setCancelled(true);
        pendingConfirmations.put(uuid, now);

        if (!confirmMessage.isEmpty()) {
            String itemName = event.getItemDrop().getItemStack().getType().name().toLowerCase();
            String msg = confirmMessage.replace("{item}", itemName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pendingConfirmations.remove(event.getPlayer().getUniqueId());
    }
}
