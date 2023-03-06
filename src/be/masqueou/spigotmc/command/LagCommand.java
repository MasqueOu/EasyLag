package be.masqueou.spigotmc.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.masqueou.spigotmc.EasyLag;
import be.masqueou.spigotmc.utils.reflection.Reflection;

public class LagCommand implements CommandExecutor {

	private final EasyLag easyLag;
	
	public LagCommand(EasyLag easyLag) {
		this.easyLag = easyLag;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(easyLag.getMessage("message.sender_is_not_player"));
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			String date = new SimpleDateFormat("EE dd MMM y").format(new Date(System.currentTimeMillis()));

			player.sendMessage(" ");
			player.sendMessage(easyLag.getMessage("format.header"));
			player.sendMessage(" ");
			player.sendMessage(easyLag.getMessage("format.first_line").replace("$server_name$", easyLag.getMessage("main.server_name")).replace("$date$", date).replace("$time$", getTimeValue()));
			player.sendMessage(easyLag.getMessage("format.second_line"));
			player.sendMessage(easyLag.getMessage("format.third_line").replace("$ms$", String.valueOf(getPlayerPing(player))));
			player.sendMessage(easyLag.getMessage("format.fourth_line"));
			player.sendMessage(easyLag.getMessage("format.fifth_line").replace("$tps$", sendLastTps()));
			player.sendMessage(" ");
			player.sendMessage(easyLag.getMessage("format.footer"));
			player.sendMessage(" ");
		} else if (args.length == 1) { 
			if(args[0].equalsIgnoreCase("reload")) {
				if(player.hasPermission(easyLag.getMessage("permission.reload_plugin")) || player.isOp()) {
					easyLag.rlConfig();
					player.sendMessage(easyLag.getMessage("main.prefix") + easyLag.getMessage("message.success_reloading_config"));
				} else {
					player.sendMessage(easyLag.getMessage("main.prefix") + easyLag.getMessage("message.not_permission"));
				}
			}
		}
		return false;
	}

	private String getTimeValue() {
		String result;
		boolean selector = Boolean.parseBoolean(easyLag.getMessage("main.use_24_hour_format"));

		String now = new SimpleDateFormat("hh:mm aa").format(new Date().getTime());
		SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
		SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");

		if (selector) {
			try {
				result = outFormat.format(inFormat.parse(now));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else {
			result = inFormat.format(new Date());
		}

		return result;
	}

	private String sendLastTps() {
		StringBuilder sb = new StringBuilder(easyLag.getMessage("format.tps") + " ");
		double[] tps = getNMSRecentTps();
		for (int i = 0; i < 3; i++) {
			sb.append(format(tps[i]));
			if(i < 2) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	private String format(double tps) {
		return (tps > 18.0D ? ChatColor.GREEN
				: tps > 17.0D ? ChatColor.YELLOW : tps > 15 ? ChatColor.RED : ChatColor.DARK_RED)
				+ (tps > 20.0D ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D);
	}

	private static final Class<?> minecraftServerClass = Reflection.getNmsClass("MinecraftServer");
	private static final Method getServerMethod = minecraftServerClass != null
			? Reflection.makeMethod(minecraftServerClass, "getServer")
			: null;
	private static final Field recentTpsField = minecraftServerClass != null
			? Reflection.makeField(minecraftServerClass, "recentTps")
			: null;

	private static double[] getNMSRecentTps() {
		double[] result = new double[] {-1.0D, -1.0D, -1.0D};
		if (getServerMethod == null || recentTpsField == null) return result;
		Object server = Reflection.callMethod(getServerMethod, null);
		result = Reflection.getField(recentTpsField, server);
		return result;
	}

	public int getPlayerPing(Player player) {
		try {
			String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
		} catch (Exception e) {
			return player.getPing();
		}
	}

}
