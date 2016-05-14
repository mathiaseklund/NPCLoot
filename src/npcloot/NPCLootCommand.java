package npcloot;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NPCLootCommand implements CommandExecutor {

	NPCLoot plugin = NPCLoot.getMain();
	Messages msg = Messages.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender.isOp() || sender.hasPermission("npcloot.admin")) {
			if (args.length == 0) {
				msg.cmsg(sender, "&7### &eAvailable Commands &7###");
				msg.cmsg(sender, "&a/npcloot reload &7-&b Reloads all the loot files.");
				msg.cmsg(sender, "&a/npcloot <npcid> <lootFilename> &7-&b Makes the NPC's with that ID drop items from specified loot file.");
				msg.cmsg(sender, "&7######");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					Bukkit.getServer().getPluginManager().disablePlugin(plugin);
					Bukkit.getServer().getPluginManager().enablePlugin(plugin);
					msg.cmsg(sender, "&2LOOTFILES HAVE BEEN RELOADED");
				} else {
					msg.cmsg(sender, "&7USAGE: &e/npcloot <npcid> <lootFileName>");
				}
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("reload")) {

					Bukkit.getServer().getPluginManager().disablePlugin(plugin);
					Bukkit.getServer().getPluginManager().enablePlugin(plugin);
					msg.cmsg(sender, "&2LOOTFILES HAVE BEEN RELOADED");
				} else {
					String npcid = args[0];
					String fileName = args[1];
					boolean exists = false;
					try {
						File f = new File(plugin.getDataFolder() + File.separator + "lootfiles" + File.separator);
						File[] files = f.listFiles();
						for (File fe : files) {
							if (fe.exists()) {
								String s = fe.getName();
								msg.cmsg(sender, s);
								if (s.equalsIgnoreCase(fileName + ".yml")) {
									fileName = fileName + ".yml";
									exists = true;
								}
							} else {
								// this is not true file not found
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (exists) {
						// File file = new File(plugin.getDataFolder() +
						// File.separator + "lootfiles" + File.separator +
						// fileName + ".yml");
						// FileConfiguration lootfile =
						// YamlConfiguration.loadConfiguration(file);
						plugin.config.set("loot." + npcid, fileName);
						plugin.savec();
						msg.cmsg(sender, "%a" + fileName + "'s loot has been linked to NPC's with the ID " + npcid);
					} else {
						msg.cmsg(sender, "&4ERROR! &eNo loot file by that name.");
					}
				}
			}
		}
		return false;
	}

}
