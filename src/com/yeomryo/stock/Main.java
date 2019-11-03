package com.yeomryo.stock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.yeomryo.stock.Player.PlayerData;
import com.yeomryo.stock.Stock.Stock;
import com.yeomryo.stock.Stock.StockTimer;
import com.yeomryo.stock.utils.LinmaluYamlConfiguration;
import com.yeomryo.stock.utils.YConfig;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	public static LinkedList<Stock> StockList = new LinkedList<>();
	public static HashMap<String, PlayerData> PlayerList = new HashMap<>();
	public static StockTimer timer = new StockTimer();
	public static Economy economy;
	public static File dataFolder;
	public static int changeTime = 60;
	public static String pf = " ≪ §6YCoin§f ≫ ";
	
	public void loadStock(){
		LinmaluYamlConfiguration yc = YConfig.getStockYML(dataFolder, "stock.yml");
		List<String> list = yc.getStringList("주식목록");
		changeTime = yc.getInt("변동시간");
		pf = yc.getString("접두사").replaceAll("&", "§");
		StockList.clear();
		for(String key : list){
			String description;
			int value;
			int fvalue;
			description = yc.getString(key+".설명");
			fvalue = yc.getInt(key+".최초가치");
			value = yc.getInt(key+".현재가치");
			Stock s = new Stock(key, description, fvalue);
			s.setValue(value);
			s.setAmount(yc.getInt(key+".최대구매수량"));
			s.setMinPrice(yc.getInt(key+".최소변동량"));
			s.setMaxPrice(yc.getInt(key+".최대변동량"));
			s.setLastChange(yc.getInt(key+".직전변동량"));
			StockList.add(s);
		}
	}
	public void loadPlayer(){
		LinmaluYamlConfiguration list = YConfig.getYML(dataFolder, "_players.yml");
		List<String> players = list.getStringList("players");
		for(String key : players){
			PlayerData pd = new PlayerData(key);
			LinmaluYamlConfiguration yc = YConfig.getYML(dataFolder, key+".yml");
			List<String> stk = yc.getStringList("보유코인");
			for(String ss : stk){
				Stock s = null;
				for(Stock zs : StockList){
					if(zs.getName().equalsIgnoreCase(ss)){
						s=zs;
					}
				}
				if(s != null){
					int i = yc.getInt(s.getName()+".보유량");
					pd.getStock().put(s, i);
				}
			}
			PlayerList.put(key, pd);
		}

	}
	public static void saveStock(){
		LinmaluYamlConfiguration yc;
		List<String> keys = new ArrayList<>();
		yc = YConfig.getYMLforsave(dataFolder, "stock.yml");
		yc.set("변동시간", changeTime);
		yc.set("접두사", pf.replaceAll("§","&"));
		for(Stock s : StockList){
			keys.add(s.getName());
			yc.set(s.getName()+".설명", s.getDescription());
			yc.set(s.getName()+".최초가치", s.getFirstvalue());
			yc.set(s.getName()+".현재가치", s.getValue());
			yc.set(s.getName()+".최소변동량", s.getMinPrice());
			yc.set(s.getName()+".최대변동량", s.getMaxPrice());
			yc.set(s.getName()+".최대구매수량", s.getAmount());
			yc.set(s.getName()+".직전변동량", s.getLastChange());
		}
		yc.set("주식목록", keys);
		try {
			yc.save(new File(dataFolder, "stock.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onEnable() {
		dataFolder = getDataFolder();
		loadStock();
		loadPlayer();
		Player[] pl = Bukkit.getOnlinePlayers();
		for(Player p : pl){
			if(!PlayerList.containsKey(p.getName())){
				PlayerData pd = new PlayerData(p);
				PlayerList.put(p.getName(), pd);
				pd.save();
			}
		}
		timer = new StockTimer();
		timer.schedule(timer.task, 1000,1000);
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("[ YCoin ] 제작 : 염료 (yeomryo@naver.com)");
		System.out.println("[ YCoin ] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("[ YCoin ] 플러그인이 활성화 되었습니다.");
		System.out.println("[ YCoin ] 본 플러그인의 저작권은 모두 염료에게 있습니다.");
		System.out.println(" ");
		System.out.println("[ YCoin ] 플레이어 정보를 불러왔습니다.");
		System.out.println("[ YCoin ] 등록된 플레이어 : "+PlayerList.size());
		System.out.println(" ");
		System.out.println("[ YCoin ] 등록된 코인 정보를 불러왔습니다.");
		System.out.println("[ YCoin ] 등록된 코인 : "+StockList.size());
		System.out.println(" ");
		System.out.println("[ YCoin ] 기본 라이센스 - 상업적 이용, 배포가 불가능합니다.");
		System.out.println(" ");
		System.out.println(" ");
		
	}
	@Override
	public void onDisable() {
		timer.task.cancel();
		timer.cancel();
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("[ YCoin ] 제작 : 염료 (yeomryo@naver.com)");
		System.out.println("[ YCoin ] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("[ YCoin ] 플러그인이 비활성화 되었습니다.");
		System.out.println("[ YCoin ] 본 플러그인의 저작권은 모두 염료에게 있습니다.");
		System.out.println(" ");
		System.out.println("[ YCoin ] 기본 라이센스 - 상업적 이용, 배포가 불가능합니다.");
		System.out.println(" ");
		System.out.println(" ");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("코인")){
			if(args.length == 0){
				sender.sendMessage("§a-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 생성 <코인명> <시작가격> : §a코인을 생성합니다.");
				sender.sendMessage(pf+"/코인 삭제 <코인명> : §a코인을 삭제합니다.");
				sender.sendMessage(pf+"/코인 변동값 <코인명> <최소> <최대> : §a코인의 변동값을 설정합니다.");
				sender.sendMessage(pf+"/코인 제한 <코인명> <수량> : §a코인의 최대 수량을 제한합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 주기 <닉네임> <코인명> <수량> : §a코인을 지급합니다.");
				sender.sendMessage(pf+"/코인 뺏기 <닉네임> <코인명> <수량> : §a코인을 뺏습니다.");
				sender.sendMessage(pf+"/코인 설정 <닉네임> <코인명> <수량> : §a코인을 설정합니다.");
				sender.sendMessage(pf+"/코인 초기화 : §a모든 유저의 코인을 초기화 합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 확인 [닉네임] : §a보유 코인을 확인합니다.");
				sender.sendMessage(pf+"/코인 상점 : §a코인 상점을 오픈합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 리로드 : §astock.yml 파일을 다시 불러옵니다.");
				sender.sendMessage(" ");
				sender.sendMessage("§a-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				return true;
			}
			if(args[0].equalsIgnoreCase("생성")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 3){
					try{
						String name = args[1];
						int price = Integer.parseInt(args[2]);
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								sender.sendMessage(pf+"§c이미 존재하는 이름입니다.");
								return true;
							}
						}
						Stock s = new Stock(name, "", price);
						StockList.add(s);
						sender.sendMessage(pf+"§a코인이 생성되었습니다.");
						saveStock();
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 생성 <코인명> <시작가격> : §a코인을 생성합니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 생성 <코인명> <시작가격> : §a코인을 생성합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("삭제")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 2){
						String name = args[1];
						LinkedList<Stock> slist = (LinkedList<Stock>) StockList.clone();
						for(Stock s : slist){
							if(s.getName().equalsIgnoreCase(name)){
								StockList.remove(s);
								for(PlayerData pd : PlayerList.values()){
									if(pd.getStock() != null)
										pd.getStock().remove(s);
								}
								sender.sendMessage(pf+s.getName()+"§a코인이 삭제되었습니다.");
								saveStock();
								return true;
							}
						}
						sender.sendMessage(pf+"§c존재하지 않는 이름입니다.");
						return true;
				}else{
					sender.sendMessage(pf+"/코인 삭제 <코인명> : §a코인을 삭제합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("변동값")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 4){
					try{
						String name = args[1];
						int min = Integer.parseInt(args[2]);
						int max = Integer.parseInt(args[3]);
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								s.setMinPrice(min);
								s.setMaxPrice(max);
								sender.sendMessage(pf+"§a변동값이 설정되었습니다.");
								saveStock();
								return true;
							}
						}
						sender.sendMessage(pf+"§c존재하지 않는 이름입니다.");
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 변동값 <코인명> <최소> <최대> : §a코인의 변동값을 설정합니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 변동값 <코인명> <최소> <최대> : §a코인의 변동값을 설정합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("제한")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}

				if(args.length == 3){
					try{
						String name = args[1];
						int amount = Integer.parseInt(args[2]);
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								s.setAmount(amount);
								sender.sendMessage(pf+"§a최대 보유량이 설정되었습니다.");
								saveStock();
								return true;
							}
						}
						sender.sendMessage(pf+"존재하지 않는 이름입니다.");
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 제한 <코인명> <수량> : §a코인의 최대 수량을 제한합니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 제한 <코인명> <수량> : §a코인의 최대 수량을 제한합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("주기")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 4){
					try{
						Player p = Bukkit.getPlayer(args[1]);
						String name = args[2];
						int amount = Integer.parseInt(args[3]);
						if(p == null){
							sender.sendMessage(pf+"존재하지 않는 플레이어입니다..");
							return true;
						}
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								PlayerData pd = PlayerList.get(p.getName());
								if(pd.getStock().containsKey(s)){
									if(pd.getStock().get(s)+amount <= s.getAmount()){
										pd.getStock().put(s, pd.getStock().get(s)+amount);
										sender.sendMessage(pf+"§a해당 플레이어에게 코인을 지급했습니다.");
										p.sendMessage(pf+"§a관리자에 의해 코인이 지급되었습니다.");
										pd.save();
										return true;
									}else{
										sender.sendMessage(pf+"§c최대 보유량("+s.getAmount()+")을 초과할 수 없습니다. 현재 보유량 : "+pd.getStock().get(s));
										return true;
									}
								}
								else{
									if(amount <= s.getAmount()){
										pd.getStock().put(s, amount);
										sender.sendMessage(pf+"§a해당 플레이어에게 코인을 지급했습니다.");
										p.sendMessage(pf+"§a관리자에 의해 코인이 지급되었습니다.");
										pd.save();
										return true;
									}else{
										sender.sendMessage(pf+"§c최대 보유량("+s.getAmount()+")을 초과할 수 없습니다. 현재 보유량 : 0");
										return true;
									}
								}
							}
						}
						sender.sendMessage(pf+"§c존재하지 않는 이름입니다.");
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 주기 <닉네임> <코인명> <수량> : §a코인을 지급합니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 주기 <닉네임> <코인명> <수량> : §a코인을 지급합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("뺏기")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 4){
					try{
						Player p = Bukkit.getPlayer(args[1]);
						String name = args[2];
						int amount = Integer.parseInt(args[3]);
						if(p == null){
							sender.sendMessage(pf+"§c존재하지 않는 플레이어입니다..");
							return true;
						}
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								PlayerData pd = PlayerList.get(p.getName());
								if(pd.getStock().containsKey(s)){
									if(pd.getStock().get(s)-amount > 0){
										pd.getStock().put(s, pd.getStock().get(s)-amount);
										sender.sendMessage(pf+"§a해당 플레이어의 코인을 뺏었습니다.");
										p.sendMessage(pf+"§c관리자에 의해 코인을 뺏겼습니다.");
										pd.save();
										return true;
									}else{
										pd.getStock().put(s, 0);
										sender.sendMessage(pf+"코인을 뺏었습니다.");
										p.sendMessage(pf+"코인을 뺏겼습니다.");
										pd.save();
										return true;
									}
								}
								else{
									sender.sendMessage(pf+"§c그 플레이어는 해당 코인을 보유하고 있지 않습니다.");
									return true;
								}
							}
						}
						sender.sendMessage(pf+"§c존재하지 않는 이름입니다.");
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 뺏기 <닉네임> <코인명> <수량> : §a코인을 뺏습니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 뺏기 <닉네임> <코인명> <수량> : §a코인을 뺏습니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("설정")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				if(args.length == 4){
					try{
						Player p = Bukkit.getPlayer(args[1]);
						String name = args[2];
						int amount = Integer.parseInt(args[3]);
						if(p == null){
							sender.sendMessage(pf+"§c존재하지 않는 플레이어입니다..");
							return true;
						}
						for(Stock s : StockList){
							if(s.getName().equalsIgnoreCase(name)){
								PlayerData pd = PlayerList.get(p.getName());
								if(amount <= s.getAmount()){
									pd.getStock().put(s, amount);
									sender.sendMessage(pf+"§a해당 플레이어의 코인을 설정했습니다.");
									p.sendMessage(pf+"§a관리자에 의해 코인이 설정되었습니다.");
									pd.save();
									return true;
								}else{
									sender.sendMessage(pf+"§c최대 보유량("+s.getAmount()+")을 초과할 수 없습니다.");
									return true;
								}
							}
						}
						sender.sendMessage(pf+"§c존재하지 않는 이름입니다.");
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(pf+"/코인 설정 <닉네임> <코인명> <수량> : §a코인을 설정합니다.");
						return true;
					}
				}else{
					sender.sendMessage(pf+"/코인 설정 <닉네임> <코인명> <수량> : §a코인을 설정합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("초기화")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				for(PlayerData pd : PlayerList.values()){
					pd.getStock().clear();
					pd.save();
				}
				Bukkit.broadcastMessage(pf+"§6관리자 "+sender.getName()+"에 의해 모든 플레이어가 소지한 코인이 초기화됩니다.");
			}
			else if(args[0].equalsIgnoreCase("상점")){
				if(!(sender instanceof Player)){
					sender.sendMessage(pf+"§c콘솔에서는 사용할 수 없는 명령어 입니다.");
					return true;
				}
				Player p = (Player)sender;
				PlayerData pd = Main.PlayerList.get(p.getName());
				pd.showAllStock();
			}
			else if(args[0].equalsIgnoreCase("확인")){
				if(!(sender instanceof Player)){
					sender.sendMessage(pf+"§c콘솔에서는 사용할 수 없는 명령어 입니다.");
					return true;
				}
				if(args.length == 2){
					Player p = Bukkit.getPlayer(args[1]);
					if(p == null){
						sender.sendMessage(pf+"§c존재하지 않는 플레이어입니다..");
						return true;
					}
					PlayerData pd = PlayerList.get(p.getName());
					pd.showMyStock(p);
				}else if(args.length == 1){
					PlayerData pd = PlayerList.get(sender.getName());
					pd.showMyStock((Player)sender);
				}
				else{
					sender.sendMessage(pf+"/코인 확인 [닉네임] : §a보유 코인을 확인합니다.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("리로드")){
				if(!sender.isOp()){
					sender.sendMessage(pf+"§c관리자 전용 명령어입니다.");
					return true;
				}
				sender.sendMessage(pf+"§astock.yml 파일을 다시 불러왔습니다.");
				loadStock();
				loadPlayer();
			}else{
				sender.sendMessage("§a-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 생성 <코인명> <시작가격> : §a코인을 생성합니다.");
				sender.sendMessage(pf+"/코인 삭제 <코인명> : §a코인을 삭제합니다.");
				sender.sendMessage(pf+"/코인 변동값 <코인명> <최소> <최대> : §a코인의 변동값을 설정합니다.");
				sender.sendMessage(pf+"/코인 제한 <코인명> <수량> : §a코인의 최대 수량을 제한합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 주기 <닉네임> <코인명> <수량> : §a코인을 지급합니다.");
				sender.sendMessage(pf+"/코인 뺏기 <닉네임> <코인명> <수량> : §a코인을 뺏습니다.");
				sender.sendMessage(pf+"/코인 설정 <닉네임> <코인명> <수량> : §a코인을 설정합니다.");
				sender.sendMessage(pf+"/코인 초기화 : §a모든 유저의 코인을 초기화 합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 확인 [닉네임] : §a보유 코인을 확인합니다.");
				sender.sendMessage(pf+"/코인 상점 : §a코인 상점을 오픈합니다.");
				sender.sendMessage(" ");
				sender.sendMessage(pf+"/코인 리로드 : §astock.yml 파일을 다시 불러옵니다.");
				sender.sendMessage(" ");
				sender.sendMessage("§a-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				return true;
			}
			return true;
		}
		return false;
	}
}
