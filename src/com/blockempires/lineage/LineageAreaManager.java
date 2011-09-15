package com.blockempires.lineage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class LineageAreaManager {
	private LineagePlugin plugin;
	private LineageArea spawnArea;
	private String spawnName;
	private List<LineageArea> areaList=new ArrayList<LineageArea>();

	public LineageAreaManager(LineagePlugin pluginRef) {
		plugin=pluginRef;
		loadAreasConfig();
	}
	
	// SpawnArea should be a list with each world being a key. Multiple spawns in multiple worlds ftw.
	public LineageArea getSpawn(){
		return spawnArea;
	}
	
	public void setSpawn(LineageArea spawn){
		String aname=spawn.getArea();
		if(aname==null) return;
		spawnArea=spawn;
		spawnName=aname;
	}
	
	public void reloadAreasConfig(){
		areaList=new ArrayList<LineageArea>();
		spawnName="";
		spawnArea=null;
		this.loadAreasConfig();
	}
	
	public void loadAreasConfig(){
		spawnName=plugin.config.getString("spawn.area");
		ConfigurationNode areas=plugin.config.getNode("areas");
		if(areas==null){
			//Need some error handling/messaging
			return;
		}
		// Loop through areas in configuration file
		for(String area : areas.getKeys()){
			// Nothing found so skip area, this shouldn't happen
			if(area==null) continue;
			// Check for world
			String worldName=areas.getString(area+".world");
			World world=plugin.getServer().getWorld(worldName);
			if(world==null) continue;
			//Check for region
			if(!LineagePlugin.regionExists(area,world)) continue;
			// So far so good, create the region
			LineageArea areaObj=new LineageArea(area,world);
			//Set help
			areaObj.setHelp(areas.getBoolean(area+".help",true));
			//Set chat prefix
			areaObj.setPrefix(areas.getString(area+".prefix"));
			//Set Area link
			areaObj.setNext(areas.getString(area+".next"));
			//Get NPC Messages
			List<ConfigurationNode> messages=areas.getNodeList(area+".messages",null);
			if(messages!=null){
				for(ConfigurationNode message : messages){
					areaObj.addMessage(message.getString("msg"),message.getInt("time",0));
				}
			}
			// Check that if set, spawn exists. Spawn needs to be last in order to add the whole spawnArea
			ConfigurationNode spawn=areas.getNode(area+".spawn");
			if(spawn!=null){
				// Set spawn area
				areaObj.setStringSpawn(spawn.getString("x"),spawn.getString("y"),spawn.getString("z"),spawn.getString("pitch"),spawn.getString("yaw"));
				// Check to see if this is spawn region
				if(area.equals(spawnName))	spawnArea=areaObj;
			}
			//Looks like we got through it! Time to add it to our list of areas
			areaList.add(areaObj);
		}
	}
	
	
	//@Todo: It should be areas.worldname.area for best results...i suppose
	public void saveAreasConfig(){
		//Clean out the areas from the config
		plugin.config.removeProperty("areas");
		if(!areaList.isEmpty()){
			plugin.config.setProperty("spawn.area", spawnName);
			for(LineageArea area : areaList){
				String areaName=area.getArea();
				if(areaName==null) continue;
				plugin.config.setProperty("areas."+areaName+".world", area.getWorldName());
				plugin.config.setProperty("areas."+areaName+".prefix", area.getPrefix());
				plugin.config.setProperty("areas."+areaName+".help", area.getHelp());
				plugin.config.setProperty("areas."+areaName+".next", area.getNext());
				Location spawn=area.getSpawn();
				if(spawn!=null){
					plugin.config.setProperty("areas."+areaName+".spawn.x", spawn.getX());
					plugin.config.setProperty("areas."+areaName+".spawn.y", spawn.getY());
					plugin.config.setProperty("areas."+areaName+".spawn.z", spawn.getZ());
					plugin.config.setProperty("areas."+areaName+".spawn.pitch", spawn.getPitch());
					plugin.config.setProperty("areas."+areaName+".spawn.yaw", spawn.getYaw());
				}
				//Convert Messages into a HashMap
				List<LineageAreaMessage> messages=area.getMessages();
				List<Map<String,Object>> msgList = new ArrayList<Map<String,Object>>(); 
				for(LineageAreaMessage message : messages){
					Map<String, Object> msgMap=new HashMap<String, Object>();
					msgMap.put("msg", message.getMsg());
					msgMap.put("time", message.getTime());
					msgList.add(msgMap);
				}
				//Puts map into the configuration for messages
				plugin.config.setProperty("areas."+areaName+".messages",msgList);
			}
		}
		plugin.config.save();
	}
	
	public void stopPlayerMessage(Player player){
		for(LineageArea area : areaList){
			if(area.queue==null || area.queue.isEmpty()) continue;
			int[] tasks=area.queue.get(player.getName());
			if(tasks!=null && tasks.length > 0){
				for(int i=0; i<tasks.length; i++){
					Bukkit.getServer().getScheduler().cancelTask(tasks[i]);
				}
			}
		}
	}
	
	//@Todo: Make multi-world compatible
	public boolean areaExists(String areaName, World world){
		for(LineageArea area : areaList){
			if(areaName.equals(area.getArea())) return true;
		}
		return false;
	}

	public void addArea(String areaName, World world) {
		if(areaExists(areaName, world)) return;
		LineageArea areaObj=new LineageArea(areaName, world);	
		areaList.add(areaObj);
	}
	
	public LineageArea getArea(String areaName, World world){
		if(areaName==null) return null;
		for(LineageArea area : areaList){
			if(areaName.equals(area.getArea())) return area;
		}
		return null;
	}
	
	public LineageArea getPlayerArea(Player player){
		for(LineageArea area : areaList){
			if(inRegion(player, area.getArea())) return area;
		}
		return null;
	}
	
	public boolean inRegion(Location loc, String regionName, World w){
		ProtectedRegion region = LineagePlugin.getRegion(regionName, w);
		if(region==null) return false;
		com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
		if (region.contains(v)) {
		     return true;
		}
		return false;
	}
	
	public boolean inRegion(Player player, String regionName){
		Location loc=player.getLocation();
		World w=player.getWorld();
		ProtectedRegion region = LineagePlugin.getRegion(regionName, w);
		if(region==null) return false;
		com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
		if (region.contains(v)) {
		     return true;
		}
		return false;
	}
	
	
}
