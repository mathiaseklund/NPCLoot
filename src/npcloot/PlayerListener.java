package npcloot;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class PlayerListener implements Listener {

	private static PlayerListener instance = new PlayerListener();

	NPCLoot plugin = NPCLoot.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();
	Methods methods = Methods.getInstance();

	public static PlayerListener getInstance() {
		return instance;
	}

	@EventHandler
	public void npcDeath(NPCDeathEvent event) {
		event.setDroppedExp(0);
		event.getDrops().clear();
		NPC npc = event.getNPC();
		int id = npc.getId();
		String fileName = plugin.config.getString("loot." + id);
		if (fileName != null) {
			File file = new File(plugin.getDataFolder() + File.separator + "lootfiles" + File.separator + fileName);
			Player lastAttacker = Lists.lastAttacker.get(id);
			ArrayList<Player> allAttackers = new ArrayList<Player>();
			double mD = 0;
			String mostDamage = "";
			if (Lists.damage.get(id) != null) {
				for (String s : Lists.damage.get(id)) {
					String playerName = s.split(":")[0];
					Player p = Bukkit.getPlayer(playerName);
					allAttackers.add(p);
					Double damage = Double.parseDouble(s.split(":")[1]);
					if (mD < damage) {
						mD = damage;
						mostDamage = playerName;
					}
				}
			}
			Player mdplayer = Bukkit.getPlayer(mostDamage);
			Location loc = event.getNPC().getEntity().getLocation();
			if (!allAttackers.isEmpty()) {
				methods.dropLootAll(id, file, loc, allAttackers);
			}
			if (mdplayer != null) {
				methods.dropLootMD(id, mdplayer, file, loc);
			}
			if (lastAttacker != null) {
				methods.dropLootLH(id, lastAttacker, file, loc);
			}
			Lists.lastAttacker.remove(id);
			Lists.damage.remove(id);
		} else {
			System.out.print(ChatColor.DARK_RED + "ERROR! No loot assigned to NPC! ID: " + id);
		}
	}

	@EventHandler
	public void npcDamageByEntity(NPCDamageByEntityEvent event) {
		if (!event.isCancelled()) {
			NPC npc = event.getNPC();
			int id = npc.getId();
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				String playerName = player.getName();
				double dmg = event.getDamage();
				ArrayList<String> content = new ArrayList<String>();
				boolean contains = false;
				if (Lists.damage.get(id) != null) {
					content = Lists.damage.get(id);
					for (String s : Lists.damage.get(id)) {
						String pN = s.split(":")[0];
						if (pN.equalsIgnoreCase(playerName)) {
							contains = true;
							content.remove(s);
							double totaldamage = Double.parseDouble(s.split(":")[1]);
							totaldamage = totaldamage + dmg;
							String str = playerName + ":" + totaldamage;
							content.add(str);
						}
					}
				}
				if (!contains) {
					String str = playerName + ":" + dmg;
					content.add(str);
				}
				Lists.damage.remove(id);
				Lists.lastAttacker.remove(id);
				Lists.damage.put(id, content);
				Lists.lastAttacker.put(id, player);
			}
		}
	}

	public void moneyMessage(final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				Lists.moneyPicked.remove(player.getName());
				int amount = Lists.money.get(player.getName());
				msg.msg(player, plugin.config.getString("message.moneygained").replace("%amount%", "" + amount));
				Lists.money.remove(player.getName());
			}
		}, 20);
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack is = event.getItem().getItemStack();
		if (is.hasItemMeta()) {
			ItemMeta im = is.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			lore.addAll(im.getLore());
			if (isMoney(is)) {
				boolean hasHidden = false;
				for (String s : lore) {
					s = s.replaceAll("§", "");
					if (s.equalsIgnoreCase("m")) {
						hasHidden = true;
					}
					if (s.equalsIgnoreCase("all")) {
						if (hasHidden) {
							// Player takes money
							event.setCancelled(true);
							event.getItem().remove();
							if (is.getType() == Material.DIAMOND) {
								int amount = is.getAmount();
								methods.giveMoney(player, (100 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (100 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (100 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (100 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							} else if (is.getType() == Material.EMERALD) {
								int amount = is.getAmount();
								methods.giveMoney(player, (10 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (10 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (10 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (10 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							} else if (is.getType() == Material.GOLD_NUGGET) {
								int amount = is.getAmount();
								methods.giveMoney(player, (1 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (1 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (1 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (1 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							}
						}
					} else if (s.equalsIgnoreCase(player.getName())) {
						if (hasHidden) {
							// Player takes money
							event.setCancelled(true);
							event.getItem().remove();
							if (is.getType() == Material.DIAMOND) {
								int amount = is.getAmount();
								methods.giveMoney(player, (100 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (100 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (100 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (100 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							} else if (is.getType() == Material.EMERALD) {
								int amount = is.getAmount();
								methods.giveMoney(player, (10 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (10 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (10 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (10 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							} else if (is.getType() == Material.GOLD_NUGGET) {
								int amount = is.getAmount();
								methods.giveMoney(player, (1 * amount));
								if (Lists.moneyPicked.containsKey(player.getName())) {
									if (Lists.moneyPicked.get(player.getName())) {
										int money = Lists.money.get(player.getName());
										money = money + (1 * amount);
										Lists.money.put(player.getName(), money);
									} else {
										Lists.money.put(player.getName(), (1 * amount));
										Lists.moneyPicked.put(player.getName(), true);
										moneyMessage(player);
									}
								} else {
									Lists.money.put(player.getName(), (1 * amount));
									Lists.moneyPicked.put(player.getName(), true);
									moneyMessage(player);
								}
							}
						}
					} else {
						if (hasHidden) {
							event.setCancelled(true);
						}
					}
				}
				// if (lore.contains(ChatColor.COLOR_CHAR)) {
				// lore.remove(0);
				// msg.brdcst("CONTAINS HIDDEN LORE");
				// String all = "all";
				// String[] cs = all.split("");
				// all = "";
				// for (String str : cs) {
				// all = all + ChatColor.COLOR_CHAR + str;
				// }
				// String name = player.getName();
				// String[] chars = name.split("");
				// String pn = "";
				// for (String s : chars) {
				// pn = pn + ChatColor.COLOR_CHAR + s;
				// }
				// if (lore.contains(pn)) {
				//
				// // Player takes money
				// event.setCancelled(true);
				// event.getItem().remove();
				// msg.brdcst("REMOVING ENTITY");
				// if (is.getType() == Material.DIAMOND) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (100 * amount));
				// } else if (is.getType() == Material.EMERALD) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (10 * amount));
				// } else if (is.getType() == Material.GOLD_NUGGET) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (1 * amount));
				// }
				// } else {
				// if (lore.contains(all)) {
				// // Player takes money
				// event.setCancelled(true);
				// event.getItem().remove();
				// msg.brdcst("REMOVING ENTITY");
				//
				// if (is.getType() == Material.DIAMOND) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (100 * amount));
				// } else if (is.getType() == Material.EMERALD) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (10 * amount));
				// } else if (is.getType() == Material.GOLD_NUGGET) {
				// int amount = is.getAmount();
				// methods.giveMoney(player, (1 * amount));
				// }
				// } else {
				// event.setCancelled(true);
				// }
				// }
				// } else {
				// msg.brdcst("NO HIDDEN DATA");
				// }
			} else {
				boolean removeAll = false;
				boolean removePlayer = false;
				boolean isID = false;
				for (String s : lore) {
					String og = s;
					s = s.replaceAll("§", "");
					if (s.equalsIgnoreCase("all")) {
						removeAll = true;
					} else if (s.equalsIgnoreCase(player.getName())) {
						removePlayer = true;
					} else {
						if (og.contains(ChatColor.COLOR_CHAR + "")) {
							og = Methods.convertToInvisibleString(og);
							if (og.length() > 1) {
								og = og.replaceAll("§", "");
								if (og.contains("id:")) {
									isID = true;
								} else {
									isID = false;
								}
							} else {
								isID = false;
							}
						}
					}
				}
				if (!isID) {
					event.setCancelled(true);
				}
				if (removeAll) {
					String all = Methods.convertToInvisibleString("all");
					lore.remove(all);
					lore.remove("all");
					im.setLore(lore);
					is.setItemMeta(im);
				} else if (removePlayer) {
					String pn = Methods.convertToInvisibleString(player.getName());
					lore.remove(pn);
					im.setLore(lore);
					is.setItemMeta(im);
				}
				// boolean getItem = false;
				// for (String s : lore) {
				// s = ChatColor.stripColor(s);
				// if (s.equalsIgnoreCase("all")) {
				// getItem = true;
				// } else {
				// if (s.equalsIgnoreCase(player.getName())) {
				// getItem = true;
				// } else {
				// // DONT GET ITEM
				// }
				// }
				// }
				// if (getItem) {
				// } else {
				// event.setCancelled(true);
				// }
			}
		}
	}

	public boolean isMoney(ItemStack is) {
		if (is.getType() == Material.DIAMOND || is.getType() == Material.EMERALD || is.getType() == Material.GOLD_NUGGET) {
			return true;
		} else {
			return false;
		}
	}
}