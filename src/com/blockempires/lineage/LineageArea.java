package com.blockempires.lineage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


//@TODO: This should extend Location

public class LineageArea {
	
	private World world;
	private Location spawn;
	private String prefix;
	private List<LineageAreaMessage> messages=new ArrayList<LineageAreaMessage>();
	public Map<String,int[]> queue = new HashMap<String,int[]>(); 
	private String area;
	private String nextArea;
	private boolean showhelp=true;

	public LineageArea(String area, World world) {
		// TODO Auto-generated constructor stub
		this.world=world;
		this.area=area;
	}

	public void setStringSpawn(String x, String y, String z, String pitch, String yaw) {
		// TODO Auto-generated method stub
		double xpos=Double.parseDouble(x);
		double ypos=Double.parseDouble(y);
		double zpos=Double.parseDouble(z);
		float p=new Float(pitch);
		float ya=new Float(yaw);
		Location loc=new Location(this.world, xpos, ypos, zpos, p, ya);
		if(loc!=null) this.spawn=loc;
	}
	
	public void setSpawn(Location loc){
		if(loc!=null) this.spawn=loc;
	}
	
	public Location getSpawn(){
		return this.spawn;
	}
	
	public void setHelp(boolean help){
		this.showhelp=help;
	}
	
	public boolean getHelp(){
		return this.showhelp;
	}
	
	public LineageArea getNextWarp(){
		return LineagePlugin.getAreaManager().getArea(nextArea,world);
	}
	
	public String getNext(){
		return this.nextArea;
	}
	
	public void setNext(String string){
		if(string!=null) this.nextArea=string;
	}
	
	public String getWorldName(){
		if(world!=null) return world.getName();
		return null;
	}
	
	public String getArea(){
		return this.area;
	}

	public void setPrefix(String string) {
		if(string!=null) this.prefix=string;
	}
	
	public String getPrefix(){
		return prefix;
	}

	public void addMessage(String msg, int time) {
		LineageAreaMessage lam=new LineageAreaMessage(msg,time);
		messages.add(lam);		
	}
	
	public List<LineageAreaMessage> getMessages(){
		return messages;
	}

	public void sendMessages(Player player) {
		int[] tasks = new int[messages.size()];
		for(int i=0; i<messages.size(); i++){
			LineageAreaMessage message=messages.get(i);
			//If it's the last message, lets let it know
			LineageAutoMessage autoMessage=new LineageAutoMessage(this,message,player);
			if(i==messages.size()-1) autoMessage.last=true;
			tasks[i]=Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LineagePlugin.getInstance(),autoMessage,message.getTime());
		}
		queue.put(player.getName(), tasks);
	}

	public boolean deleteMessage(int number) {
		if(messages.size()<number) return false;
		messages.remove(number-1);
		return true;
	}
	
	public boolean setMesssageDuration(int msg, int ticks) {
		if(messages.size()<msg) return false;
		messages.get(msg-1).setDuration(ticks);
		return true;
	}

}
