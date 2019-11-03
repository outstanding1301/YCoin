package com.yeomryo.stock.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import com.yeomryo.stock.Main;

public class YConfig {

	public static LinmaluYamlConfiguration getYML(String path, String file){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(path);
		if(!f.exists())
			f.mkdirs();
		f = new File(path,file);
		try {
			if(!f.exists())
					f.createNewFile();
			yc.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}
	public static LinmaluYamlConfiguration getYMLforsave(File path, String file){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(path,"");
		if(!f.exists())
			f.mkdirs();
		f = new File(path,file);
		try {
			if(f.exists())
				f.delete();
			f.createNewFile();	
			yc.load(f);
			
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}

	public static LinmaluYamlConfiguration getConfig(){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(Main.dataFolder, "");
		if(!f.exists())
			f.mkdirs();
		f = new File(Main.dataFolder,"config.yml");
		try {
			if(!f.exists()){
				f.createNewFile();
				Main.saveStock();
			}
			yc.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}
	public static LinmaluYamlConfiguration getConfigforsave(){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(Main.dataFolder, "");
		if(!f.exists())
			f.mkdirs();
		f = new File(Main.dataFolder,"config.yml");
		try {
			if(!f.exists()){
					f.createNewFile();
					yc.load(f);
			}else{
				f.delete();
				f.createNewFile();
				yc.load(f);
			}
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}

	public static LinmaluYamlConfiguration getYML(File path, String file){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(path,"");
		if(!f.exists())
			f.mkdirs();
		f = new File(path,file);
		try {
			if(!f.exists())
					f.createNewFile();
			yc.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}
	public static LinmaluYamlConfiguration getStockYML(File path, String file){
		LinmaluYamlConfiguration yc= new LinmaluYamlConfiguration();
		File f = new File(path,"");
		if(!f.exists())
			f.mkdirs();
		f = new File(path,file);
		try {
			if(!f.exists()){
				f.createNewFile();
				Main.saveStock();
			}
			yc.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return yc;
	}
	
}
