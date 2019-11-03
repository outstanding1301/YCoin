package com.yeomryo.stock.Stock;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.yeomryo.stock.Main;
import com.yeomryo.stock.Player.PlayerData;


public class Stock {
	private String name; // 주식 이름
	private String description; // 설명
	private long firstvalue; // 가치
	private long value; // 가치
	
	private double improve; // 증가확률 0~1
	private long lastChange; // - > 감소 + > 증가
	private long nextChange; // - > 감소 + > 증가
	
	private int minPrice;
	private int maxPrice;

	private int amount; // 총 주식 수
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Stock(String name, String description, int value) {
		this.name = name;
		this.description = description;
		this.firstvalue = value;
		this.value = value;
		this.improve=0.5;
		this.amount = 100;
		this.nextChange=0;
		valueAlgo();
	}
	
	public long getNextChange() {
		return nextChange;
	}

	public void setNextChange(long nextChange) {
		this.nextChange = nextChange;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public long getFirstvalue() {
		return firstvalue;
	}

	public void setFirstvalue(long firstvalue) {
		this.firstvalue = firstvalue;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public void setLastChange(long lastChange) {
		this.lastChange = lastChange;
	}
	public long getLastChange() {
		return this.lastChange;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getImprove() {
		return improve;
	}

	public long getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setImprove(double improve) {
		this.improve = improve;
	}
	
	public void Change(){
		if(this.value+this.nextChange > 0){
			this.value += this.nextChange;
			Show();
			valueAlgo();
		}else{
			this.nextChange = -this.value;
			this.value = 0;
			Bukkit.broadcastMessage(Main.pf+"§a[상장폐지]"+this.name+"§f의 가격이 재설정 되었습니다! "+ChatColor.DARK_AQUA+"▼ "+NumberFormat.getIntegerInstance().format(this.value)+"원 "+NumberFormat.getIntegerInstance().format((this.nextChange))+"원 ("+to2(((double)this.nextChange/(this.value-this.nextChange))*100)+"%)");
			for(PlayerData pd : Main.PlayerList.values()){
				List<Stock> l = new ArrayList<>();
				for(Stock s : pd.getStock().keySet()){
					l.add(s);
				}
				for(Stock s : l){
					pd.getStock().remove(s);
				}
			}
			this.nextChange = 0;
			this.value = this.firstvalue;
			this.lastChange = 0;
			valueAlgo();
		}
	}
	public void Show(){
		if(nextChange>=0){
			Bukkit.broadcastMessage(Main.pf+"§a"+this.name+"§f의 가격이 재설정 되었습니다! "+ChatColor.RED+"▲ "+NumberFormat.getIntegerInstance().format(this.value)+"원 +"+NumberFormat.getIntegerInstance().format((this.nextChange))+"원 (+"+to2(((double)this.nextChange/(this.value-this.nextChange))*100)+"%)");
		}else{
			Bukkit.broadcastMessage(Main.pf+"§a"+this.name+"§f의 가격이 재설정 되었습니다! "+ChatColor.DARK_AQUA+"▼ "+NumberFormat.getIntegerInstance().format(this.value)+"원 "+NumberFormat.getIntegerInstance().format((this.nextChange))+"원 ("+to2(((double)this.nextChange/(this.value-this.nextChange))*100)+"%)");
		}
	}
	public double to2(double d){
		int i = (int)d*100;
		return (double)i/100;
	}
	public void valueAlgo(){
		this.lastChange = this.nextChange;
		
		Random nex = new Random();
		if(maxPrice-minPrice > 0){
			if(nex.nextInt(2) == 0)
				this.nextChange = minPrice+nex.nextInt(maxPrice-minPrice);
			else
				this.nextChange = (minPrice+nex.nextInt(maxPrice-minPrice))*(-1);
		}else{
			this.nextChange = 0;
		}
			
		
	}

	public String getChange() {
		String asf;
		if(lastChange>0){
		asf = ChatColor.GREEN+"▲ "+NumberFormat.getIntegerInstance().format(Math.abs(this.lastChange))+ChatColor.WHITE;
	}else if(lastChange == 0){
		asf = ChatColor.WHITE+"-";
	}else{
		asf = ChatColor.RED+"▼ "+NumberFormat.getIntegerInstance().format(Math.abs(this.lastChange))+ChatColor.WHITE;
	}
		return asf;
	}

	
}
