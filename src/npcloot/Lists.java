package npcloot;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class Lists {

	public static HashMap<Integer, ArrayList<String>> damage = new HashMap<Integer, ArrayList<String>>();
	public static HashMap<Integer, Player> lastAttacker = new HashMap<Integer, Player>();
	public static HashMap<String, Integer> money = new HashMap<String, Integer>();
	public static HashMap<String, Boolean> moneyPicked = new HashMap<String, Boolean>();
}
