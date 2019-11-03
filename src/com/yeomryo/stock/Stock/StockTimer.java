package com.yeomryo.stock.Stock;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import com.yeomryo.stock.Main;
import com.yeomryo.stock.Player.PlayerData;

public class StockTimer extends Timer{
	public TimerTask task;
	int i=0;
	public StockTimer() {
		i=0;
		task = new TimerTask() {
			
			@Override
			public void run() {
				i++;
				if(i>=Main.changeTime){
					for(Stock s : Main.StockList){
						s.Change();
					}
					Main.saveStock();
					for(PlayerData pd : Main.PlayerList.values()){
						pd.refresh();
					}
					i=0;
				}
			}
		};
	}
}
