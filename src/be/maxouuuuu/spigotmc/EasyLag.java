package be.maxouuuuu.spigotmc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import be.maxouuuuu.spigotmc.command.LagCommand;
import be.maxouuuuu.spigotmc.utils.bstats.Metrics;
import be.maxouuuuu.spigotmc.utils.bstats.Metrics.MultiLineChart;

public class EasyLag extends JavaPlugin {

	private FileConfiguration fileConfiguration;

	@Override
	public void onEnable() {
		Bukkit.getLogger().info("----------------------------------------------------------------");
		Bukkit.getLogger().info("EasyLag by MasqueOù");
		Bukkit.getLogger().info("Hi :3 ! EasyLag is free, want the source code ? Check my Github \"MasqueOu\".");
		Bukkit.getLogger().info("----------------------------------------------------------------");

		initStats();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		rlConfig();
		
		getCommand("lag").setExecutor(new LagCommand(this));
	}
	
	private void initStats() {
		int pluginId = 12218;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new MultiLineChart("players_and_servers", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> valueMap = new HashMap<>();
                valueMap.put("servers", 1);
                valueMap.put("players", Bukkit.getOnlinePlayers().size());
                return valueMap;
            }
        }));
	}
	
	public void rlConfig() {
		reloadConfig();
		fileConfiguration = null;
		
		fileConfiguration = getConfig();
	}
	
	public String getMessage(String s) {
		if(fileConfiguration.get(s) != null) {
			return fileConfiguration.get(s).toString().replace("&", "§");
		} else {
			return "Hello, i found a problem in the configuration file. Reset the configuration and contact me if the problem persists. Error code: "+s;
		}
	}

	@Override
	public void onDisable() {
		saveConfig();
	}

}