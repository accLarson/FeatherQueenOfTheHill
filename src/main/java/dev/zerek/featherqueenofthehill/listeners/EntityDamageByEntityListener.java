package dev.zerek.featherqueenofthehill.listeners;

import dev.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import dev.zerek.featherqueenofthehill.managers.QueenManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private final FeatherQueenOfTheHill plugin;
    private final QueenManager qm;

    public EntityDamageByEntityListener(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        this.qm = plugin.getQueenManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (qm.isQueenOnline() && qm.getQueen().getPlayer().hasPermission("feather.queen.atspawn") && !qm.getQueen().getPlayer().hasPermission("feather.queen.notatspawn")){
            if (event.getDamager() == qm.getQueen() && event.getEntity() instanceof Player){
                qm.addScore((float) (event.getDamage()/75.0));
            }
            if (event.getEntity() == qm.getQueen() && event.getDamager() instanceof Player){
                qm.addScore((float) (event.getDamage()/100.0));
            }
        }
    }
}
