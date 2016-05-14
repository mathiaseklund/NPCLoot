package npcloot;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import customitems.CustomItems;
import rankup.RenownAPI;

public class Methods {

	private static Methods instance = new Methods();

	NPCLoot plugin = NPCLoot.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();
	RenownAPI api = RenownAPI.getInstance();
	CustomItems itemapi = CustomItems.getMain();

	public static Methods getInstance() {
		return instance;
	}

	public static String convertToInvisibleString(String s) {
		String hidden = "";
		for (char c : s.toCharArray())
			hidden += ChatColor.COLOR_CHAR + "" + c;
		return hidden;
	}

	// TODO add renown command
	public void dropLootMD(int id, Player player, File lootfile, final Location loc) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(lootfile);
		for (int i = 0; i <= 100; i++) {
			if (i > 0) {
				String type = "";
				if (file.getString("dmg." + i + ".type") != null) {
					type = file.getString("dmg." + i + ".type");
					if (type.equalsIgnoreCase("item")) {
						double chance = file.getDouble("dmg." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("dmg." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							ItemStack is = itemapi.getItem(file.getString("dmg." + i + ".item"), amount);
							ItemMeta im = is.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.addAll(im.getLore());
							String playerName = player.getName();
							playerName = convertToInvisibleString(playerName);
							lore.add(playerName);
							im.setLore(lore);
							is.setItemMeta(im);
							final ItemStack is2 = is;
							final Item item = loc.getWorld().dropItem(loc, is);
							lore.remove(playerName);
							lore.add(convertToInvisibleString("all"));
							im.setLore(lore);
							is2.setItemMeta(im);
							int diss = plugin.config.getInt("loot.disapear");
							diss = diss * 20;
							int ffa = plugin.config.getInt("loot.ffa");
							ffa = ffa * 20;
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {

									item.setItemStack(is2);
								}
							}, ffa);
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {
									loc.getChunk().load();
									item.remove();
								}
							}, diss);
						}
					} else if (type.equalsIgnoreCase("vault")) {
						double chance = file.getDouble("dmg." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("dmg." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							dropMoney(player, amount, loc);
						}
					} else if (type.equalsIgnoreCase("renown")) {
						double chance = file.getDouble("dmg." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							int amount = file.getInt("dmg." + i + ".amount");
							api.addRenown(player.getUniqueId().toString(), amount);
							int newrenown = api.getRenown(player.getUniqueId().toString());
							msg.msg(player, plugin.config.getString("message.loot.renown").replace("%amount%", amount + "").replace("%newrenown%", "" + newrenown));
						}
					}
				} else {
					break;
				}
			}
		}
	}

	public void dropLootAll(int id, File lootfile, final Location loc, ArrayList<Player> players) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(lootfile);
		for (int i = 0; i <= 100; i++) {
			if (i > 0) {
				String type = "";
				if (file.getString("all." + i + ".type") != null) {
					type = file.getString("all." + i + ".type");
					if (type.equalsIgnoreCase("item")) {
						double chance = file.getDouble("all." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("all." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							ItemStack is = itemapi.getItem(file.getString("all." + i + ".item"), amount);
							ItemMeta im = is.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.addAll(im.getLore());
							String playerName = "all";
							playerName = convertToInvisibleString(playerName);
							lore.add(playerName);
							im.setLore(lore);
							is.setItemMeta(im);
							final Item item = loc.getWorld().dropItem(loc, is);
							final ItemStack is2 = is;
							lore.remove(playerName);
							lore.add(convertToInvisibleString("all"));
							im.setLore(lore);
							is2.setItemMeta(im);
							int diss = plugin.config.getInt("loot.disapear");
							diss = diss * 20;

							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {
									loc.getChunk().load();
									item.remove();
								}
							}, diss);
						}
					} else if (type.equalsIgnoreCase("vault")) {
						double chance = file.getDouble("all." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("all." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							dropMoneyALL(amount, loc);
						}
					} else if (type.equalsIgnoreCase("renown")) {
						double chance = file.getDouble("all." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							for (Player p : players) {
								int amount = file.getInt("all." + i + ".amount");
								api.addRenown(p.getUniqueId().toString(), amount);
								int newrenown = api.getRenown(p.getUniqueId().toString());
								msg.msg(p, plugin.config.getString("message.loot.renown").replace("%amount%", amount + "").replace("%newrenown%", "" + newrenown));
							}
						}
					}
				} else {
					break;
				}
			}
		}
	}

	public void dropLootLH(int id, Player player, File lootfile, final Location loc) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(lootfile);
		for (int i = 0; i <= 100; i++) {
			if (i > 0) {
				String type = "";
				if (file.getString("hit." + i + ".type") != null) {
					type = file.getString("hit." + i + ".type");
					if (type.equalsIgnoreCase("item")) {
						double chance = file.getDouble("hit." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("hit." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							ItemStack is = itemapi.getItem(file.getString("hit." + i + ".item"), amount);
							ItemMeta im = is.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.addAll(im.getLore());
							String playerName = player.getName();
							playerName = convertToInvisibleString(playerName);
							lore.add(playerName);
							im.setLore(lore);
							is.setItemMeta(im);
							final ItemStack is2 = is;
							final Item item = loc.getWorld().dropItem(loc, is);
							lore.remove(playerName);
							lore.add(convertToInvisibleString("all"));
							im.setLore(lore);
							is2.setItemMeta(im);
							int diss = plugin.config.getInt("loot.disapear");
							diss = diss * 20;
							int ffa = plugin.config.getInt("loot.ffa");
							ffa = ffa * 20;
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {

									item.setItemStack(is2);
								}
							}, ffa);
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {
									loc.getChunk().load();
									item.remove();
								}
							}, diss);
						}
					} else if (type.equalsIgnoreCase("vault")) {
						double chance = file.getDouble("hit." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							String aString = file.getString("hit." + i + ".amount");
							int a1 = Integer.parseInt(aString.split("-")[0]);
							int a2 = Integer.parseInt(aString.split("-")[1]);
							int amount = util.randInt(a1, a2);
							dropMoney(player, amount, loc);
						}
					} else if (type.equalsIgnoreCase("renown")) {
						double chance = file.getDouble("hit." + i + ".percentage");
						boolean drop = drop(chance);
						if (drop) {
							int amount = file.getInt("hit." + i + ".amount");
							api.addRenown(player.getUniqueId().toString(), amount);
							int newrenown = api.getRenown(player.getUniqueId().toString());
							msg.msg(player, plugin.config.getString("message.loot.renown").replace("%amount%", amount + "").replace("%newrenown%", "" + newrenown));
						}
					}
				} else {
					break;
				}
			}
		}
	}

	public void dropMoneyALL(double amount, Location loc) {
		if (amount >= 100) {
			ItemStack is = new ItemStack(Material.DIAMOND);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");
			String loreName = "all";
			loreName = convertToInvisibleString(loreName);
			meta.add(loreName);
			im.setLore(meta);
			is.setItemMeta(im);
			final Item item = loc.getWorld().dropItem(loc, is);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 100;
			dropMoneyALL(amount, loc);
		} else if (amount >= 10) {
			ItemStack is = new ItemStack(Material.EMERALD);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");
			String loreName = "all";
			loreName = convertToInvisibleString(loreName);
			meta.add(loreName);
			im.setLore(meta);
			is.setItemMeta(im);
			final Item item = loc.getWorld().dropItem(loc, is);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 10;
			dropMoneyALL(amount, loc);
		} else if (amount >= 1) {
			ItemStack is = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");
			String loreName = "all";
			loreName = convertToInvisibleString(loreName);
			meta.add(loreName);
			im.setLore(meta);
			is.setItemMeta(im);
			final Item item = loc.getWorld().dropItem(loc, is);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 1;
			dropMoneyALL(amount, loc);
		}
	}

	public void dropMoney(Player player, double amount, Location loc) {
		if (amount >= 100) {
			ItemStack is = new ItemStack(Material.DIAMOND);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");

			String playerName = player.getName();
			playerName = convertToInvisibleString(playerName);
			meta.add(playerName);
			im.setLore(meta);
			is.setItemMeta(im);
			final ItemStack is2 = is;
			final Item item = loc.getWorld().dropItem(loc, is);
			meta.remove(playerName);
			meta.add(convertToInvisibleString("all"));
			im.setLore(meta);
			is2.setItemMeta(im);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			int ffa = plugin.config.getInt("loot.ffa");
			ffa = ffa * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {

					item.setItemStack(is2);
				}
			}, ffa);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 100;
			dropMoney(player, amount, loc);
		} else if (amount >= 10) {
			ItemStack is = new ItemStack(Material.EMERALD);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");
			String playerName = player.getName();
			playerName = convertToInvisibleString(playerName);
			meta.add(playerName);
			im.setLore(meta);
			is.setItemMeta(im);
			final ItemStack is2 = is;
			final Item item = loc.getWorld().dropItem(loc, is);
			meta.remove(playerName);
			meta.add(convertToInvisibleString("all"));
			im.setLore(meta);
			is2.setItemMeta(im);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			int ffa = plugin.config.getInt("loot.ffa");
			ffa = ffa * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {

					item.setItemStack(is2);
				}
			}, ffa);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 10;
			dropMoney(player, amount, loc);
		} else if (amount >= 1) {
			ItemStack is = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta im = is.getItemMeta();
			ArrayList<String> meta = new ArrayList<String>();
			meta.add(ChatColor.COLOR_CHAR + "m");
			String playerName = player.getName();
			playerName = convertToInvisibleString(playerName);
			meta.add(playerName);
			im.setLore(meta);
			is.setItemMeta(im);
			final ItemStack is2 = is;
			final Item item = loc.getWorld().dropItem(loc, is);
			meta.remove(playerName);
			meta.add(convertToInvisibleString("all"));
			im.setLore(meta);
			is2.setItemMeta(im);
			int diss = plugin.config.getInt("loot.disapear");
			diss = diss * 20;
			int ffa = plugin.config.getInt("loot.ffa");
			ffa = ffa * 20;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {

					item.setItemStack(is2);
				}
			}, ffa);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					item.remove();
				}
			}, diss);
			amount = amount - 1;
			dropMoney(player, amount, loc);
		}
	}

	public boolean drop(double percent) {
		int baseval = 1000;
		util.randInt(0, 1000);
		int k = (int) (baseval * (percent / 100.0f));
		int randint = util.randInt(0, 1000);
		if (randint <= k) {
			return true;
		} else {
			return false;
		}

	}

	public void giveMoney(Player player, int amount) {
		plugin.econ.depositPlayer(player, amount);
	}
}
