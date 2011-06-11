package com.WildAmazing.marinating.CombatTag;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.*;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * CombatTag for Bukkit
 *
 * @author <Your Name>
 */
public class CombatTag extends JavaPlugin {
    private final CombatTagPlayerListener playerListener = new CombatTagPlayerListener(this);
    private final CombatTagEntityListener entityListener = new CombatTagEntityListener(this);
    private HashMap<String, PlayerCombatClass> PLAYERLIST = new HashMap<String, PlayerCombatClass>(); //All players should be in this list.
    
    static String mainDirectory = "plugins/CombatTag";
    public static File CONFIG = new File(mainDirectory + File.separator + "CombatTag.properties");
    public static File PVPLOG = new File(mainDirectory + File.separator + "CombatTag.Players");
    static Properties prop = new Properties();
    Properties pvploggers = new Properties();
    private long TAGTIME = 15000; //time in milliseconds (1000 is 1)
    private long GRACEPERIOD = 45000; //time in milliseconds before players are considered penalized
   // private long EXTENDEDGRACEPERIOD = 30000; //grace period for players who accidently disconnect.
    private String PENALTY = "DEATH";
    private boolean INVENTORYCLEAR = true;
    private boolean LIGHTNING = false;
    private boolean DROP = true; //Dropitems at feet instead of using command
    private boolean DEBUG = false;
    private int MAXRELOG = 1; //Number of times a player can relog during a tag
    private String MSG2PLR = "&d[CombatTag] &c $tagged &6 was executed for logging off while in combat with &c $tagger";
    private String MSG1PLR =  "&d[CombatTag] &c $tagged &6 was executed for logging off during pvp";
    private String ITEMSDROPPEDMSG = "&d[CombatTag] &c $tagged &6 has pvp logged. His/Her items drop at your feet";
    
    public static Logger log = Logger.getLogger("Minecraft");

    public CombatTag(){
    	super();
    }
    public void onEnable() {
        log.info("[CombatTag] Operational.");
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, (Listener) entityListener, Event.Priority.Monitor, this);
        
    	new File(mainDirectory).mkdir(); //directory for the config file
        if(!CONFIG.exists()){ //Make config file if it doesn't exist
        	try { 
        		
        		CONFIG.createNewFile(); //make new file
                FileOutputStream out = new FileOutputStream(CONFIG); //for writing the file
               // prop.put("Extended_Grace_Period", "30");
	        	prop.put("Debug", "False");
               // prop.put("Drop_items_on_pvp_log", "true");
               prop.put("Penalty", "DEATH");
               prop.put("PvpMessage2plr", "&d[CombatTag] &c $tagged &6 was executed for logging off while in combat with &c $tagger");
               prop.put("PvpMessage1plr", "&d[CombatTag] &c $tagged &6 was executed for logging off during pvp");
               prop.put("ItemsDroppedMsg", "&d[CombatTag] &c $tagged &6 has pvp logged. His/Her items drop at your feet");
               // prop.put("Inventory_steal", "true");
                prop.put("TagTime", "15");
                prop.put("Grace_period","45");
                prop.put("MaxRelog", "1");
               // prop.put("Lightning","false");
                prop.store(out, " TagTime = duration of tag" + "\r\n Grace_period = time the player has to relog (starting from the moment they logout)"
                		+ "\r\n Debug = enables debug mode (be ready for the spam)" + "\r\n PvpMessage2plr is called upon a pvp logger logging back in.\r\n It supports $tagger (person who hit the pvplogger) and $tagged (Pvplogger).\r\n It also supports color coding using the &(0-9,a-f)"
                		+ "\r\n PvpMessage1plr is nearly the same as PvpMessage1plr except it is called when the pvp logger did not log back in before the server was reloaded or restarted.\r\n It supports $tagged and &colors only."
                		+ "\r\n ItemsDroppedMsg is called when the player is considered a pvplogger(when the items would normally drop to the gound)." +
                		 "\r\n It supports $tagger,$tagged and chat colors and only send the message to the person who tagged the pvp logger, as apposed to the entire server." +
                		 "\r\n MaxRelog is the maximum number of times a player can relog during a tag period.");
                out.flush();  
                out.close(); //save and close writer
                log.info("[CombatTag] New file created.");
            } catch (IOException ex) {
                log.warning("[CombatTag] File creation error: "+ex.getMessage());
            }
        }
			if(!PVPLOG.exists()){
				try { 
					PVPLOG.createNewFile();
					FileOutputStream badplayers = new FileOutputStream(PVPLOG);
        			pvploggers.store(badplayers, "Do not edit this file\r\n This file is only for players who have not yet been punished by combattag.");
        			badplayers.flush();
        			badplayers.close();
        			log.info("[CombatTag] BadPlayers file created.");
        		
				}
				catch (IOException ex){
					log.warning("[CombatTag] File creation error: " + ex.getMessage());
				}
	    }
        else {
	    	log.info("[CombatTag] Detected existing config file and loading.");
	        loadProcedure();//added later
	        logit("Debug is enabled! Be ready for the spam.");

        }
			loadplayers();//get players currently online here
    }
    public void loadProcedure(){
    	try {
	        FileInputStream in = new FileInputStream(CONFIG); //Creates the input stream
	        prop.load(in); //loads file
	        PENALTY = prop.getProperty("Penalty");
	       // EXTENDEDGRACEPERIOD = Long.parseLong(prop.getProperty("Extended_Grace_Period"))*1000; //To be implemented (will change time depending on disconect type)
	       // INVENTORYCLEAR = Boolean.parseBoolean(prop.getProperty("Inventory_steal"));
	        TAGTIME = Long.parseLong(prop.getProperty("TagTime"))*1000;
	        GRACEPERIOD = Long.parseLong(prop.getProperty("Grace_period"))*1000;
	       // LIGHTNING = Boolean.parseBoolean(prop.getProperty("Lightning"));
	       // DROP = Boolean.parseBoolean(prop.getProperty("Drop_items_on_pvp_log"));
	        DEBUG = Boolean.parseBoolean(prop.getProperty("Debug"));
	        MSG2PLR = prop.getProperty("PvpMessage2plr");
	        MSG1PLR = prop.getProperty("PvpMessage1plr");
	        ITEMSDROPPEDMSG = prop.getProperty("ItemsDroppedMsg");
	        MAXRELOG = Integer.parseInt(prop.getProperty("MaxRelog"));
	        in.close(); //Closes the input stream.
	        FileInputStream inplayerfile = new FileInputStream(PVPLOG);
	        pvploggers.load(inplayerfile);
	        inplayerfile.close();
    	}catch (Exception e){
    		log.severe("[CombatTag] Loading error: "+e.getMessage());
    	}
    }
    public void onDisable() {
    	try {
        	for (PlayerCombatClass i : PLAYERLIST.values())
        	{
        		if(i.hasPvplogged() == true)
        		{
        			pvploggers.put(i.getPlayerName(), "logged");
        		}
        	}
			FileOutputStream badplayers = new FileOutputStream(PVPLOG);
			pvploggers.store(badplayers, "Do not edit this file\r\n This file is only for players who have not yet been punished by combattag.");
			badplayers.flush();
			badplayers.close();
		} catch (FileNotFoundException e) {

			log.info("Combat tag has encountered error" + e);
			
		} catch (IOException e) {
			log.info("Combat tag has encountered error" + e);
		}

		
        log.info("[CombatTag] Out.");
    }
    public boolean checkpvplogger(String Playername)
    {
    	if(pvploggers.containsKey(Playername))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    public void removepvplogger(String Playername)
    {
    	pvploggers.remove(Playername);
    }
    public boolean getDropItems(){
    	return DROP;
    }
    public boolean getLightning(){
    	return LIGHTNING;
    }
    public int getMaxRelog()
    {
    	return MAXRELOG;
    }
    public long getGracePeriod(){
    	return GRACEPERIOD;
    }
  /*  public long getExtendedGracePeriod()
    {
    	return EXTENDEDGRACEPERIOD;
    }
    */

    public String getPenalty(){
    	return PENALTY;
    }
    public void setPenalty(String s){
    	PENALTY = s;
    }
    public boolean getInventoryClear(){
    	return INVENTORYCLEAR;
    }
    public void setInventoryClear(boolean b){
    	INVENTORYCLEAR = b;
    }
    public long getTagTime()
    {
    	return TAGTIME;
    }
    public boolean isinPlayerList(String PlayerName)
    {
    	return(PLAYERLIST.containsKey(PlayerName));
    }
    public PlayerCombatClass getPCC(String PlayerName)// Retrieves PlayerCombatClass from HashMap
    {
    	return(PLAYERLIST.get(PlayerName));
    }
    public void addtoPCC(Player myplayer)// Adds Player to HashMap PlayerList
    {
    	PLAYERLIST.put(myplayer.getName(),new PlayerCombatClass(myplayer));
    	return;
    }
    public void logit(String stringlog)
    {
    	if (DEBUG == true){
    		log.info(stringlog);
    	}
    }
    public void loadplayers()//Loads Players upon starting up. Used for reloads and such
    {
        logit("loading all players currently in world");
        //Add all Players currently online to the PLAYERLIST
        List<World> myworlds = getServer().getWorlds();
    	for(int i = 0;myworlds.size() > i; i++)
    	{
    		List<Player> myplayers = myworlds.get(i).getPlayers();
    		for(int k = 0;myplayers.size() > k; k++)
    		{
    			addtoPCC(myplayers.get(k));
    		}
    	}
    	return;
    }
	public void killAndClean(Player p)//Kills Player and cleans inventory
	{
		if (getPenalty().equalsIgnoreCase("DEATH")){
			p.getInventory().clear();
			if (getLightning())
				p.getWorld().strikeLightning(p.getLocation());
			logit(p.getName() + "'s inventory has been cleared and killed");
			
    		p.setHealth(0);
		}
	}
	public void removetaggedfromallplrs(String PlayerName)// Removes Player from all other players tagged list
	{
		for (PlayerCombatClass i : PLAYERLIST.values()){
			i.removeFromTaggedPlayers(PlayerName);
		}
		logit("Removing " + PlayerName + " from all players taged list");
		return;
	}
	public void dropitemsandclearPCCitems(String Winner, String Loser)// Drops items naturally infront of Player and removes items from ...
	{
		PlayerCombatClass PCCLoser = getPCC(Loser);
		PlayerCombatClass PCCWinner = getPCC(Winner);
			if(isPlrOnline(PCCWinner.getPlayerName()))
			{
				Player PlrWinner = getServer().getPlayer(PCCWinner.getPlayerName());//Winner by default (or  by pvp logging)
				sendMessageWinner(PlrWinner, PCCLoser.getPlayerName());
				if(getPenalty().equalsIgnoreCase("DEATH"))
				{
					
					logit("dropping " + Loser + "items at " + Winner + "'s feet");
				
					for(int i = 0;PCCLoser.getItems().size() > i; i++)
					{
						PlrWinner.getWorld().dropItemNaturally(PlrWinner.getLocation(), PCCLoser.getItems().get(i));
					}
				}
			}
			else
			{
				logit("Unable to get winner null returned. (winner not online)");
			}
		PCCLoser.clearItems();
		return;
	}
	public void configureTaggerAndTagged(PlayerCombatClass Tagger, PlayerCombatClass Tagged)//Configures Tagged and Tagger appropriately
	{
		if(!(Tagger.hasTaggedPlayer(Tagged.getPlayerName())))
		{
			Tagged.setTaggedBy(Tagger.getPlayerName()); //Sets tagger as the tagger for tagged
			Tagger.addToTaggedPlayers(Tagged.getPlayerName()); //Adds Tagged player to Taggers player list
		}
		Tagged.setTagExpiration(getTagTime());// Sets the tag expiration for Tagged
	}
	
	public void announcePvPLog(String tagged, String tagger)
	{
		String Messageout = MSG2PLR;
		Messageout = Messageout.replace("$tagged", tagged);
		Messageout = Messageout.replace("$tagger", tagger);
		Messageout = Messageout.replaceAll("&([0-9a-f])", "\u00A7$1");
		getServer().broadcastMessage(Messageout);
	}
	public void announcePvPLog(String tagged)
	{
		String Messageout = MSG1PLR;
		Messageout = Messageout.replace("$tagged", tagged);
		Messageout = Messageout.replaceAll("&([0-9a-f])", "\u00A7$1");
		getServer().broadcastMessage(Messageout);
	}
	public void sendMessageWinner(Player winner, String Loser)
	{
		 String mymessage = ITEMSDROPPEDMSG;
		 mymessage = mymessage.replace("$tagged", Loser );
		 mymessage = mymessage.replaceAll("&([0-9a-f])", "\u00A7$1");
		 winner.sendMessage(mymessage);
	}
	
	public boolean isPlrOnline(String Playername)
	{
		try
		{
			Player myplayer = getServer().getPlayer(Playername);
			return myplayer.isOnline();
		}
		catch(NullPointerException e)
		{
			return false;
		}
	}
	public void updateTags(String playername, Boolean Continue_deeper)//Updates tags for players (called from onCommand)
	{
		PlayerCombatClass Tagged = getPCC(playername); //Player1 (tagged player)
		if(Tagged.isTagged())
		{
			if(Tagged.tagExpired())
			{
				if(isPlrOnline(Tagged.getPlayerName()))//If tagged is online
				{
					Tagged.removeTimesReloged();
					PlayerCombatClass Tagger = getPCC(Tagged.getTaggedBy());
					Tagged.removeTaggedBy();
					Tagger.removeFromTaggedPlayers(Tagged.getPlayerName());
					//remove tags here
				}
			}
		}
		if(Continue_deeper == true)
		{
			if(Tagged.hasTaggedPlayer())// Check to see if Player has tagged other players
			{	
				logit(Tagged.getPlayerName() + " has tagged players");
				ArrayList<String> Myarray = Tagged.getTaggedPlayers();// Temporary arraylist for deep copy
				logit("setting up iterator");
				//Make a deep copy of Myarray
				Iterator<String> itr = Myarray.iterator(); // Setup iterator
				ArrayList<String> backup = new ArrayList<String>();
				while (itr.hasNext())//Check to see if there is another element in Myarray
				{
					backup.add(itr.next());	//Copy each element in arraylist
				}
				//Deep copy finished
				Iterator<String> newitr = backup.iterator();//Setup iterator
				while(newitr.hasNext())// Check to see if there is another element in backup
				{
					String currentplayer = newitr.next();
					logit("recursive call to update tags using " + currentplayer);
					updateTags(currentplayer, false);// Recursive call for tagged player
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(command.getName().equalsIgnoreCase("ct") || command.getName().equalsIgnoreCase("combattag"))
		{
			if(sender instanceof Player)
			{
				if(args.length == 0)
				{
					Player CmdPlr = (Player)sender;
					PlayerCombatClass PCCPlr = getPCC(CmdPlr.getName());
					updateTags(PCCPlr.getPlayerName(),true);//Updates tags to represent most recent values
					if(PCCPlr.isTagged())
					{
						
						CmdPlr.sendMessage(ChatColor.GOLD + "You are tagged by " + ChatColor.RED +PCCPlr.getTaggedBy() + ChatColor.GOLD +" for " + PCCPlr.tagPeriodInSeconds() + " more seconds.");
						CmdPlr.sendMessage(ChatColor.GOLD + "You have " + getGracePeriod()/1000 + " seconds to relog");
						CmdPlr.sendMessage(ChatColor.GOLD + "You have " + new Integer (getMaxRelog()-PCCPlr.getTimesReloged()).toString() + " relog(s) remaining for this tag");
					}
					else
					{
						CmdPlr.sendMessage(ChatColor.GOLD + "You are not tagged.");
					}
					CmdPlr.sendMessage(ChatColor.GOLD + "You have tagged : " + ChatColor.RED +PCCPlr.TaggedList());//Sends the player a list of players he has tagged
				return true;
				}
			}
			else
			{
				log.info("Only Players can use CombatTag");
				return true;
			}
		}
		return false;
	}
}

