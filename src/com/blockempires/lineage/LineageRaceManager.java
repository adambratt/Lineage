package com.blockempires.lineage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.ConfigurationNode;

import com.iConomy.iConomy;
import com.iConomy.system.Account;




public class LineageRaceManager {
	private LineagePlugin plugin;
	private List<LineageRace> raceList=new ArrayList<LineageRace>();

	public LineageRaceManager(LineagePlugin pluginRef) {
		plugin=pluginRef;
		loadRaceConfig();
	}
	
	public void reloadRaceConfig(){
		raceList=new ArrayList<LineageRace>();
		this.loadRaceConfig();
	}
	

	private void loadRaceConfig() {
		//Loop through the races
		ConfigurationNode races=plugin.config.getNode("races");
		if(races==null){
			//Need some error handling/messaging
			return;
		}
		for(String raceName : races.getKeys()){
			if(raceName==null) continue;
			ConfigurationNode raceNode=plugin.config.getNode("races."+raceName);
			LineageRace race=new LineageRace(raceName);
			race.setMoney(raceNode.getInt("money", 0));
			String worldName=raceNode.getString("button.world","");
			World world=Bukkit.getServer().getWorld(worldName);
			if(world!=null){
				race.setButton(new Location(world, raceNode.getDouble("button.x",0),raceNode.getDouble("button.y",0),raceNode.getDouble("button.z",0)));
			}
			worldName=raceNode.getString("warp.world","");
			world=Bukkit.getServer().getWorld(worldName);
			if(world!=null){
				race.setSpawn(new Location(world, raceNode.getDouble("warp.x",0),raceNode.getDouble("warp.y",0),raceNode.getDouble("warp.z",0),new Float(raceNode.getDouble("warp.yaw",0)), new Float(raceNode.getDouble("warp.pitch",0))));
			}
			ConfigurationNode itemNode=raceNode.getNode("items");
			if(itemNode!=null){
				for(String itemid : itemNode.getKeys()){
					race.addItem(Integer.parseInt(itemid),raceNode.getInt("items."+itemid, 1));
				}
			}
			raceList.add(race);			
		}
	}


	public void saveRaceConfig() {
		plugin.config.removeProperty("races");
		for(LineageRace race : raceList){
			if(race.getName()==null) continue;
			String prefix="races."+race.getName();
			plugin.config.setProperty(prefix+".money", race.getMoney());
			if(race.getSpawn()!=null){
				Location loc=race.getSpawn();
				plugin.config.setProperty(prefix+".warp.x", loc.getX());
				plugin.config.setProperty(prefix+".warp.y", loc.getY());
				plugin.config.setProperty(prefix+".warp.z", loc.getZ());
				plugin.config.setProperty(prefix+".warp.pitch", loc.getPitch());
				plugin.config.setProperty(prefix+".warp.yaw", loc.getYaw());
				plugin.config.setProperty(prefix+".warp.world", loc.getWorld().getName());
			}
			if(race.getButton()!=null){
				Location loc=race.getButton();
				plugin.config.setProperty(prefix+".button.x", loc.getX());
				plugin.config.setProperty(prefix+".button.y", loc.getY());
				plugin.config.setProperty(prefix+".button.z", loc.getZ());
				plugin.config.setProperty(prefix+".button.world", loc.getWorld().getName());
			}
			for(ItemStack item : race.getItems()){
				plugin.config.setProperty(prefix+".items."+item.getTypeId(),item.getAmount());
			}
		}
		plugin.config.save();
	}


	public boolean buttonExists(Location block) {
		for(LineageRace race : raceList){
			if(block.equals(race.getButton())) return true;
		}
		return false;
	}
	
	public LineageRace buttonRace(Location loc){
		for(LineageRace race : raceList){
			if(loc.equals(race.getButton())) return race;
		}
		return null;
	}

	public LineageRace getRace(String string) {
		for(LineageRace race : raceList){
			if(string.equals(race.getName())) return race;
		}
		return null;
	}


	public boolean raceExists(String string) {
		for(LineageRace race : raceList){
			if(string.equals(race.getName())) return true;
		}
		return false;
	}


	public LineageRace addRace(String string) {
		if(raceExists(string)) return null;
		Permission perm=plugin.getServer().getPluginManager().getPermission("lineage.race."+string);
		if(perm==null) plugin.getServer().getPluginManager().addPermission(new Permission("lineage.race."+string,PermissionDefault.FALSE));
		LineageRace race=new LineageRace(string);
		raceList.add(race);
		return race;
	}


	public void raceButtonPress(Player player, Location location) {
		LineageRace race=buttonRace(location);
		//Add Money
		Account account=iConomy.getAccount(player.getName());
		if(account!=null) account.getHoldings().add(race.getMoney());
		//Add Items
		race.giveItems(player);
		//Here is where we add them to the group....
		LineagePlugin.getPermissions().getUser(player).addGroup(race.getName());
		//Teleport them to new Spawn
		player.teleport(LineageUtil.getSafeDestination(race.getSpawn()));
	}
}
