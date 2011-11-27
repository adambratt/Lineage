package com.blockempires.lineage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LineageAutoMessage implements Runnable {
	LineageArea area;
	LineageAreaMessage message;
	Player player;
	public boolean last=false;
	
	public LineageAutoMessage(LineageArea area,LineageAreaMessage message, Player player) {
		this.area=area;
		this.message=message;
		this.player=player;
	}

	@Override
	public void run() {
		String prefix="";
		if(area.getPrefix()!=null){
			prefix=ChatColor.GOLD+area.getPrefix()+" ";
		}
		player.sendMessage(prefix+ChatColor.AQUA+message.getMsg());
		if(last && area.getHelp()) 
			player.sendMessage(ChatColor.GRAY+"Type "+ChatColor.GREEN+"/ok"+ChatColor.GRAY+" if you would like to continue, if you would like to hear that again type"+ChatColor.LIGHT_PURPLE+" /repeat");
	}

}
