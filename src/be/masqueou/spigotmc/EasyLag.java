package be.masqueou.spigotmc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import be.masqueou.spigotmc.command.LagCommand;
import be.masqueou.spigotmc.utils.bstats.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyLag extends JavaPlugin {

	private FileConfiguration fileConfiguration;

	@Override
	public void onEnable() {
		Bukkit.getLogger().info("--------------------------------------------");
		Bukkit.getLogger().info("EasyLag by MasqueOù");
		Bukkit.getLogger().info("Hi ! EasyLag is free and opensource.");
		Bukkit.getLogger().info("Thanks for using my plugin.");
		Bukkit.getLogger().info("--------------------------------------------");

		initStats();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		rlConfig();
		
		Objects.requireNonNull(getCommand("lag")).setExecutor(new LagCommand(this));
	}
	
	private void initStats() {
		int pluginId = 12218;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
			Map<String, Integer> valueMap = new HashMap<>();
			valueMap.put("servers", 1);
			valueMap.put("players", Bukkit.getOnlinePlayers().size());
			return valueMap;
		}));
	}
	
	public void rlConfig() {
		reloadConfig();
		fileConfiguration = null;
		
		fileConfiguration = getConfig();
	}
	
	public String getMessage(String s) {
		if(fileConfiguration.get(s) != null) {
			return Objects.requireNonNull(fileConfiguration.get(s)).toString().replace("&", "§");
		} else {
			return "Hello, i found a problem in the configuration file. Reset the configuration and contact me if the problem persists. Error code: "+s;
		}
	}

	@Override
	public void onDisable() {
		saveConfig();
	}

}