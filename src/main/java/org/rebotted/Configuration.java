package org.rebotted;

import java.math.BigInteger;

/**
 * The main configuration for the Client
 * 
 * @author Freyr
 */
public final class Configuration {
	
	private Configuration() {
		
	}

	public static boolean OSRS_GAMEFRAME = false;

	public static final BigInteger RSA_MODULUS = new BigInteger(
			"91553247461173033466542043374346300088148707506479543786501537350363031301992107112953015516557748875487935404852620239974482067336878286174236183516364787082711186740254168914127361643305190640280157664988536979163450791820893999053469529344247707567448479470137716627440246788713008490213212272520901741443");

	public static final BigInteger RSA_EXPONENT = new BigInteger("65537");

	/**
	 * Sends client-related debug messages to the client output stream
	 */
	public static boolean client_debug = true;

	/**
	 * The address of the server that the client will be connecting to
	 */
	public static String server_address = "2006rebotted.hopto.org";

	public static final String CACHE_DIRECTORY = "./Cache/";

	/**
	 * The port of the server that the client will be connecting to
	 */
	public static int server_port = 43594;

	public static boolean useJaggrab = false;

	/**
	 * Toggles a security feature called RSA to prevent packet sniffers
	 */
	public static final boolean ENABLE_RSA = true;

	/**
	 * The url that the users will get redirected to after clicking "New User"
	 */
	public static final String REGISTER_ACCOUNT = "www.google.com";

	/**
	 * A string which indicates the Client's name.
	 */
	public static final String CLIENT_NAME = "2006Rebotted";

	/**
	 * Dumps map region images when new regions are loaded.
	 */
	public static boolean dumpMapRegions = false;

	/**
	 * Displays debug messages on loginscreen and in-game
	 */
	public static boolean clientData = true;

	/**
	 * Enables the use of music played through the client
	 */
	public static boolean enableMusic = true;

	/**
	 * Toggles the ability for a player to see roofs in-game
	 */
	public static boolean enableRoofs = true;

	/**
	 * Used for change worlds button on login screen
	 */
	public static boolean worldSwitch = false;

	/**
	 * Enables extra frames in-between animations to give the animation a smooth
	 * look
	 */
	public static boolean enableTweening = true;

	/**
	 * Used to repack indexes Index 1 = Models Index 2 = Animations Index 3 =
	 * Sounds/Music Index 4 = Maps
	 */
	public static boolean repackIndexOne = false, repackIndexTwo = false, repackIndexThree = false,
			repackIndexFour = false;

	/**
	 * Dump Indexes Index 1 = Models Index 2 = Animations Index 3 = Sounds/Music
	 * Index 4 = Maps
	 */
	public static boolean dumpIndexOne = false, dumpIndexTwo = false, dumpIndexThree = false, dumpIndexFour = false;

	/**
	 * Shows the ids of items, objects, and npcs on right click
	 */
	public static boolean enableIds = false;

	/**
	 * Used to merge all the OS Buddy XP Drops so the counter doesn't get too
	 * big if you are training a lot of different skills
	 */
	public static boolean xp_merge = true;

	/**
	 * Enables fog effects
	 */
	public static boolean enableFog = false;

	/**
	 * newDamage enables or disables fake constitution.
	 */
	public static boolean newDamage = true;

	/**
	 * npcBits can be changed to what your server's bits are set to.
	 */
	public static final int npcBits = 12;

	/**
	 * Enables the use of run energy
	 */
	public static boolean runEnergy = false;

	/**
	 * Displays health above entities heads
	 */
	public static boolean hpAboveHeads = false;

	/**
	 * Displays names above entities
	 */
	public static boolean namesAboveHeads = false;

	/**
	 * Displays OS Buddy orbs on HUD
	 */
	public static boolean enableOrbs = true;

	/**
	 * Enables/Disables Revision 554 hitmarks
	 */
	public static boolean hitmarks554 = false;

	/**
	 * Enables/Disables Revision 554 health bar
	 */
	public static boolean hpBar554 = false;

	/**
	 * Enables the HUD to display 10 X the amount of hitpoints
	 */
	public static boolean tenXHp = false;

}
