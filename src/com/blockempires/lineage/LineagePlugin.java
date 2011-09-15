package com.blockempires.lineage;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.blockempires.lineage.commands.AreaCommands;
import com.blockempires.lineage.commands.LineageCommands;
import com.iConomy.iConomy;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class LineagePlugin extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private boolean eventsRegistered = false;
	private LineagePlayerListener playerListener;
	public static WorldGuardPlugin wgPlugin;
	private static iConomy ecoPlugin;
	private static LineageAreaManager areaManager;
	private static LineagePlugin instance;
	private static LineageRaceManager raceManager;
	private static PermissionManager permManager;
	public PluginManager pManage;
	public Configuration config;
	
	public static LineagePlugin getInstance(){
		return LineagePlugin.instance;
	}
	
	public void serverlog(String msg) {
		// Just a simple little function that will put "[lineage]" before putting into the log 
		// Use logger to output to console
        log.log(Level.INFO, "[Lineage] " + msg);
    }
	
	public void onEnable(){ 
		//Save our instance
		LineagePlugin.instance=this;
		// Here's where the code goes when we load up the plugin to the server
		serverlog("Registering Events...");
		registerEvents();
		serverlog("Registering Commands...");
		registerCommands();
		serverlog("Loading Lineage Areas and Races...");
		dependLoad();
		serverlog("Version " + getDescription().getVersion() + " is enabled!");
	}
	
	public void onDisable(){ 
		//Log that we've been shutdown
		serverlog("Plugin is disabled!");
	}
	 
	private void dependLoad() {
		//Load dependencies first
		if(pManage.isPluginEnabled("PermissionsEx")){
			permManager=PermissionsEx.getPermissionManager();
		}else{
			serverlog("PermissionsEx does not appear to be installed");
		}
		if(pManage.isPluginEnabled("iConomy")){
			Plugin eco=pManage.getPlugin("iConomy");
			if(eco instanceof iConomy){
				LineagePlugin.ecoPlugin=(iConomy) eco;
			}
		}else{
			serverlog("iConomy does not appear to be installed");
			//Need to die or something here
		}
		if(pManage.isPluginEnabled("WorldGuard")){
			Plugin wg=pManage.getPlugin("WorldGuard");
			if(wg instanceof WorldGuardPlugin){
				LineagePlugin.wgPlugin=(WorldGuardPlugin) wg;
			}
			//Need to add error handling if it doesn't load
		}else{
			serverlog("WorldGuard does not appear to be installed");
			//Need to die or something here
		}
		//Now Load Configuration
		config = getConfiguration();
		//Load Areas+Races
		LineagePlugin.areaManager=new LineageAreaManager(this);
		LineagePlugin.raceManager=new LineageRaceManager(this);
	}
	
	private void registerCommands(){
		//register commands ...
		try {
			getCommand("lineager").setExecutor(new AreaCommands());
			getCommand("lineage").setExecutor(new LineageCommands());
		} catch (Exception e) {
			serverlog("Error: Commands not definated in 'plugin.yaml'");
		}
	}
	
	private void registerEvents(){
		if(!eventsRegistered){
			playerListener=new LineagePlayerListener(this);
			pManage = getServer().getPluginManager();
			pManage.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.High, this);
			pManage.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
			//pManage.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.High, this);
		}
	}
	
	public static ProtectedRegion getRegion(String regionName, World world){
		ProtectedRegion region=LineagePlugin.getWorldGuard().getRegionManager(world).getRegion(regionName);
		if(region==null) return null;
		return region;
	}
	
	public static boolean regionExists(String regionName, World worldName){
		ProtectedRegion r=LineagePlugin.getRegion(regionName, worldName);
		if(r==null) return false;
		return true;
	}
	
	public static iConomy getEconomy(){
		return LineagePlugin.ecoPlugin;
	}
	
	public static WorldGuardPlugin getWorldGuard(){
		return LineagePlugin.wgPlugin;
	}

	public static LineageAreaManager getAreaManager() {
		return areaManager;
	}

	public static LineageRaceManager getRaceManager() {
		return raceManager;
	}
	
	public static PermissionManager getPermissions() {
		return permManager;
	}
}
