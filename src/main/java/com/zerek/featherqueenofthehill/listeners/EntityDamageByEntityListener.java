package com.zerek.featherqueenofthehill.listeners;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import com.zerek.featherqueenofthehill.managers.QueenManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private final FeatherQueenOfTheHill plugin;
    private final QueenManager qm;

    public EntityDamageByEntityListener(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        this.qm = plugin.getQueenManager();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (qm.isOnline()){
            if (event.getDamager() == qm.getQueen()){
                qm.addScore(event.getDamage()/75.0);
            }
            if (event.getEntity() == qm.getQueen()){
                qm.addScore(event.getDamage()/100.0);
            }
        }
    }
}
