
package org.wargamer2010.signshop.listeners.sslisteners;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;

import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.events.SSCreatedEvent;
import org.wargamer2010.signshop.hooks.HookManager;

public class WorldGuardChecker implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSSBuildEvent(SSCreatedEvent event) {
        if(event.isCancelled() || !event.canBeCancelled() || event.getPlayer().getPlayer() == null)
            return;
        if(HookManager.getHook("WorldGuard") == null)
            return;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = ((LocalPlayer) event.getPlayer().getPlayer()).getWorld();
        Location sl = event.getSign().getLocation();
        BlockVector3 region = BlockVector3.at(sl.getX(), sl.getY(), sl.getZ());

        for(ProtectedRegion r : container.get(world).getApplicableRegions(region).getRegions()) {
            for(Map.Entry<Flag<?>, Object> flag : r.getFlags().entrySet()) {
                if(flag.getKey().getName().equals("allow-shop")) {
                    if(flag.getKey() instanceof StateFlag) {
                        if(flag.getValue() == StateFlag.State.ALLOW) // allow-shop is true
                            return;
                    }
                }
            }
        }

        if(event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(SignShopConfig.getError("region_allow_shops_but_op", event.getMessageParts()));
        } else if(event.getPlayer().hasBypassShopPlots("WorldGuard")) {
            event.getPlayer().sendMessage(SignShopConfig.getError("region_allow_shops_but_perm", event.getMessageParts()));
        } else {
            event.getPlayer().sendMessage(SignShopConfig.getError("region_does_not_allow_shops", event.getMessageParts()));
            event.setCancelled(true);
        }
    }
}
