package com.blockempires.lineage.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.blockempires.lineage.LineageArea;
import com.blockempires.lineage.LineageAreaMessage;
import com.blockempires.lineage.LineagePlugin;
import com.blockempires.lineage.LineageRace;

public class AreaCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			if(!sender.isOp()){
				// Permissions
			}
		}else{
			// Need to be a player to use this, how else will we know which world to use!
			return false;
		}
		Player player=(Player) sender;
		//If this is dealing with the race side of things
		if(args.length>2 && args[0].equalsIgnoreCase("race")){
			if(args[1].equalsIgnoreCase("add")){
				if(args.length==3){
					if(LineagePlugin.getRaceManager().raceExists(args[2])){
						player.sendMessage(ChatColor.RED+"The race/group you entered already exists!");
						return false;
					}
					LineageRace newRace=LineagePlugin.getRaceManager().addRace(args[2]);
					if(newRace!=null){
						player.sendMessage(ChatColor.RED+args[2]+" was created!");
						return true;
					}
					player.sendMessage(ChatColor.RED+"The race you entered could not be created!");
					return false;
				}
				player.sendMessage(ChatColor.RED+"Invalid argument count!");
				return false;
			}
			if(!LineagePlugin.getRaceManager().raceExists(args[2])){
				player.sendMessage(ChatColor.RED+"The race/group you entered does not existed!");
				return false;
			}
			//Race exists so get it
			String raceName=args[2];
			LineageRace race=LineagePlugin.getRaceManager().getRace(raceName);
			if(args.length==3){
				if(args[1].equalsIgnoreCase("setbutton")){
					Block block = player.getTargetBlock(null, 100);
                    Material mat = block.getType();
                    if (!mat.equals(Material.STONE_BUTTON)) {
                        player.sendMessage(ChatColor.RED+"That's not a stone button you're looking at. Try again!");
                        return false;
                    }
                    if(LineagePlugin.getRaceManager().buttonExists(block.getLocation())){
                    	player.sendMessage(ChatColor.RED+"This button is already linked to a race!");
                    	return false;
                    }
                    race.setButton(block);
                    player.sendMessage(ChatColor.BLUE+"Linked button to: "+raceName);
                    return true;
				}
				if(args[1].equalsIgnoreCase("setwarp")){
					race.setSpawn(player.getEyeLocation());
					LineagePlugin.getRaceManager().saveRaceConfig();
					player.sendMessage(ChatColor.BLUE+"Warp set for '"+raceName+"'.");
					return true;
				}
			}
			if(args.length>=4){
				if(args[1].equalsIgnoreCase("additem")){
					int amount=1;
					if(args.length>4) amount=Integer.parseInt(args[4]);
					int type=Integer.parseInt(args[3]);
					ItemStack item=new ItemStack(type, amount);		
					race.addItem(item);
					LineagePlugin.getRaceManager().saveRaceConfig();
					player.sendMessage(ChatColor.BLUE+"Added Item for '"+raceName+"'.");
					return true;
				}
				if(args[1].equalsIgnoreCase("money")){
					int amount=Integer.parseInt(args[3]);
					race.setMoney(amount);
					LineagePlugin.getRaceManager().saveRaceConfig();
					player.sendMessage(ChatColor.BLUE+"Set money to "+amount+" for '"+raceName+"'.");
					return true;
				}
			}			
			return false;
		}
		//Must be the area side of things
		if(args.length==1){
			//Reload check first
			if(args[0].equalsIgnoreCase("reload")){
				LineagePlugin.getAreaManager().reloadAreasConfig();
				LineagePlugin.getRaceManager().reloadRaceConfig();
				player.sendMessage(ChatColor.GREEN+"Lineage Config Reloaded!");
				return true;
			}
			// Only 1 argument means we're editing an existing area
			LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
			if(currentArea==null){
				player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
				return false;
			}
			// Are we requesting a message list?
			if(args[0].equalsIgnoreCase("listmsg")){
				player.sendMessage(ChatColor.BLUE+"Message listing for '"+currentArea.getArea()+"':");
				int i=1;
				for(LineageAreaMessage msg : currentArea.getMessages()){
					player.sendMessage(ChatColor.GREEN+"["+i+"]"+ChatColor.BLUE+" "+msg.getTime()+" "+ChatColor.WHITE+msg.getMsg());
					i++;
				}
				return true;
			}
			// Are we setting a warp point?
			if(args[0].equalsIgnoreCase("setwarp")){
				currentArea.setSpawn(player.getEyeLocation());
				LineagePlugin.getAreaManager().saveAreasConfig();
				player.sendMessage(ChatColor.BLUE+"Warp set for '"+currentArea.getArea()+"'.");
				return true;
			}
			// Are we setting the spawn point?
			if(args[0].equalsIgnoreCase("setspawn")){
				currentArea.setSpawn(player.getEyeLocation());
				LineagePlugin.getAreaManager().setSpawn(currentArea);
				LineagePlugin.getAreaManager().saveAreasConfig();
				player.sendMessage(ChatColor.BLUE+"Spawn set in '"+currentArea.getArea()+"'.");
				return true;
			}
		}
		if(args.length==2){
			if(args[0].equalsIgnoreCase("area")){
				if(!LineagePlugin.regionExists(args[1], player.getWorld())){
					player.sendMessage(ChatColor.RED+"WorldGuard region '"+args[1]+"' does not exist in this world!");
					return false;
				}
				//Region exists so we can use it
				LineagePlugin.getAreaManager().addArea(args[1], player.getWorld());
				LineagePlugin.getAreaManager().saveAreasConfig();
				player.sendMessage(ChatColor.BLUE+"Lineage Area '"+args[1]+"' created.");
				return true;				
			}
			// Delete a message
			if(args[0].equalsIgnoreCase("delmsg")){
				LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
				if(currentArea==null){
					player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
					return false;
				}
				int number=new Integer(args[1]);
				if(currentArea.deleteMessage(number)){ 
					player.sendMessage(ChatColor.BLUE+"Message deleted from '"+currentArea.getArea()+"':");
					LineagePlugin.getAreaManager().saveAreasConfig();
					return true;
				}
				player.sendMessage(ChatColor.RED+"Invalid message number!");
				return false;
			}
			// Link to another area
			if(args[0].equalsIgnoreCase("linkto")){
				if(!LineagePlugin.getAreaManager().areaExists(args[1], player.getWorld())){
					player.sendMessage(ChatColor.RED+"A Lineage Area for '"+args[1]+"' does not exist!");
					return false;
				}
				LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
				if(currentArea==null){
					player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
					return false;
				}
				currentArea.setNext(args[1]);
				LineagePlugin.getAreaManager().saveAreasConfig();
				player.sendMessage(ChatColor.BLUE+"Linked this Lineage Area to '"+args[1]+"' successfully.");
				return true;
			}
		}
		//Sometimes the prefix will have a space or two in it...
		if(args.length>=2){
			// Change msg duration
			if(args[0].equalsIgnoreCase("time")){
				LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
				if(currentArea==null){
					player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
					return false;
				}
				int number=new Integer(args[1]);
				int ticks=new Integer(args[2]);
				if(currentArea.setMesssageDuration(number,ticks)){ 
					player.sendMessage(ChatColor.BLUE+"Message time changed from '"+currentArea.getArea()+"':");
					LineagePlugin.getAreaManager().saveAreasConfig();
					return true;
				}
				player.sendMessage(ChatColor.RED+"Invalid message number!");
				return false;
			}
			// Are we setting the prefix?
			if(args[0].equalsIgnoreCase("setprefix")){
				LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
				if(currentArea==null){
					player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
					return false;
				}
				String msg="";
				for(int i=0; i< (args.length-1); i++){
					msg+=" "+args[i+1];
				}
				currentArea.setPrefix(msg);
				LineagePlugin.getAreaManager().saveAreasConfig();
				player.sendMessage(ChatColor.BLUE+"Prefix set to "+args[1]+" in '"+currentArea.getArea()+"'.");
				return true;
			}
		}
		//Messages will always have lots of spaces in them....
		if(args.length>=3){
			if(args[0].equalsIgnoreCase("msg")){
				LineageArea currentArea=LineagePlugin.getAreaManager().getPlayerArea(player);
				if(currentArea==null){
					player.sendMessage(ChatColor.RED+"There is not a Lineage Area here!");
					return false;
				}
				String msg="";
				for(int i=0; i< (args.length-2); i++){
					msg+=" "+args[i+2];
				}
				//Make sure second term is int
				try{
					int duration=Integer.parseInt(args[1]);
					currentArea.addMessage(msg,duration);
					LineagePlugin.getAreaManager().saveAreasConfig();
					player.sendMessage(ChatColor.BLUE+"Lineage message added.");
					return true;
				}catch(NumberFormatException nfe){
					player.sendMessage(ChatColor.RED+"Invalid format. Use /lineager msg <duration> <msg>");
					return false;
				}
			}
		}
		return false;
	}

}
