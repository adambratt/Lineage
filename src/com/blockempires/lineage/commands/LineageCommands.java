package com.blockempires.lineage.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blockempires.lineage.LineageArea;
import com.blockempires.lineage.LineagePlugin;
import com.blockempires.lineage.LineageUtil;

public class LineageCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			if(!sender.isOp()){
				//Permissions
			}
		}else{
			// Need to be a player to use this, server console can't do tutorials!
			return false;
		}
		Player player=(Player) sender;
		LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
		if(currentArea==null){
			player.sendMessage(ChatColor.RED+"You are not in the Lineage Area!");
			return false;
		}
		if(args.length==1){
			if(args[0].equalsIgnoreCase("yes")){
				LineageArea nextArea=currentArea.getNextWarp();
				if(nextArea==null){
					player.sendMessage(ChatColor.RED+"There's no reason for you to agree right now!");
					return false;
				}
				Location nextLoc=nextArea.getSpawn();
				if(nextLoc==null){
					player.sendMessage(ChatColor.RED+"Looks like there's no where for you to go next!");
					return false;
				}
				LineagePlugin.getAreaManager().stopPlayerMessage(player);
				player.teleport(LineageUtil.getSafeDestination(nextLoc));
				nextArea.sendMessages(player);		
				return true;
			}
			if(args[0].equalsIgnoreCase("repeat")){
				LineagePlugin.getAreaManager().stopPlayerMessage(player);
				currentArea.sendMessages(player);	
				return true;
				
			}
		}
		return false;
	}

}
