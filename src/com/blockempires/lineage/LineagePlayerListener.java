package com.blockempires.lineage;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;


public class LineagePlayerListener extends PlayerListener {
	
	private LineagePlugin plugin;

	public LineagePlayerListener(LineagePlugin pluginRef) {
		// Initialize listener
		this.plugin=pluginRef;
	}

	public void onPlayerJoin(PlayerJoinEvent event){
		Player joinPlayer=event.getPlayer();
		if(joinPlayer.hasPermission("lineage.hasrace")){
			return;
		}
		// Let's check to see if we have a spawn area setup
		LineageArea spawn=LineagePlugin.getAreaManager().getSpawn();
		if(spawn==null) return; // No spawn exists, no point in trying to move them through tutorial
	
		// If they aren't in the area bring them over to the spawn
		if(!LineagePlugin.getAreaManager().inRegion(joinPlayer, spawn.getArea())){
			joinPlayer.teleport(LineageUtil.getSafeDestination(spawn.getSpawn()));
			joinPlayer.sendMessage("Taking "+joinPlayer.getName()+" to walkthrough intro....");
		}
		// Now they are here so it's tutorial time
		plugin.serverlog("Starting spawn tutorial for "+joinPlayer.getName());
		spawn.sendMessages(joinPlayer);		
		return;
	}
	
    public void onPlayerInteract (PlayerInteractEvent event) {
        //Checks if Action was clicking a Block
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if (block.getType().equals(Material.STONE_BUTTON)) {
            	//Check if this button is linked to a race
            	if(!LineagePlugin.getRaceManager().buttonExists(block.getLocation())) return;
            	//If has race, tell them to stop cheating
            	if(player.hasPermission("lineage.hasrace")){
            		player.sendMessage(ChatColor.RED+"You already have a race. If you want to change races, this isn't the way to do it.");
            		return;
            	}
                //If no class, set class and go to spawn
            	LineagePlugin.getRaceManager().raceButtonPress(player,block.getLocation());
            }
        }
    }
	
	/*
	public void onPlayerTeleport(PlayerTeleportEvent event){
		Player player=event.getPlayer();
		if(plugin.permManage.has(player, "Lineage.race")){
			return;
		}
		//Alright, so if they aren't spawning in the 
	}
	*/
	
	
}
