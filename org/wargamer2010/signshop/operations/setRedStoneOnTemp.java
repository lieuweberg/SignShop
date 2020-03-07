package org.wargamer2010.signshop.operations;

import org.bukkit.Material;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.Block;
import org.bukkit.Bukkit;
import org.wargamer2010.signshop.util.signshopUtil;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.util.lagSetter;
import org.wargamer2010.signshop.SignShop;

public class setRedStoneOnTemp implements SignShopOperation {
    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        boolean foundLever = false;
        for(Block block : ssArgs.getActivatables().get())
            if(block.getType() == Material.getMaterial("LEVER"))
                foundLever = true;
        if(!foundLever) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("lever_missing", ssArgs.getMessageParts()));
            return false;
        }
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        if(!setupOperation(ssArgs))
            return false;
        boolean bReturn = false;
        Block bLever;

        for(int i = 0; i < ssArgs.getActivatables().get().size(); i++) {
            bLever = ssArgs.getActivatables().get().get(i);

            if(bLever.getType() == Material.getMaterial("LEVER")) {
                Switch lever = (Switch) bLever.getBlockData();
                if(!lever.isPowered())
                    bReturn = true;
            }
        }
        if(!bReturn)
            ssArgs.sendFailedRequirementsMessage("already_on");
        return bReturn;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        if(!setupOperation(ssArgs))
            return false;

        Block bLever;

        int delay = 20;
        if(!ssArgs.getFirstOperationParameter().isEmpty()) {
            try {
                delay = Integer.parseInt(ssArgs.getFirstOperationParameter());
                if(delay <= 0)
                    delay = 20;
            } catch(NumberFormatException ignored) { }
        }

        for(int i = 0; i < ssArgs.getActivatables().get().size(); i++) {
            bLever = ssArgs.getActivatables().get().get(i);
            if(bLever.getType() == Material.getMaterial("LEVER")) {
                Switch lever = (Switch) bLever.getBlockData();
                if(!lever.isPowered()) {
                    lever.setPowered(true);
                    bLever.setBlockData(lever);
                    signshopUtil.generateInteractEvent(bLever, ssArgs.getPlayer().get().getPlayer(), ssArgs.getBlockFace().get());
                    Bukkit.getServer().getScheduler().runTaskLater(SignShop.getInstance(),new lagSetter(bLever),10*delay);
                }
            }
        }

        return true;
    }
}
