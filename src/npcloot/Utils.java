package npcloot;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

	private static Utils instance = new Utils();

	NPCLoot plugin = NPCLoot.getMain();
	Messages msg = Messages.getInstance();

	public static Utils getInstance() {
		return instance;
	}

	public String LocToString(Location loc) {
		String location = "";
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		String world = loc.getWorld().getName();
		location = x + ":" + y + ":" + z + ":" + world;
		return location;
	}

	public Location StringToLoc(String location) {
		Location loc;
		double x = Double.parseDouble(location.split(":")[0]);
		double y = Double.parseDouble(location.split(":")[1]);
		double z = Double.parseDouble(location.split(":")[2]);
		World world = Bukkit.getWorld(location.split(":")[3]);
		loc = new Location(world, x, y, z);
		return loc;
	}

	public String colorString(String string) {
		string = ChatColor.translateAlternateColorCodes('&', string);
		return string;
	}

	public int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}
