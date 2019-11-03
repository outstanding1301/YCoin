package com.yeomryo.stock;

import java.text.NumberFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.yeomryo.stock.Player.PlayerData;
import com.yeomryo.stock.Stock.Stock;

public class EventManager implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(!Main.PlayerList.containsKey(e.getPlayer().getName())){
			PlayerData pd = new PlayerData(e.getPlayer());
			Main.PlayerList.put(e.getPlayer().getName(), pd);
			pd.save();
		}
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		Inventory iv = e.getInventory();
		ItemStack is = e.getCurrentItem();
		
		if(iv.getName().equalsIgnoreCase("코인 거래소")){
			Player p = (Player)iv.getHolder();
			PlayerData pd = Main.PlayerList.get(p.getName());
			
			if(is != null){
				if(is.getType() == Material.PAPER){
					if(is.hasItemMeta()){
						ItemMeta im = is.getItemMeta();
						List<String> ll = im.getLore();
						Stock s = null;
						for(Stock ss : Main.StockList){
							if(im.getDisplayName().contains(ss.getName())){
								s=ss;
							}
						}
						if(e.getClick() == ClickType.LEFT){
							if(s.getValue()<=0){
								p.sendMessage(Main.pf+ChatColor.RED+"가치가 0원인 암호화폐는 구매할 수 없습니다!");
							}else{
								pd.buyStock(s, 1);
								pd.refresh();
							}
						}else if(e.getClick() == ClickType.SHIFT_LEFT){
							if(s.getValue()<=0){
								p.sendMessage(Main.pf+ChatColor.RED+"가치가 0원인 암호화폐는 구매할 수 없습니다!");
							}else{
								pd.buyStock(s, 10);
								pd.refresh();
							}
						}else if(e.getClick() == ClickType.RIGHT){
							pd.sellStock(s, 1);
							pd.refresh();
							
						}else if(e.getClick() == ClickType.SHIFT_RIGHT){
							pd.sellStock(s, 10);
							pd.refresh();
							
						}
						e.setCancelled(true);
					}
				}
				if(is.getType() == Material.ARROW){
					if(is.hasItemMeta()){
						ItemMeta im = is.getItemMeta();
						if(im.getDisplayName().equals(ChatColor.AQUA+"닫기")){
							p.closeInventory();
							e.setCancelled(true);
						}
					}
				}
				e.setCancelled(true);
			}
		}
		
		if(iv.getName().equalsIgnoreCase("코인지갑")){
			Player p = (Player)iv.getHolder();
			if(is != null){
				if(is.getType() == Material.ARROW){
					if(is.hasItemMeta()){
						ItemMeta im = is.getItemMeta();
						if(im.getDisplayName().equals(ChatColor.AQUA+"닫기")){
							p.closeInventory();
							e.setCancelled(true);
						}
					}
				}else
					e.setCancelled(true);
				e.setCancelled(true);
			}
		}
	}
}
