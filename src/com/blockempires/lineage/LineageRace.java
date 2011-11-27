package com.blockempires.lineage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LineageRace {

	private String name;
	private Location spawn;
	private Location button;
	private List<ItemStack> itemList=new ArrayList<ItemStack>();
	private int money=0;

	public LineageRace(String raceName) {
		this.name=raceName;
	}

	public void setSpawn(Location eyeLocation) {
		this.spawn=eyeLocation;		
	}
	
	public Location getSpawn(){
		return this.spawn;
	}

	public void setButton(Block block) {
		this.button=block.getLocation();
	}
	
	public void setButton(Location loc){
		this.button=loc;
	}

	public void addItem(ItemStack item) {
		this.itemList.add(item);
	}
	
	public void addItem(int id, int amount){
		this.itemList.add(new ItemStack(id,amount));
	}
	
	public List<ItemStack> getItems(){
		return this.itemList;
	}
	
	public void giveItems(Player player){
		for(int i=0; i<itemList.size(); i++){
			player.getInventory().addItem(itemList.get(i));
		}
	}

	public void setMoney(int amount) {
		this.money=amount;
	}
	
	public int getMoney(){
		return this.money;
	}

	public String getName() {
		return this.name;
	}

	public Location getButton() {
		return this.button;
	}

}
