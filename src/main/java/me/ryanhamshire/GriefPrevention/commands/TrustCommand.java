package me.ryanhamshire.GriefPrevention.commands;

import com.github.puregero.multilib.MultiLib;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.handlers.TrustHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrustCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("trust")) {
                if(args.length != 1) return false;

                Player target = null;
                if(Bukkit.getPlayer(args[0]) != null) {
                    target = Bukkit.getPlayer(args[0]);
                }else{
                    return false;
                }

                if(MultiLib.isExternalPlayer(target)) {
                    MultiLib.notify("griefprevention:trust", "TRUST;" + player.getName() + ";" + args[0] + ";Build");
                }else{
                    TrustHandler.trustRequest(player, ClaimPermission.Build, args[0]);
                }
                return true;
            }else if(cmd.getName().equalsIgnoreCase("accesstrust")) {
                if(args.length != 1) return false;

                Player target = null;
                if(Bukkit.getPlayer(args[0]) != null) {
                    target = Bukkit.getPlayer(args[0]);
                }else{
                    return false;
                }

                if(MultiLib.isExternalPlayer(target)) {
                    MultiLib.notify("griefprevention:trust", "TRUST;" + player.getName() + ";" + args[0] + ";Access");
                }else{
                    TrustHandler.trustRequest(player, ClaimPermission.Access, args[0]);
                }
                return true;
            }else if(cmd.getName().equalsIgnoreCase("containertrust")) {
                if(args.length != 1) return false;

                Player target = null;
                if(Bukkit.getPlayer(args[0]) != null) {
                    target = Bukkit.getPlayer(args[0]);
                }else{
                    return false;
                }

                if(MultiLib.isExternalPlayer(target)) {
                    MultiLib.notify("griefprevention:trust", "TRUST;" + player.getName() + ";" + args[0] + ";Inventory");
                }else{
                    TrustHandler.trustRequest(player, ClaimPermission.Inventory, args[0]);
                }
                return true;
            }else if(cmd.getName().equalsIgnoreCase("permissiontrust")) {
                if(args.length != 1) return false;

                Player target = null;
                if(Bukkit.getPlayer(args[0]) != null) {
                    target = Bukkit.getPlayer(args[0]);
                }else{
                    return false;
                }

                if(MultiLib.isExternalPlayer(target)) {
                    MultiLib.notify("griefprevention:trust", "TRUST;" + player.getName() + ";" + args[0] + ";null");
                }else{
                    TrustHandler.trustRequest(player, null, args[0]);
                }
                return true;
            }else if(cmd.getName().equalsIgnoreCase("untrust")) {
                if(args.length != 1) return false;

                Player target = null;
                if(Bukkit.getPlayer(args[0]) != null) {
                    target = Bukkit.getPlayer(args[0]);
                }else{
                    return false;
                }

                if(MultiLib.isExternalPlayer(target)) {
                    MultiLib.notify("griefprevention:untrust", "UNTRUST;" + player.getName() + ";" + args[0]);
                }else{
                    TrustHandler.unTrustRequest(player, player.getName());
                }
                return true;
            }
        }
        return false;
    }

}
