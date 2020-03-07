package org.wargamer2010.signshop.hooks;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WorldGuardHook implements Hook  {

    @Override
    public String getName() {
        return "WorldGuard";
    }

    @Override
    public Boolean canBuild(Player player, Block block) {
        if (HookManager.getHook("WorldGuard") == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) return true;

        Location loc = localPlayer.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testState(loc, localPlayer, Flags.BUILD);
    }

    @Override
    public Boolean protectBlock(Player player, Block block) {
        return false;
    }
}
