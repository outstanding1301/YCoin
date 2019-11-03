package com.yeomryo.stock.Player;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.yeomryo.stock.Main;
import com.yeomryo.stock.Stock.Stock;
import com.yeomryo.stock.utils.LinmaluYamlConfiguration;
import com.yeomryo.stock.utils.YConfig;

public class PlayerData {
	private String player;
	private HashMap<Stock, Integer> stock = new HashMap<>();

	public PlayerData(Player p) {
		this.player = p.getName();
		stock = new HashMap<>();
	}

	public PlayerData(String s) {
		this.player = s;
		stock = new HashMap<>();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	public void setPlayer(Player player) {
		this.player = player.getName();
	}

	public HashMap<Stock, Integer> getStock() {
		return stock;
	}

	public int getAmount(Stock stock) {
		if(this.stock.containsKey(stock))
			return this.stock.get(stock);
		else
			return 0;
	}
	public static ItemStack getICON(Material material, String name){
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}
	public static ItemStack getICON(Material material, String name, String... lore){
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		LinkedList<String> l = new LinkedList<>();
		for(String s : lore)
			l.add(s);
		im.setLore(l);
		is.setItemMeta(im);
		return is;
	}
	public void refresh(){
		if(player != null){
			if(getPlayer() != null){
				if(getPlayer().isOnline()){
					InventoryView ivv = getPlayer().getOpenInventory();
					if(ivv != null){
						Inventory iv = ivv.getTopInventory();
						if(iv != null){
							if(iv.getName().equalsIgnoreCase("코인 거래소")){
								iv.clear();
								for(Stock s : Main.StockList){
									ItemStack is = new ItemStack(Material.PAPER);
									ItemMeta im = is.getItemMeta();
									im.setDisplayName("§a[§f "+ChatColor.BOLD+s.getName()+" §a]");
									LinkedList<String> ll = new LinkedList<>();
									ll.add(s.getDescription());
									ll.add("§a◈§f 현재 가격 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue())+"§f원");
									ll.add(" ");
									ll.add("§3◈§f 변동값 : §6"+s.getMinPrice()+"§f원 ~ §6"+s.getMaxPrice()+"§f원");
									ll.add(" ");
									ll.add("§c◈§f 최근 변동 : "+s.getChange());
									ll.add(" ");
									if(stock.containsKey(s)){
										ll.add("§e◈§f 보유 개수 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(stock.get(s))+"§f개");
										ll.add(" ");
										ll.add("§6◈§f 총 가치 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue()*stock.get(s))+"§f원");
									}else
										ll.add("§e◈§f 보유 개수 : §a0§f개");
									ll.add(" ");
									ll.add("§9∴§e LEFT CLICK : §b1§f개 구매   §e RIGHT CLICK : §b1§f개 판매 §9∴");
									ll.add("§9∴§6 SHIFT+LEFT : §310§f개 구매  §6SHIFT+RIGHT : §310§f개 판매 §9∴");
									im.setLore(ll);
									is.setItemMeta(im);
									iv.setItem(iv.firstEmpty(), is);
								}
								ItemStack is2 = new ItemStack(Material.ARROW);
								ItemMeta im = is2.getItemMeta();
								im.setDisplayName(ChatColor.AQUA+"닫기");
								is2.setItemMeta(im);
								iv.setItem(44,is2);
								ItemStack iss = getICON(Material.EMERALD, ChatColor.BOLD+"§b[ §f보유 금액 §b]", "§a◈ "+NumberFormat.getIntegerInstance().format(getMoney())+"§f원");
								iss.setTypeId(397);
								iss.setDurability((short)3);
								iv.setItem(36, iss);
							}else if(iv.getName().equalsIgnoreCase("코인지갑")){
								iv.clear();
								for(Stock s : stock.keySet()){
									if(stock.get(s)>0){
										ItemStack is = new ItemStack(Material.PAPER);
										ItemMeta im = is.getItemMeta();
										im.setDisplayName("§a[§f "+ChatColor.BOLD+s.getName()+" §a]");
										LinkedList<String> ll = new LinkedList<>();
										ll.add(s.getDescription());
										ll.add("§a◈§f 현재 가격 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue())+"원");
										ll.add(" ");
										ll.add("§3◈§f 변동값 : §6"+s.getMinPrice()+"§f원 ~ §6"+s.getMaxPrice()+"§f원");
										ll.add(" ");
										ll.add("§c◈§f 최근 변동 : "+s.getChange());
										ll.add(" ");
										ll.add("§e◈§f 보유 개수 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(stock.get(s))+"§f개");
										ll.add(" ");
										ll.add("§6◈§f 총 가치 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue()*stock.get(s))+"§f원");
										im.setLore(ll);
										is.setItemMeta(im);
										iv.setItem(iv.firstEmpty(), is);
									}
								}
								ItemStack is2 = new ItemStack(Material.ARROW);
								ItemMeta im = is2.getItemMeta();
								im.setDisplayName(ChatColor.AQUA+"닫기");
								is2.setItemMeta(im);
								ItemStack iss = getICON(Material.EMERALD, ChatColor.BOLD+"§b[ §f보유 금액 §b]", "§a◈ "+NumberFormat.getIntegerInstance().format(getMoney())+"§f원");
								iss.setTypeId(397);
								iss.setDurability((short)3);
								iv.setItem(36, iss);								
								iv.setItem(44,is2);
							}
						}
					}
				}
			}
		}
	}
	public boolean buyStock(Stock stock, int amount) {
		if(stock.getValue()*amount <= getMoney()){
			int have = 0;
			if(this.stock.containsKey(stock))
				have = this.stock.get(stock);
			if(stock.getAmount() >= amount+have){
				if(this.stock.containsKey(stock))
					this.stock.put(stock, this.stock.get(stock)+amount);
				else
					this.stock.put(stock, amount);
				removeMoney(stock.getValue()*amount);
				getPlayer().sendMessage(Main.pf+ChatColor.GREEN+stock.getName()+" §6"+amount+"개§f를 매입했습니다! (현재 수량 : §e"+this.stock.get(stock)+"§f개)");
				save();
				return true;
			}else{
				getPlayer().sendMessage(Main.pf+ChatColor.RED+"최대 보유 수량을 초과했습니다.");
			}
		}else{
			getPlayer().sendMessage(Main.pf+ChatColor.RED+"소지금이 부족합니다.");
		}
		return false;
	}
	public int sellStock(Stock stock, int amount) {
		if(this.stock.containsKey(stock)){
			if(this.stock.containsKey(stock)){
				if(this.stock.get(stock) > amount){
					this.stock.put(stock, this.stock.get(stock)-amount);
					addMoney(stock.getValue()*amount);
					getPlayer().sendMessage(Main.pf+ChatColor.GREEN+stock.getName()+" §6"+amount+"개§f를 매각했습니다! (현재 수량 : §e"+this.stock.get(stock)+"§f개)");
					return 1;
				}else if(this.stock.get(stock) == amount){
					this.stock.remove(stock);
					addMoney(stock.getValue()*amount);
					getPlayer().sendMessage(Main.pf+ChatColor.GREEN+stock.getName()+" §6"+amount+"개§f를 매각했습니다! (현재 수량 : §e"+0+"§f개)");
					return 0;
				}else{
					getPlayer().sendMessage(Main.pf+ChatColor.RED+"그만한 코인을 보유하고 있지 않습니다.");
				}
			}else{
				getPlayer().sendMessage(Main.pf+ChatColor.RED+"해당 코인을 보유하고 있지 않습니다.");
			}
		}
		return -1;
	}
	public void showMyStock(Player p){
		Inventory iv = Bukkit.createInventory(getPlayer(), 5*9, "코인지갑");
		for(Stock s : stock.keySet()){
			if(stock.get(s)>0){
				ItemStack is = new ItemStack(Material.PAPER);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("§a[§f "+ChatColor.BOLD+s.getName()+" §a]");
				LinkedList<String> ll = new LinkedList<>();
				ll.add(s.getDescription());
				ll.add("§a◈§f 현재 가격 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue())+"원");
				ll.add(" ");
				ll.add("§3◈§f 변동값 : §6"+s.getMinPrice()+"§f원 ~ §6"+s.getMaxPrice()+"§f원");
				ll.add(" ");
				ll.add("§c◈§f 최근 변동 : "+s.getChange());
				ll.add(" ");
				ll.add("§e◈§f 보유 개수 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(stock.get(s))+"§f개");
				ll.add(" ");
				ll.add("§6◈§f 총 가치 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue()*stock.get(s))+"§f원");
				im.setLore(ll);
				is.setItemMeta(im);
				iv.setItem(iv.firstEmpty(), is);
			}
		}
		ItemStack is2 = new ItemStack(Material.ARROW);
		ItemMeta im = is2.getItemMeta();
		im.setDisplayName(ChatColor.AQUA+"닫기");
		is2.setItemMeta(im);
		iv.setItem(44,is2);								
		ItemStack iss = getICON(Material.EMERALD, ChatColor.BOLD+"§b[ §f보유 금액 §b]", "§a◈ "+NumberFormat.getIntegerInstance().format(getMoney())+"§f원");
		iss.setTypeId(397);
		iss.setDurability((short)3);
		iv.setItem(36, iss);
		p.openInventory(iv);

	}
	public void showAllStock(){
		Inventory iv = Bukkit.createInventory(getPlayer(), 9*5, "코인 거래소");
		for(Stock s : Main.StockList){
			ItemStack is = new ItemStack(Material.PAPER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§a[§f "+ChatColor.BOLD+s.getName()+" §a]");
			LinkedList<String> ll = new LinkedList<>();
			ll.add(s.getDescription());
			ll.add("§a◈§f 현재 가격 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue())+"§f원");
			ll.add(" ");
			ll.add("§3◈§f 변동값 : §6"+s.getMinPrice()+"§f원 ~ §6"+s.getMaxPrice()+"§f원");
			ll.add(" ");
			ll.add("§c◈§f 최근 변동 : "+s.getChange());
			ll.add(" ");
			if(stock.containsKey(s)){
				ll.add("§e◈§f 보유 개수 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(stock.get(s))+"§f개");
				ll.add(" ");
				ll.add("§6◈§f 총 가치 : "+ChatColor.GREEN+NumberFormat.getIntegerInstance().format(s.getValue()*stock.get(s))+"§f원");
			}else
				ll.add("§e◈§f 보유 개수: §a0§f 개");
			ll.add(" ");
			ll.add("§9∴§e LEFT CLICK : §b1§f개 구매   §e RIGHT CLICK : §b1§f개 판매 §9∴");
			ll.add("§9∴§6 SHIFT+LEFT : §310§f개 구매  §6SHIFT+RIGHT : §310§f개 판매 §9∴");
			im.setLore(ll);
			is.setItemMeta(im);
			iv.setItem(iv.firstEmpty(), is);
		}
		ItemStack is2 = new ItemStack(Material.ARROW);
		ItemMeta im = is2.getItemMeta();
		im.setDisplayName(ChatColor.AQUA+"닫기");
		is2.setItemMeta(im);
		iv.setItem(44,is2);		
		ItemStack iss = getICON(Material.EMERALD, ChatColor.BOLD+"§b[ §f보유 금액 §b]", "§a◈ "+NumberFormat.getIntegerInstance().format(getMoney())+"§f원");
		iss.setTypeId(397);
		iss.setDurability((short)3);
		iv.setItem(36, iss);
		getPlayer().openInventory(iv);

	}
	public void showWallet(){
		getPlayer().sendMessage(Main.pf+ChatColor.YELLOW+"현재 소지금 : "+NumberFormat.getIntegerInstance().format(getMoney())+"원");
	}

	public int getMoney(){
		return (int)Main.economy.getBalance(player);
	}
	public void addMoney(double d){
		Main.economy.depositPlayer(player,d);
	}
	public void removeMoney(double d){
		Main.economy.withdrawPlayer(player,d);
	}
	
	public void save(){
		LinmaluYamlConfiguration yc = YConfig.getYMLforsave(Main.dataFolder, player+".yml");
		yc.set("이름", player);
		List<String> keys = new ArrayList<>();
		for(Stock s : stock.keySet()){
			keys.add(s.getName());
			yc.set(s.getName()+".보유량", stock.get(s));
		}
		yc.set("보유코인", keys);
		LinmaluYamlConfiguration list = YConfig.getYML(Main.dataFolder, "_players.yml");
		List<String> players = list.getStringList("players");
		if(players==null)
			players=new ArrayList<>();
		if(!players.contains(player))
			players.add(player);
		list.set("players", players);
		try {
			yc.save(new File(Main.dataFolder, player+".yml"));
			list.save(new File(Main.dataFolder, "_players.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
