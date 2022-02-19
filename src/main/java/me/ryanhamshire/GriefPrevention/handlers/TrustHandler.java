package me.ryanhamshire.GriefPrevention.handlers;

import me.ryanhamshire.GriefPrevention.*;
import me.ryanhamshire.GriefPrevention.events.TrustChangedEvent;
import me.ryanhamshire.GriefPrevention.util.TextMode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class TrustHandler {

    public static void trustRequest(Player player, ClaimPermission permissionLevel, String recipientName) {
        // Determine which claim the player is standing in
        Claim claim = GriefPrevention.getInstance().dataStore.getClaimAt(player.getLocation(), true /*ignore height*/, null);

        // Validate player or group argument
        String permission = null;
        OfflinePlayer otherPlayer = null;
        UUID recipientID = null;
        if(recipientName.startsWith("[") && recipientName.endsWith("]")) {
            permission = recipientName.substring(1, recipientName.length() - 1);
            if(permission == null || permission.isEmpty()) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.InvalidPermissionID);
                return;
            }
        }else{
            otherPlayer = GriefPrevention.getInstance().resolvePlayerByName(recipientName);
            boolean isPermissionFormat = recipientName.contains(".");
            if(otherPlayer == null && !recipientName.equals("public") && !recipientName.equals("all") && !isPermissionFormat) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.PlayerNotFound2);
                return;
            }

            if(otherPlayer == null && isPermissionFormat) {
                // Player does not exist and argument has a period so this is a permission instead
                permission = recipientName;
            }else if(otherPlayer != null) {
                recipientName = otherPlayer.getName();
                recipientID = otherPlayer.getUniqueId();
            }else{
                recipientName = "public";
            }
        }

        // Determine which claims should be modified
        ArrayList<Claim> targetClaims = new ArrayList<>();
        if(claim == null) {
            PlayerData playerData = GriefPrevention.getInstance().dataStore.getPlayerData(player.getUniqueId());
            targetClaims.addAll(playerData.getClaims());
        }else{
            // Check permission here
            if(claim.checkPermission(player, ClaimPermission.Manage, null) != null) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.NoPermissionTrust, claim.getOwnerName());
                return;
            }

            // See if the player has the level of permission he's trying to grant
            Supplier<String> errorMessage;

            // Permission level null indicates granting permission trust
            if(permissionLevel == null) {
                errorMessage = claim.checkPermission(player, ClaimPermission.Edit, null);
                if(errorMessage != null) {
                    errorMessage = () -> "Only " + claim.getOwnerName() + " can grant /PermissionTrust here.";
                }
            }

            // Otherwise just use the ClaimPermission enum values
            else{
                errorMessage = claim.checkPermission(player, permissionLevel, null);
            }

            // Error message for trying to grant a permission the player doesn't have
            if(errorMessage != null) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.CantGrantThatPermission);
                return;
            }

            targetClaims.add(claim);
        }

        // If we didn't determine which claims to modify, tell the player to be specific
        if(targetClaims.size() == 0) {
            GriefPrevention.sendMessage(player, TextMode.Err, Messages.GrantPermissionNoClaim);
            return;
        }

        String identifierToAdd = recipientName;
        if(permission != null) {
            identifierToAdd = "[" + permission + "]";
            // Replace recipientName as well so the success message clearly signals a permission
            recipientName = identifierToAdd;
        }else if(recipientID != null) {
            identifierToAdd = recipientID.toString();
        }

        // Calling the event
        TrustChangedEvent event = new TrustChangedEvent(player, targetClaims, permissionLevel, true, identifierToAdd);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            return;
        }

        // Apply changes
        for(Claim currentClaim : event.getClaims()) {
            if(permissionLevel == null) {
                if(!currentClaim.managers.contains(identifierToAdd)) {
                    currentClaim.managers.add(identifierToAdd);
                }
            }else{
                currentClaim.setPermission(identifierToAdd, permissionLevel);
            }
            GriefPrevention.getInstance().dataStore.saveClaim(currentClaim);
        }

        // Notify player
        String permissionDescription;
        if(recipientName.equals("public")) {
            recipientName = GriefPrevention.getInstance().dataStore.getMessage(Messages.CollectivePublic);
        }
        if(permissionLevel == null) {
            permissionDescription = GriefPrevention.getInstance().dataStore.getMessage(Messages.PermissionsPermission);
        }else if(permissionLevel == ClaimPermission.Build) {
            permissionDescription = GriefPrevention.getInstance().dataStore.getMessage(Messages.BuildPermission);
        }else if(permissionLevel == ClaimPermission.Access) {
            permissionDescription = GriefPrevention.getInstance().dataStore.getMessage(Messages.AccessPermission);
        }else{
            // ClaimPermission.Inventory
            permissionDescription = GriefPrevention.getInstance().dataStore.getMessage(Messages.ContainersPermission);
        }

        String location;
        if(claim == null) {
            location = GriefPrevention.getInstance().dataStore.getMessage(Messages.LocationAllClaims);
        }else{
            location = GriefPrevention.getInstance().dataStore.getMessage(Messages.LocationCurrentClaim);
        }

        GriefPrevention.sendMessage(player, TextMode.Success, Messages.GrantPermissionConfirmation, recipientName, permissionDescription, location);
    }

    public static void unTrustRequest(Player player, String recipientName) {
        //determine which claim the player is standing in
        Claim claim = GriefPrevention.getInstance().dataStore.getClaimAt(player.getLocation(), true /*ignore height*/, null);

        //validate player or group argument
        String permission = null;
        OfflinePlayer otherPlayer = null;
        UUID recipientID = null;
        if(recipientName.startsWith("[") && recipientName.endsWith("]")) {
            permission = recipientName.substring(1, recipientName.length() - 1);
            if(permission == null || permission.isEmpty()) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.InvalidPermissionID);
                return;
            }
        }else{
            otherPlayer = GriefPrevention.getInstance().resolvePlayerByName(recipientName);
            boolean isPermissionFormat = recipientName.contains(".");
            if(otherPlayer == null && !recipientName.equals("public") && !recipientName.equals("all") && !isPermissionFormat) {
                GriefPrevention.sendMessage(player, TextMode.Err, Messages.PlayerNotFound2);
                return;
            }

            if(otherPlayer == null && isPermissionFormat) {
                //player does not exist and argument has a period so this is a permission instead
                permission = recipientName;
            }else if(otherPlayer != null) {
                recipientName = otherPlayer.getName();
                recipientID = otherPlayer.getUniqueId();
            }else{
                recipientName = "public";
            }
        }

        //if no claim here, apply changes to all his claims
        if(claim == null) {
            PlayerData playerData = GriefPrevention.getInstance().dataStore.getPlayerData(player.getUniqueId());

            String idToDrop = otherPlayer.getName();
            if(otherPlayer != null) {
                idToDrop = otherPlayer.getUniqueId().toString();
            }

            //calling event
            TrustChangedEvent event = new TrustChangedEvent(player, playerData.getClaims(), null, false, idToDrop);
            Bukkit.getPluginManager().callEvent(event);

            //dropping permissions
            for(Claim targetClaim : event.getClaims()) {
                claim = targetClaim;

                //if untrusting "all" drop all permissions
                if(permission == null) {
                    claim.clearPermissions();
                }

                //otherwise drop individual permissions
                else{
                    claim.dropPermission(idToDrop);
                    claim.managers.remove(idToDrop);
                }

                //save changes
                GriefPrevention.getInstance().dataStore.saveClaim(claim);
            }

            //beautify for output
            if(idToDrop.equals("public")) {
                idToDrop = "the public";
            }

            //confirmation message
            if(permission != null) {
                GriefPrevention.sendMessage(player, TextMode.Success, Messages.UntrustIndividualAllClaims, idToDrop);
            }else{
                GriefPrevention.sendMessage(player, TextMode.Success, Messages.UntrustEveryoneAllClaims);
            }
        }

        //otherwise, apply changes to only this claim
        else if(claim.checkPermission(player, ClaimPermission.Manage, null) != null) {
            GriefPrevention.sendMessage(player, TextMode.Err, Messages.NoPermissionTrust, claim.getOwnerName());
        }else{
            //if clearing all
            if(permission == null) {
                //requires owner
                if(claim.checkPermission(player, ClaimPermission.Edit, null) != null) {
                    GriefPrevention.sendMessage(player, TextMode.Err, Messages.UntrustAllOwnerOnly);
                }

                //calling the event
                TrustChangedEvent event = new TrustChangedEvent(player, claim, null, false, otherPlayer.getName());
                Bukkit.getPluginManager().callEvent(event);


                event.getClaims().forEach(Claim::clearPermissions);
                GriefPrevention.sendMessage(player, TextMode.Success, Messages.ClearPermissionsOneClaim);
            }

            //otherwise individual permission drop
            else{
                String idToDrop = otherPlayer.getName();
                if(otherPlayer != null) {
                    idToDrop = otherPlayer.getUniqueId().toString();
                }
                boolean targetIsManager = claim.managers.contains(idToDrop);
                if(targetIsManager && claim.checkPermission(player, ClaimPermission.Edit, null) != null)  //only claim owners can untrust managers
                {
                    GriefPrevention.sendMessage(player, TextMode.Err, Messages.ManagersDontUntrustManagers, claim.getOwnerName());
                }else{
                    //calling the event
                    TrustChangedEvent event = new TrustChangedEvent(player, claim, null, false, idToDrop);
                    Bukkit.getPluginManager().callEvent(event);

                    event.getClaims().forEach(targetClaim -> targetClaim.dropPermission(event.getIdentifier()));

                    //beautify for output
                    if(idToDrop.equals("public")) {
                        idToDrop = "the public";
                    }

                    GriefPrevention.sendMessage(player, TextMode.Success, Messages.UntrustIndividualSingleClaim, idToDrop);
                }
            }

            //save changes
            GriefPrevention.getInstance().dataStore.saveClaim(claim);
        }
    }
}
