package npcloot;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class NPCLoot extends JavaPlugin {

	private static NPCLoot main;

	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	File configurationConfig;
	public FileConfiguration config;
	File playerData;
	public FileConfiguration pdata;

	String prefix = "";

	public static NPCLoot getMain() {
		return main;
	}

	public void onEnable() {
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		main = this;
		configurationConfig = new File(getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configurationConfig);
		playerData = new File(getDataFolder() + File.separator + "lootfiles" + File.separator, "tempLoot.yml");
		pdata = YamlConfiguration.loadConfiguration(playerData);
		loadConfig();
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		getCommand("npcloot").setExecutor(new NPCLootCommand());
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void savec() {
		try {
			config.save(configurationConfig);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void savepd() {
		try {
			pdata.save(playerData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {

		config.addDefault("message.loot.renown", "You've been given %amount% renown. New renown balance: %newrenown% renown.");
		config.addDefault("loot.ffa", 20);
		config.addDefault("loot.disapear", 180);
		config.addDefault("message.moneygained", "&bYou've picked up &a%amount%6b gold.");
		pdata.addDefault("dmg.1.type", "item");
		pdata.addDefault("dmg.2.type", "vault");
		pdata.addDefault("dmg.3.type", "renown");
		pdata.addDefault("dmg.1.item", 1);
		pdata.addDefault("dmg.1.amount", "1-1");
		pdata.addDefault("dmg.2.amount", "1-5000");
		pdata.addDefault("dmg.2.percentage", 1);
		pdata.addDefault("dmg.3.amount", 50);
		pdata.addDefault("hit.1.type", "item");
		pdata.addDefault("hit.2.type", "vault");
		pdata.addDefault("hit.3.type", "renown");
		pdata.addDefault("hit.1.item", 1);
		pdata.addDefault("hit.1.amount", "1-1");
		pdata.addDefault("hit.1.percentage", 50);
		pdata.addDefault("hit.2.amount", "1-5000");
		pdata.addDefault("hit.2.percentage", 1);
		pdata.addDefault("hit.3.amount", 50);
		pdata.addDefault("all.1.type", "item");
		pdata.addDefault("all.2.type", "vault");
		pdata.addDefault("all.3.type", "renown");
		pdata.addDefault("all.1.item", 1);
		pdata.addDefault("all.1.amount", "1-1");
		pdata.addDefault("all.1.percentage", 50);
		pdata.addDefault("all.2.amount", "1-5000");
		pdata.addDefault("all.2.percentage", 1);
		pdata.addDefault("all.3.amount", 50);
		config.addDefault("message.noperm", "&4Error: You don't have permission to use this function.");
		config.addDefault("message.onlyplayer", "&4Error: Only players may use this function.");
		config.addDefault("prefix", "");
		config.options().copyDefaults(true);
		pdata.options().copyDefaults(true);
		savepd();
		savec();
		prefix = config.getString("prefix");

	}

}
