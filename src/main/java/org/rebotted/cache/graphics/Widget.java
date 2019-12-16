package org.rebotted.cache.graphics;

import org.rebotted.Client;
import org.rebotted.Configuration;
import org.rebotted.cache.FileArchive;
import org.rebotted.cache.anim.Frame;
import org.rebotted.cache.def.ItemDefinition;
import org.rebotted.cache.def.NpcDefinition;
import org.rebotted.collection.ReferenceCache;
import org.rebotted.entity.model.Model;
import org.rebotted.io.Buffer;
import org.rebotted.util.StringUtils;

/**
 * Previously known as RSInterface, which is a class used to create and show
 * game interfaces.
 */
public final class Widget {

	public static final int OPTION_OK = 1;
	public static final int OPTION_USABLE = 2;
	public static final int OPTION_CLOSE = 3;
	public static final int OPTION_TOGGLE_SETTING = 4;
	public static final int OPTION_RESET_SETTING = 5;
	public static final int OPTION_CONTINUE = 6;

	public static final int TYPE_CONTAINER = 0;
	public static final int TYPE_MODEL_LIST = 1;
	public static final int TYPE_INVENTORY = 2;
	public static final int TYPE_RECTANGLE = 3;
	public static final int TYPE_TEXT = 4;
	public static final int TYPE_SPRITE = 5;
	public static final int TYPE_MODEL = 6;
	public static final int TYPE_ITEM_LIST = 7;

	public void swapInventoryItems(int i, int j) {
		int id = inventoryItemId[i];
		inventoryItemId[i] = inventoryItemId[j];
		inventoryItemId[j] = id;
		id = inventoryAmounts[i];
		inventoryAmounts[i] = inventoryAmounts[j];
		inventoryAmounts[j] = id;
	}

	public static void load(FileArchive interfaces, GameFont textDrawingAreas[], FileArchive graphics) {
		spriteCache = new ReferenceCache(50000);
		Buffer buffer = new Buffer(interfaces.readFile("data"));
		int defaultParentId = -1;
		buffer.readUShort();
		interfaceCache = new Widget[38000];

		while (buffer.currentPosition < buffer.payload.length) {
			int interfaceId = buffer.readUShort();
			if (interfaceId == 65535) {
				defaultParentId = buffer.readUShort();
				interfaceId = buffer.readUShort();
			}

			Widget widget = interfaceCache[interfaceId] = new Widget();
			widget.id = interfaceId;
			widget.parent = defaultParentId;
			widget.type = buffer.readUnsignedByte();
			widget.optionType = buffer.readUnsignedByte();
			widget.contentType = buffer.readUShort();
			widget.width = buffer.readUShort();
			widget.height = buffer.readUShort();
			widget.opacity = (byte) buffer.readUnsignedByte();
			widget.hoverType = buffer.readUnsignedByte();
			if (widget.hoverType != 0)
				widget.hoverType = (widget.hoverType - 1 << 8) + buffer.readUnsignedByte();
			else
				widget.hoverType = -1;
			int operators = buffer.readUnsignedByte();
			if (operators > 0) {
				widget.scriptOperators = new int[operators];
				widget.scriptDefaults = new int[operators];
				for (int index = 0; index < operators; index++) {
					widget.scriptOperators[index] = buffer.readUnsignedByte();
					widget.scriptDefaults[index] = buffer.readUShort();
				}

			}
			int scripts = buffer.readUnsignedByte();
			if (scripts > 0) {
				widget.scripts = new int[scripts][];
				for (int script = 0; script < scripts; script++) {
					int instructions = buffer.readUShort();
					widget.scripts[script] = new int[instructions];
					for (int instruction = 0; instruction < instructions; instruction++)
						widget.scripts[script][instruction] = buffer.readUShort();

				}

			}
			if (widget.type == TYPE_CONTAINER) {
				widget.drawsTransparent = false;
				widget.scrollMax = buffer.readUShort();
				widget.invisible = buffer.readUnsignedByte() == 1;
				int length = buffer.readUShort();
				widget.children = new int[length];
				widget.childX = new int[length];
				widget.childY = new int[length];
				for (int index = 0; index < length; index++) {
					widget.children[index] = buffer.readUShort();
					widget.childX[index] = buffer.readShort();
					widget.childY[index] = buffer.readShort();
				}
			}
			if (widget.type == TYPE_MODEL_LIST) {
				buffer.readUShort();
				buffer.readUnsignedByte();
			}
			if (widget.type == TYPE_INVENTORY) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.aBoolean259 = buffer.readUnsignedByte() == 1;
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.usableItems = buffer.readUnsignedByte() == 1;
				widget.replaceItems = buffer.readUnsignedByte() == 1;
				widget.spritePaddingX = buffer.readUnsignedByte();
				widget.spritePaddingY = buffer.readUnsignedByte();
				widget.spritesX = new int[20];
				widget.spritesY = new int[20];
				widget.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = buffer.readUnsignedByte();
					if (k3 == 1) {
						widget.spritesX[j2] = buffer.readShort();
						widget.spritesY[j2] = buffer.readShort();
						String s1 = buffer.readString();
						if (graphics != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");

							int index = Integer.parseInt(s1.substring(i5 + 1));

							String name = s1.substring(0, i5);

							widget.sprites[j2] = getSprite(index, graphics, name);
						}
					}
				}
				widget.actions = new String[5];
				for (int actionIndex = 0; actionIndex < 5; actionIndex++) {
					widget.actions[actionIndex] = buffer.readString();
					if (widget.actions[actionIndex].length() == 0)
						widget.actions[actionIndex] = null;
					if (widget.parent == 1644)
						widget.actions[2] = "Operate";
				}
			}
			if (widget.type == TYPE_RECTANGLE)
				widget.filled = buffer.readUnsignedByte() == 1;
			if (widget.type == TYPE_TEXT || widget.type == TYPE_MODEL_LIST) {
				widget.centerText = buffer.readUnsignedByte() == 1;
				int k2 = buffer.readUnsignedByte();
				if (textDrawingAreas != null)
					widget.textDrawingAreas = textDrawingAreas[k2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
			}

			if (widget.type == TYPE_TEXT) {
				widget.defaultText = buffer.readString().replaceAll("RuneScape", Configuration.CLIENT_NAME);
				if (widget.id == 19209) {
					widget.defaultText.replaceAll("Total", "");
				}
				widget.secondaryText = buffer.readString();
			}

			if (widget.type == TYPE_MODEL_LIST || widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT)
				widget.textColor = buffer.readInt();
			if (widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT) {
				widget.secondaryColor = buffer.readInt();
				widget.defaultHoverColor = buffer.readInt();
				widget.secondaryHoverColor = buffer.readInt();
			}
			if (widget.type == TYPE_SPRITE) {
				widget.drawsTransparent = false;
				String name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.disabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
				name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.enabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
			}
			if (widget.type == TYPE_MODEL) {
				int content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.defaultMediaType = 1;
					widget.defaultMedia = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.anInt255 = 1;
					widget.anInt256 = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.defaultAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.defaultAnimationId = -1;
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.secondaryAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.secondaryAnimationId = -1;
				widget.modelZoom = buffer.readUShort();
				widget.modelRotation1 = buffer.readUShort();
				widget.modelRotation2 = buffer.readUShort();
			}
			if (widget.type == TYPE_ITEM_LIST) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.centerText = buffer.readUnsignedByte() == 1;
				int l2 = buffer.readUnsignedByte();
				if (textDrawingAreas != null)
					widget.textDrawingAreas = textDrawingAreas[l2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
				widget.textColor = buffer.readInt();
				widget.spritePaddingX = buffer.readShort();
				widget.spritePaddingY = buffer.readShort();
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.actions = new String[5];
				for (int actionCount = 0; actionCount < 5; actionCount++) {
					widget.actions[actionCount] = buffer.readString();
					if (widget.actions[actionCount].length() == 0)
						widget.actions[actionCount] = null;
				}

			}
			if (widget.optionType == OPTION_USABLE || widget.type == TYPE_INVENTORY) {
				widget.selectedActionName = buffer.readString();
				widget.spellName = buffer.readString();
				widget.spellUsableOn = buffer.readUShort();
			}

			if (widget.type == 8)
				widget.defaultText = buffer.readString();

			if (widget.optionType == OPTION_OK || widget.optionType == OPTION_TOGGLE_SETTING
					|| widget.optionType == OPTION_RESET_SETTING || widget.optionType == OPTION_CONTINUE) {
				widget.tooltip = buffer.readString();
				if (widget.tooltip.length() == 0) {
					// TODO
					if (widget.optionType == OPTION_OK)
						widget.tooltip = "Ok";
					if (widget.optionType == OPTION_TOGGLE_SETTING)
						widget.tooltip = "Select";
					if (widget.optionType == OPTION_RESET_SETTING)
						widget.tooltip = "Select";
					if (widget.optionType == OPTION_CONTINUE)
						widget.tooltip = "Continue";
				}
			}
		}
		interfaceLoader = interfaces;
		clanChatTab(textDrawingAreas);
		configureLunar(textDrawingAreas);
		quickCurses(textDrawingAreas);
		quickPrayers(textDrawingAreas);
		edgevilleHomeTeleport(textDrawingAreas);
		equipmentScreen(textDrawingAreas);
		equipmentTab(textDrawingAreas);
		itemsOnDeathDATA(textDrawingAreas);
		itemsKeptOnDeath(textDrawingAreas);
		itemsOnDeath(textDrawingAreas);

		repositionModernSpells();

		spriteCache = null;
	}

	public static void debugInterface() {
		Widget widget = Widget.interfaceCache[12424];
		for (int i = 0; i < widget.children.length; i++) {
			System.out.println("childX: " + widget.childX[i] + " childY: " + widget.childY[i] + " index: " + i
					+ " spellId: " + widget.children[i]);
		}
	}

	public static void repositionModernSpells() {

		Widget widget = Widget.interfaceCache[12424];
		for (int index = 0; index < widget.children.length; index++) {

			switch (widget.children[index]) {

			case 1185:
				widget.childX[33] = 148;
				widget.childY[33] = 150;
				break;

			case 1183: // wind wave
				widget.childX[31] = 76;
				widget.childY[31] = 149;
				break;

			case 1188: // earth wave
				widget.childX[36] = 71;
				widget.childY[36] = 172;
				break;

			case 1543:
				widget.childX[46] = 96;
				widget.childY[46] = 173;
				break;

			case 1193: // charge
				widget.childX[41] = 49;
				widget.childY[41] = 198;
				break;

			case 12435: // tele other falador
				widget.childX[54] = 74;
				widget.childY[54] = 198;
				break;

			case 12445: // teleblock
				widget.childX[55] = 99;
				widget.childY[55] = 198;
				break;

			case 6003: // lvl 6 enchant
				widget.childX[57] = 122;
				widget.childY[57] = 198;
				break;

			// 150 x is end of the line

			case 12455: // tele other camelot
				widget.childX[56] = 147;
				widget.childY[56] = 198;
				break;
			}
		}
	}

	public static void itemsKeptOnDeath(GameFont[] tda) {
		Widget Interface = addInterface(22030);
		addSprite(22031, 214);
		addHoverButton(22032, 215, 17, 17, "Close", 250, 22033, 3);
		addHoveredButton(22033, 216, 17, 17, 22034);
		addText(22035, "", tda, 0, 0xff981f, false, true);
		addText(22036, "", tda, 0, 0xff981f, false, true);
		addText(22037, "", tda, 0, 0xff981f, false, true);
		addText(22038, "", tda, 0, 0xff981f, false, true);
		addText(22039, "", tda, 0, 0xff981f, false, true);
		addText(22040, "", tda, 1, 0xffcc33, false, true);
		setChildren(9, Interface);
		setBounds(22031, 7, 8, 0, Interface);
		setBounds(22032, 480, 18, 1, Interface);
		setBounds(22033, 480, 18, 2, Interface);
		setBounds(22035, 348, 98, 3, Interface);
		setBounds(22036, 348, 110, 4, Interface);
		setBounds(22037, 348, 122, 5, Interface);
		setBounds(22038, 348, 134, 6, Interface);
		setBounds(22039, 348, 146, 7, Interface);
		setBounds(22040, 398, 297, 8, Interface);
	}

	public static void clanChatTab(GameFont[] tda) {
		Widget tab = addTabInterface(37128);
		addHoverButton(37129, 208, 72, 32, "Join Chat", -1, 37130, 5);
		addHoveredButton(37130, 209, 72, 32, 37131);
		addHoverButton(37132, 208, 72, 32, "Clan Setup", -1, 37133, 5);
		addHoveredButton(37133, 209, 72, 32, 37134);
		addText(37135, "Join Chat", tda, 0, 0xff9b00, true, true);
		addText(37136, "Clan Setup", tda, 0, 0xff9b00, true, true);
		addSprite(37137, 210);
		addText(37138, "Clan Chat", tda, 1, 0xff9b00, true, true);
		addText(37139, "Talking in: Not in chat", tda, 0, 0xff9b00, false, true);
		addText(37140, "Owner: None", tda, 0, 0xff9b00, false, true);
		tab.totalChildren(13);
		tab.child(0, 16126, 0, 221);
		tab.child(1, 16126, 0, 59);
		tab.child(2, 37137, 0, 62);
		tab.child(3, 37143, 0, 62);
		tab.child(4, 37129, 15, 226);
		tab.child(5, 37130, 15, 226);
		tab.child(6, 37132, 103, 226);
		tab.child(7, 37133, 103, 226);
		tab.child(8, 37135, 51, 237);
		tab.child(9, 37136, 139, 237);
		tab.child(10, 37138, 95, 1);
		tab.child(11, 37139, 10, 23);
		tab.child(12, 37140, 25, 38);
		/* Text area */
		Widget list = addTabInterface(37143);
		list.totalChildren(100);
		for (int i = 37144; i <= 37244; i++) {
			addText(i, "", tda, 0, 0xffffff, false, true);
		}
		for (int id = 37144, i = 0; id <= 37243 && i <= 99; id++, i++) {
			interfaceCache[id].actions = new String[] { "Kick" };
			list.children[i] = id;
			list.childX[i] = 5;
			for (int id2 = 37144, i2 = 1; id2 <= 37243 && i2 <= 99; id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}
		list.height = 158;
		list.width = 174;
		list.scrollMax = 1405;
	}

	public static void addHoverText2(int id, String text, String[] tooltips, GameFont tda[], int idx, int color,
			boolean center, boolean textShadowed, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.optionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xffffff;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltips = tooltips;
	}

	public static void addText2(int id, String text, GameFont tda[], int idx, int color, boolean center,
			boolean shadow) {
		Widget tab = addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.optionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
	}

	public static void addSprite(int interfaceId, int disabledSpriteId, int enabledSpriteId) {
		Widget rsinterface = interfaceCache[interfaceId] = new Widget();		
		rsinterface.id = interfaceId;
		rsinterface.parent = interfaceId;
		rsinterface.type = 5;
		rsinterface.optionType = 1;
		rsinterface.contentType = 0;
		rsinterface.width = 20;
		rsinterface.height = 20;
		rsinterface.opacity = 0;
		rsinterface.mOverInterToTrigger = 52;
		rsinterface.disabledSprite = SpriteCache.lookup(disabledSpriteId);	
		rsinterface.enabledSprite = SpriteCache.lookup(enabledSpriteId);		
	}

	public static void addText(int id, String text, GameFont wid[], int idx, int color) {
		Widget rsinterface = addTabInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.optionType = 0;
		rsinterface.width = 174;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.mOverInterToTrigger = -1;
		rsinterface.centerText = false;
		rsinterface.textShadow = true;
		rsinterface.textDrawingAreas = wid[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0;
		rsinterface.secondaryHoverColor = 0;
	}

	public static void itemsOnDeath(GameFont[] wid) {
		Widget rsinterface = addInterface(17100);
		addSprite(17101, 102);
		// addHover(17102,"Items Kept On Death/SPRITE", 1, 17, 17, "Close", 0,
		// 10602, 1);
		// addHovered(10602,"Items Kept On Death/SPRITE", 3, 17, 17, 10603);
		addText(17103, "Items kept on death", wid, 2, 0xff981f);
		addText(17104, "Items I will keep...", wid, 1, 0xff981f);
		addText(17105, "Items I will lose...", wid, 1, 0xff981f);
		addText(17106, "Info", wid, 1, 0xff981f);
		addText(17107, "", wid, 1, 0xffcc33);
		addText(17108, "", wid, 1, 0xffcc33);
		// rsinterface.scrollMax = 50;
		rsinterface.interfaceShown = false;
		rsinterface.children = new int[12];
		rsinterface.childX = new int[12];
		rsinterface.childY = new int[12];

		rsinterface.children[0] = 17101;
		rsinterface.childX[0] = 7;
		rsinterface.childY[0] = 8;
		rsinterface.children[1] = 15210;
		rsinterface.childX[1] = 478;
		rsinterface.childY[1] = 17;
		rsinterface.children[2] = 17103;
		rsinterface.childX[2] = 185;
		rsinterface.childY[2] = 18;
		rsinterface.children[3] = 17104;
		rsinterface.childX[3] = 22;
		rsinterface.childY[3] = 49;
		rsinterface.children[4] = 17105;
		rsinterface.childX[4] = 22;
		rsinterface.childY[4] = 109;
		rsinterface.children[5] = 17106;
		rsinterface.childX[5] = 347;
		rsinterface.childY[5] = 49;
		rsinterface.children[6] = 17107;
		rsinterface.childX[6] = 348;
		rsinterface.childY[6] = 270;
		rsinterface.children[7] = 17108;
		rsinterface.childX[7] = 401;
		rsinterface.childY[7] = 293;
		rsinterface.children[8] = 17115;
		rsinterface.childX[8] = 348;
		rsinterface.childY[8] = 64;
		rsinterface.children[9] = 10494;
		rsinterface.childX[9] = 26;
		rsinterface.childY[9] = 71;
		rsinterface.children[10] = 10600;
		rsinterface.childX[10] = 26;
		rsinterface.childY[10] = 129;
		rsinterface.children[11] = 15211;
		rsinterface.childX[11] = 478;
		rsinterface.childY[11] = 17;
		rsinterface = interfaceCache[10494];
		rsinterface.spritePaddingX = 6;
		rsinterface.spritePaddingY = 5;
		rsinterface = interfaceCache[10600];
		rsinterface.spritePaddingX = 6;
		rsinterface.spritePaddingY = 5;
	}

	public static void itemsOnDeathDATA(GameFont[] tda) {
		Widget RSinterface = addInterface(17115);
		addText(17109, "", 0xff981f, false, false, 0, tda, 0);
		addText(17110, "The normal amount of", 0xff981f, false, false, 0, tda, 0);
		addText(17111, "items kept is three.", 0xff981f, false, false, 0, tda, 0);
		addText(17112, "", 0xff981f, false, false, 0, tda, 0);
		addText(17113, "If you are skulled,", 0xff981f, false, false, 0, tda, 0);
		addText(17114, "you will lose all your", 0xff981f, false, false, 0, tda, 0);
		addText(17117, "items, unless an item", 0xff981f, false, false, 0, tda, 0);
		addText(17118, "protecting prayer is", 0xff981f, false, false, 0, tda, 0);
		addText(17119, "used.", 0xff981f, false, false, 0, tda, 0);
		addText(17120, "", 0xff981f, false, false, 0, tda, 0);
		addText(17121, "Item protecting prayers", 0xff981f, false, false, 0, tda, 0);
		addText(17122, "will allow you to keep", 0xff981f, false, false, 0, tda, 0);
		addText(17123, "one extra item.", 0xff981f, false, false, 0, tda, 0);
		addText(17124, "", 0xff981f, false, false, 0, tda, 0);
		addText(17125, "The items kept are", 0xff981f, false, false, 0, tda, 0);
		addText(17126, "selected by the server", 0xff981f, false, false, 0, tda, 0);
		addText(17127, "and include the most", 0xff981f, false, false, 0, tda, 0);
		addText(17128, "expensive items you're", 0xff981f, false, false, 0, tda, 0);
		addText(17129, "carrying.", 0xff981f, false, false, 0, tda, 0);
		addText(17130, "", 0xff981f, false, false, 0, tda, 0);
		RSinterface.parent = 17115;
		RSinterface.id = 17115;
		RSinterface.type = 0;
		RSinterface.optionType = 0;
		RSinterface.contentType = 0;
		RSinterface.width = 130;
		RSinterface.height = 197;
		RSinterface.opacity = 0;
		RSinterface.hoverType = -1;
		RSinterface.scrollMax = 280;
		RSinterface.children = new int[20];
		RSinterface.childX = new int[20];
		RSinterface.childY = new int[20];
		RSinterface.children[0] = 17109;
		RSinterface.childX[0] = 0;
		RSinterface.childY[0] = 0;
		RSinterface.children[1] = 17110;
		RSinterface.childX[1] = 0;
		RSinterface.childY[1] = 12;
		RSinterface.children[2] = 17111;
		RSinterface.childX[2] = 0;
		RSinterface.childY[2] = 24;
		RSinterface.children[3] = 17112;
		RSinterface.childX[3] = 0;
		RSinterface.childY[3] = 36;
		RSinterface.children[4] = 17113;
		RSinterface.childX[4] = 0;
		RSinterface.childY[4] = 48;
		RSinterface.children[5] = 17114;
		RSinterface.childX[5] = 0;
		RSinterface.childY[5] = 60;
		RSinterface.children[6] = 17117;
		RSinterface.childX[6] = 0;
		RSinterface.childY[6] = 72;
		RSinterface.children[7] = 17118;
		RSinterface.childX[7] = 0;
		RSinterface.childY[7] = 84;
		RSinterface.children[8] = 17119;
		RSinterface.childX[8] = 0;
		RSinterface.childY[8] = 96;
		RSinterface.children[9] = 17120;
		RSinterface.childX[9] = 0;
		RSinterface.childY[9] = 108;
		RSinterface.children[10] = 17121;
		RSinterface.childX[10] = 0;
		RSinterface.childY[10] = 120;
		RSinterface.children[11] = 17122;
		RSinterface.childX[11] = 0;
		RSinterface.childY[11] = 132;
		RSinterface.children[12] = 17123;
		RSinterface.childX[12] = 0;
		RSinterface.childY[12] = 144;
		RSinterface.children[13] = 17124;
		RSinterface.childX[13] = 0;
		RSinterface.childY[13] = 156;
		RSinterface.children[14] = 17125;
		RSinterface.childX[14] = 0;
		RSinterface.childY[14] = 168;
		RSinterface.children[15] = 17126;
		RSinterface.childX[15] = 0;
		RSinterface.childY[15] = 180;
		RSinterface.children[16] = 17127;
		RSinterface.childX[16] = 0;
		RSinterface.childY[16] = 192;
		RSinterface.children[17] = 17128;
		RSinterface.childX[17] = 0;
		RSinterface.childY[17] = 204;
		RSinterface.children[18] = 17129;
		RSinterface.childX[18] = 0;
		RSinterface.childY[18] = 216;
		RSinterface.children[19] = 17130;
		RSinterface.childX[19] = 0;
		RSinterface.childY[19] = 228;
	}

	public static void equipmentTab(GameFont[] wid) {
		Widget Interface = interfaceCache[1644];

		removeConfig(21338);
		removeConfig(21344);
		removeConfig(21342);
		removeConfig(21341);
		removeConfig(21340);
		removeConfig(15103);
		removeConfig(15104);
		removeConfig(15109);

		Interface.children[24] = 15102;
		Interface.childX[24] = 110;
		Interface.childY[24] = 205;
		Interface.children[25] = 15109;
		Interface.childX[25] = 39;
		Interface.childY[25] = 240;
		Interface.children[26] = 27650;
		Interface.childX[26] = 0;
		Interface.childY[26] = 0;
		Interface = addInterface(27650);

		addHoverButton(27651, 202, 40, 40, "Price-checker", -1, 27652, 1);
		addHoveredButton(27652, 203, 40, 40, 27658);

		addHoverButton(27653, 204, 40, 40, "Show Equipment Stats", -1, 27655, 1);
		addHoveredButton(27655, 205, 40, 40, 27665);

		addHoverButton(27654, 206, 40, 40, "Show items kept on death", -1, 27657, 1);
		addHoveredButton(27657, 207, 40, 40, 27666);

		setChildren(6, Interface);
		setBounds(27651, 75, 205, 0, Interface);
		setBounds(27652, 75, 205, 1, Interface);
		setBounds(27653, 23, 205, 2, Interface);
		setBounds(27654, 127, 205, 3, Interface);
		setBounds(27655, 23, 205, 4, Interface);
		setBounds(27657, 127, 205, 5, Interface);
	}

	public static void removeConfig(int id) {
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}

	public static void addHoverText(int id, String text, String tooltip, GameFont tda[], int idx, int color,
			boolean centerText, boolean textShadowed, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.optionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.mOverInterToTrigger = -1;
		rsinterface.centerText = centerText;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xffffff;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltip = tooltip;
	}

	public static void equipmentScreen(GameFont[] wid) {
		Widget Interface = Widget.interfaceCache[1644];
		addButton(19144, 217, "Show Equipment Stats");
		removeComponent(19145);
		removeComponent(19146);
		removeComponent(19147);
		setBounds(19145, 40, 210, 24, Interface);
		setBounds(19146, 40, 210, 25, Interface);
		setBounds(19147, 40, 210, 26, Interface);
		Widget tab = addTabInterface(15106);
		addSprite(15107, 211);
		addHoverButton(15210, 212, 21, 21, "Close", 250, 15211, 3);
		addHoveredButton(15211, 213, 21, 21, 15212);
		addText(15111, "Equip Your Character...", wid, 2, 0xe4a146, false, true);
		addText(15112, "Attack bonus", wid, 2, 0xe4a146, false, true);
		addText(15113, "Defence bonus", wid, 2, 0xe4a146, false, true);
		addText(15114, "Other bonuses", wid, 2, 0xe4a146, false, true);
		
		for (int i = 1675; i <= 1684; i++) {
			textSize(i, wid, 1);
		}
		
		textSize(1686, wid, 1);
		textSize(1687, wid, 1);
		addChar(15125);
		tab.totalChildren(44);
		tab.child(0, 15107, 4, 20);
		tab.child(1, 15210, 476, 29);
		tab.child(2, 15211, 476, 29);
		tab.child(3, 15111, 14, 30);
		int Child = 4;
		int Y = 69;
		for (int i = 1675; i <= 1679; i++) {
			tab.child(Child, i, 20, Y);
			Child++;
			Y += 14;
		}
		tab.child(9, 1680, 20, 161);
		tab.child(10, 1681, 20, 177);
		tab.child(11, 1682, 20, 192);
		tab.child(12, 1683, 20, 207);
		tab.child(13, 1684, 20, 221);
		tab.child(14, 1686, 20, 262);
		tab.child(15, 15125, 170, 200);
		tab.child(16, 15112, 16, 55);
		tab.child(17, 1687, 20, 276);
		tab.child(18, 15113, 16, 147);
		tab.child(19, 15114, 16, 248);
		tab.child(20, 1645, 104 + 295, 149 - 52);
		tab.child(21, 1646, 399, 163);
		tab.child(22, 1647, 399, 163);
		tab.child(23, 1648, 399, 58 + 146);
		tab.child(24, 1649, 26 + 22 + 297 - 2, 110 - 44 + 118 - 13 + 5);
		tab.child(25, 1650, 321 + 22, 58 + 154);
		tab.child(26, 1651, 321 + 134, 58 + 118);
		tab.child(27, 1652, 321 + 134, 58 + 154);
		tab.child(28, 1653, 321 + 48, 58 + 81);
		tab.child(29, 1654, 321 + 107, 58 + 81);
		tab.child(30, 1655, 321 + 58, 58 + 42);
		tab.child(31, 1656, 321 + 112, 58 + 41);
		tab.child(32, 1657, 321 + 78, 58 + 4);
		tab.child(33, 1658, 321 + 37, 58 + 43);
		tab.child(34, 1659, 321 + 78, 58 + 43);
		tab.child(35, 1660, 321 + 119, 58 + 43);
		tab.child(36, 1661, 321 + 22, 58 + 82);
		tab.child(37, 1662, 321 + 78, 58 + 82);
		tab.child(38, 1663, 321 + 134, 58 + 82);
		tab.child(39, 1664, 321 + 78, 58 + 122);
		tab.child(40, 1665, 321 + 78, 58 + 162);
		tab.child(41, 1666, 321 + 22, 58 + 162);
		tab.child(42, 1667, 321 + 134, 58 + 162);
		tab.child(43, 1688, 50 + 297 - 2, 110 - 13 + 5);
		for (int i = 1675; i <= 1684; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xe4a146;
			rsi.centerText = false;
		}
		for (int i = 1686; i <= 1687; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xe4a146;
			rsi.centerText = false;
		}
	}

	public static void addChar(int ID) {
		Widget t = interfaceCache[ID] = new Widget();
		t.id = ID;
		t.parent = ID;
		t.type = 6;
		t.optionType = 0;
		t.contentType = 328;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.mOverInterToTrigger = 0;
		t.modelZoom = 560;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.defaultAnimationId = -1;
		t.secondaryAnimationId = -1;
	}

	public static void edgevilleHomeTeleport(GameFont[] TDA) {
		Widget rsi = interfaceCache[21741];
		rsi.optionType = 1;
		rsi.tooltip = "Cast @gre@Edgeville Home Teleport";
	}

	public static void addButton(int id, int sid, String tooltip) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.optionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(sid);
		tab.enabledSprite = SpriteCache.lookup(sid);
		tab.width = tab.disabledSprite.getMyWidth();
		tab.height = tab.enabledSprite.getMyHeight();
		tab.tooltip = tooltip;
	}

	public String popupString;

	public static void addTooltipBox(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.parent = id;
		rsi.type = 8;
		rsi.popupString = text;
	}

	public static void addTooltip(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.type = 0;
		rsi.invisible = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	public static Widget addInterface(int id) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.id = id;
		rsi.parent = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static void addText(int id, String text, GameFont tda[], int idx, int color, boolean centered) {
		Widget rsi = interfaceCache[id] = new Widget();
		if (centered)
			rsi.centerText = true;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.defaultText = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		Widget rsi = interfaceCache[id];
		rsi.textColor = color;
	}

	public static void textSize(int id, GameFont tda[], int idx) {
		Widget rsi = interfaceCache[id];
		rsi.textDrawingAreas = tda[idx];
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.disabledSprite = getSprite(sprite1, interfaceLoader, sprites);
		rsi.enabledSprite = getSprite(sprite2, interfaceLoader, sprites);
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public static void removeComponent(int id) {		
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}

	public static void quickCurses(GameFont[] TDA) {
		Widget tab = addTabInterface(17234);
		addTransparentSprite(17229, 96, 50);
		addSprite(17201, 99);
		addText(17230, "Select your quick prayers:", TDA, 0, 0xFF981F, false, true);
		
		for (int i = 17202, j = 630; i <= 17228 || j <= 656; i++, j++) {
			addConfigButton(i, 17200, 98, 97, 14, 15, "Select", 0, 1, j);
		}
		
		addHoverButton(17231, 100, 190, 24, "Confirm Selection", -1, 17232, 1);
		addHoveredButton(17232, 101, 190, 24, 17233);
		int frame = 0;
		setChildren(46, tab);
		setBounds(21358, 11, 8 + 20, frame++, tab);
		setBounds(21360, 50, 11 + 20, frame++, tab);
		setBounds(21362, 87, 11 + 20, frame++, tab);
		setBounds(21364, 122, 10 + 20, frame++, tab);
		setBounds(21366, 159, 11 + 20, frame++, tab);
		setBounds(21368, 12, 45 + 20, frame++, tab);
		setBounds(21370, 46, 45 + 20, frame++, tab);
		setBounds(21372, 83, 46 + 20, frame++, tab);
		setBounds(21374, 119, 45 + 20, frame++, tab);
		setBounds(21376, 157, 45 + 20, frame++, tab);
		setBounds(21378, 11, 83 + 20, frame++, tab);
		setBounds(21380, 49, 84 + 20, frame++, tab);
		setBounds(21382, 84, 83 + 20, frame++, tab);
		setBounds(21384, 123, 84 + 20, frame++, tab);
		setBounds(21386, 159, 83 + 20, frame++, tab);
		setBounds(21388, 12, 119 + 20, frame++, tab);
		setBounds(21390, 49, 119 + 20, frame++, tab);
		setBounds(21392, 88, 119 + 20, frame++, tab);
		setBounds(21394, 122, 121 + 20, frame++, tab);
		setBounds(21396, 155, 122 + 20, frame++, tab);
		setBounds(17229, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split
		setBounds(17202, 13 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 52 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 90 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 126 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 162 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 13 - 3, 45 + 17, frame++, tab);
		setBounds(17208, 52 - 3, 45 + 17, frame++, tab);
		setBounds(17209, 90 - 3, 45 + 17, frame++, tab);
		setBounds(17210, 126 - 3, 45 + 17, frame++, tab);
		setBounds(17211, 162 - 3, 45 + 17, frame++, tab);
		setBounds(17212, 13 - 3, 80 + 17, frame++, tab);
		setBounds(17213, 52 - 3, 80 + 17, frame++, tab);
		setBounds(17214, 90 - 3, 80 + 17, frame++, tab);
		setBounds(17215, 126 - 3, 80 + 17, frame++, tab);
		setBounds(17216, 162 - 3, 80 + 17, frame++, tab);
		setBounds(17217, 13 - 3, 119 + 17, frame++, tab);
		setBounds(17218, 52 - 3, 119 + 17, frame++, tab);
		setBounds(17219, 90 - 3, 119 + 17, frame++, tab);
		setBounds(17220, 126 - 3, 119 + 17, frame++, tab);
		setBounds(17221, 162 - 3, 119 + 17, frame++, tab);
		setBounds(17230, 5, 5, frame++, tab);// text
		setBounds(17231, 0, 237, frame++, tab);// confirm
		setBounds(17232, 0, 237, frame++, tab);// Confirm hover
	}

	public static void quickPrayers(GameFont[] TDA) {
		int frame = 0;
		Widget tab = addTabInterface(17200);

		setChildren(58, tab);//
		setBounds(5632, 5, 8 + 20, frame++, tab);
		setBounds(5633, 44, 8 + 20, frame++, tab);
		setBounds(5634, 79, 11 + 20, frame++, tab);
		setBounds(19813, 116, 10 + 20, frame++, tab);
		setBounds(19815, 153, 9 + 20, frame++, tab);
		setBounds(5635, 5, 48 + 20, frame++, tab);
		setBounds(5636, 44, 47 + 20, frame++, tab);
		setBounds(5637, 79, 49 + 20, frame++, tab);
		setBounds(5638, 116, 50 + 20, frame++, tab);
		setBounds(5639, 154, 50 + 20, frame++, tab);
		setBounds(5640, 4, 84 + 20, frame++, tab);
		setBounds(19817, 44, 87 + 20, frame++, tab);
		setBounds(19820, 81, 85 + 20, frame++, tab);
		setBounds(5641, 117, 85 + 20, frame++, tab);
		setBounds(5642, 156, 87 + 20, frame++, tab);
		setBounds(5643, 5, 125 + 20, frame++, tab);
		setBounds(5644, 43, 124 + 20, frame++, tab);
		setBounds(13984, 83, 124 + 20, frame++, tab);
		setBounds(5645, 115, 121 + 20, frame++, tab);
		setBounds(19822, 154, 124 + 20, frame++, tab);
		setBounds(19824, 5, 160 + 20, frame++, tab);
		setBounds(5649, 41, 158 + 20, frame++, tab);
		setBounds(5647, 79, 163 + 20, frame++, tab);
		setBounds(5648, 116, 158 + 20, frame++, tab);
		setBounds(19826, 161, 160 + 20, frame++, tab);
		setBounds(19828, 4, 207 + 12, frame++, tab);

		setBounds(17229, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split

		setBounds(17202, 5 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 44 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 79 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 116 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 153 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 5 - 3, 48 + 17, frame++, tab);
		setBounds(17208, 44 - 3, 48 + 17, frame++, tab);
		setBounds(17209, 79 - 3, 48 + 17, frame++, tab);
		setBounds(17210, 116 - 3, 48 + 17, frame++, tab);
		setBounds(17211, 153 - 3, 48 + 17, frame++, tab);
		setBounds(17212, 5 - 3, 85 + 17, frame++, tab);
		setBounds(17213, 44 - 3, 85 + 17, frame++, tab);
		setBounds(17214, 79 - 3, 85 + 17, frame++, tab);
		setBounds(17215, 116 - 3, 85 + 17, frame++, tab);
		setBounds(17216, 153 - 3, 85 + 17, frame++, tab);
		setBounds(17217, 5 - 3, 124 + 17, frame++, tab);
		setBounds(17218, 44 - 3, 124 + 17, frame++, tab);
		setBounds(17219, 79 - 3, 124 + 17, frame++, tab);
		setBounds(17220, 116 - 3, 124 + 17, frame++, tab);
		setBounds(17221, 153 - 3, 124 + 17, frame++, tab);
		setBounds(17222, 5 - 3, 160 + 17, frame++, tab);
		setBounds(17223, 44 - 3, 160 + 17, frame++, tab);
		setBounds(17224, 79 - 3, 160 + 17, frame++, tab);
		setBounds(17225, 116 - 3, 160 + 17, frame++, tab);
		setBounds(17226, 153 - 3, 160 + 17, frame++, tab);
		setBounds(17227, 4 - 3, 207 + 4, frame++, tab);

		setBounds(17230, 5, 5, frame++, tab);// text
		setBounds(17231, 0, 237, frame++, tab);// confirm
		setBounds(17232, 0, 237, frame++, tab);// Confirm hover
	}

	public int transparency;

	private static void addTransparentSprite(int id, int spriteId, int transparency) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.optionType = 0;
		tab.contentType = 0;
		tab.transparency = (byte) transparency;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(spriteId);
		tab.enabledSprite = SpriteCache.lookup(spriteId);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static void Pestpanel(GameFont[] tda) {
		Widget RSinterface = addInterface(21119);
		addText(21120, "What", 0x999999, false, true, 52, tda, 1);
		addText(21121, "What", 0x33cc00, false, true, 52, tda, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, false, true, 52, tda, 1);
		addText(21123, "Points", 0x33ccff, false, true, 52, tda, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21120, 15, 12, 0, RSinterface);
		setBounds(21121, 15, 30, 1, RSinterface);
		setBounds(21122, 15, 48, 2, RSinterface);
		setBounds(21123, 15, 66, 3, RSinterface);
	}

	public String hoverText;

	public static void addHoverBox(int id, int ParentID, String text, String text2, int configId, int configFrame) {
		Widget rsi = addTabInterface(id);
		rsi.id = id;
		rsi.parent = ParentID;
		rsi.type = 8;
		rsi.secondaryText = text;
		rsi.defaultText = text2;
		rsi.scriptOperators = new int[1];
		rsi.scriptDefaults = new int[1];
		rsi.scriptOperators[0] = 1;
		rsi.scriptDefaults[0] = configId;
		rsi.scripts = new int[1][3];
		rsi.scripts[0][0] = 5;
		rsi.scripts[0][1] = configFrame;
		rsi.scripts[0][2] = 0;
	}

	public static void addText(int id, String text, GameFont tda[], int idx, int color, boolean center,
			boolean shadow) {
		Widget tab = addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.optionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m, int a, GameFont[] TDA, int j) {
		Widget RSInterface = addInterface(i);
		RSInterface.parent = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.optionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = a;
		RSInterface.centerText = l;
		RSInterface.textShadow = m;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.defaultText = s;
		RSInterface.secondaryText = "";
		RSInterface.textColor = k;
	}

	public static void addButton(int id, int sid, String tooltip, int w, int h) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.optionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(sid);
		tab.enabledSprite = SpriteCache.lookup(sid);
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addConfigButton(int ID, int pID, int disabledSpriteId, int enabledSpriteId, int width, int height,
			String tT, int configID, int aT, int configFrame) {
		Widget Tab = addTabInterface(ID);		
		Tab.parent = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.optionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.scriptOperators = new int[1];
		Tab.scriptDefaults = new int[1];
		Tab.scriptOperators[0] = 1;
		Tab.scriptDefaults[0] = configID;
		Tab.scripts = new int[1][3];
		Tab.scripts[0][0] = 5;
		Tab.scripts[0][1] = configFrame;
		Tab.scripts[0][2] = 0;
		Tab.disabledSprite = SpriteCache.lookup(disabledSpriteId);
		Tab.enabledSprite = SpriteCache.lookup(enabledSpriteId);		
		Tab.tooltip = tT;
	}

	public static void addSprite(int id, int spriteId) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.optionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(spriteId);
		tab.enabledSprite = SpriteCache.lookup(spriteId);
		tab.width = 512;
		tab.height = 334;
	}

	public static void addHoverButton(int i, int spriteId, int width, int height, String text,
			int contentType, int hoverOver, int aT) {
		
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.optionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = SpriteCache.lookup(spriteId);
		tab.enabledSprite = SpriteCache.lookup(spriteId);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, int j, int w, int h, int spriteId) {		
		Widget tab = addTabInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.optionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(spriteId, j, j);
		tab.totalChildren(1);
		tab.child(0, spriteId, 0, 0);
	}

	public static void addHoverImage(int i, int disabledSpriteId, int enabledSpriteId) {		
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.optionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(disabledSpriteId);
		tab.enabledSprite = SpriteCache.lookup(enabledSpriteId);
	}

	public static void addTransparentSprite(int id, int spriteId) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.optionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = SpriteCache.lookup(spriteId);
		tab.enabledSprite = SpriteCache.lookup(spriteId);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static Widget addScreenInterface(int id) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 0;
		tab.optionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = (byte) 0;
		tab.hoverType = 0;
		return tab;
	}

	public static Widget addTabInterface(int id) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;// 250
		tab.parent = id;// 236
		tab.type = 0;// 262
		tab.optionType = 0;// 217
		tab.contentType = 0;
		tab.width = 512;// 220
		tab.height = 700;// 267
		tab.opacity = (byte) 0;
		tab.hoverType = -1;// Int 230
		return tab;
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	private Model getModel(int type, int mobId) {
		Model model = (Model) models.get((type << 16) + mobId);
		
		if (model != null) {
			return model;
		}
		
		if (type == 1) {
			model = Model.getModel(mobId);
		}
		
		if (type == 2) {
			model = NpcDefinition.lookup(mobId).model();
		}
		
		if (type == 3) {
			model = Client.localPlayer.getHeadModel();
		}
		
		if (type == 4) {
			model = ItemDefinition.lookup(mobId).getUnshadedModel(50);
		}
		
		if (type == 5) {
			model = null;
		}
		
		if (model != null) {
			models.put(model, (type << 16) + mobId);
		}
		
		return model;
	}

	private static Sprite getSprite(int i, FileArchive streamLoader, String s) {
		long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
		Sprite sprite = (Sprite) spriteCache.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(streamLoader, s, i);
			spriteCache.put(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;// was parameter
		int j = 5;// was parameter
		if (flag)
			return;
		models.clear();
		if (model != null && j != 4)
			models.put(model, (j << 16) + i);
	}

	public Model method209(int j, int k, boolean flag) {
		Model model;
		if (flag)
			model = getModel(anInt255, anInt256);
		else
			model = getModel(defaultMediaType, defaultMedia);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.triangleColours == null)
			return model;
		Model model_1 = new Model(true, Frame.noAnimationInProgress(k) & Frame.noAnimationInProgress(j), false, model);
		if (k != -1 || j != -1)
			model_1.skin();
		if (k != -1)
			model_1.applyTransform(k);
		if (j != -1)
			model_1.applyTransform(j);
		model_1.light(64, 768, -50, -10, -50, true);
		return model_1;
	}


	public Widget() {
	}

	public static FileArchive interfaceLoader;
	public boolean drawsTransparent;
	public Sprite disabledSprite;
	public int lastFrameTime;

	public Sprite sprites[];
	public static Widget interfaceCache[];
	public int scriptDefaults[];
	public int contentType;
	public int spritesX[];
	public int defaultHoverColor;
	public int optionType;
	public String spellName;
	public int secondaryColor;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centerText;
	public int scrollPosition;
	public String actions[];
	public int scripts[][];
	public boolean filled;
	public String secondaryText;
	public int hoverType;
	public int spritePaddingX;
	public int textColor;
	public int defaultMediaType;
	public int defaultMedia;
	public boolean replaceItems;
	public int parent;
	public int spellUsableOn;
	private static ReferenceCache spriteCache;
	public int secondaryHoverColor;
	public int children[];
	public int childX[];
	public boolean usableItems;
	public GameFont textDrawingAreas;
	public int spritePaddingY;
	public int scriptOperators[];
	public int currentFrame;
	public int spritesY[];
	public String defaultText;
	public boolean hasActions;
	public int id;
	public int inventoryAmounts[];
	public int inventoryItemId[];
	public byte opacity;
	private int anInt255;
	private int anInt256;
	public int defaultAnimationId;
	public int secondaryAnimationId;

	public boolean aBoolean259;
	public Sprite enabledSprite;
	public int scrollMax;
	public int type;
	public int x;
	private static final ReferenceCache models = new ReferenceCache(30);
	public int anInt265;
	public boolean invisible;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int childY[];

	public static void addLunarSprite(int i, int spriteId) {
		Widget RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parent = i;
		RSInterface.type = 5;
		RSInterface.optionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = SpriteCache.lookup(spriteId);		
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}

	public static void drawRune(int i, int id, String runeName) {
		Widget RSInterface = addInterface(i);
		RSInterface.type = 5;
		RSInterface.optionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = SpriteCache.lookup(id + 185);
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	public static void addRuneText(int ID, int runeAmount, int RuneID, GameFont[] font) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 4;
		rsInterface.optionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.scriptOperators = new int[1];
		rsInterface.scriptDefaults = new int[1];
		rsInterface.scriptOperators[0] = 3;
		rsInterface.scriptDefaults[0] = runeAmount;
		rsInterface.scripts = new int[1][4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = RuneID;
		rsInterface.scripts[0][3] = 0;
		rsInterface.centerText = true;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.defaultText = "%1/" + runeAmount + "";
		rsInterface.secondaryText = "";
		rsInterface.textColor = 12582912;
		rsInterface.secondaryColor = 49152;
	}

	public static void homeTeleport() {
		Widget RSInterface = addInterface(30000);
		RSInterface.tooltip = "Cast @gre@Lunar Home Teleport";
		RSInterface.id = 30000;
		RSInterface.parent = 30000;
		RSInterface.type = 5;
		RSInterface.optionType = 5;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 30001;
		RSInterface.disabledSprite = SpriteCache.lookup(201);
		RSInterface.width = 20;
		RSInterface.height = 20;
		Widget Int = addInterface(30001);
		Int.invisible = true;
		Int.hoverType = -1;
		setChildren(1, Int);
		addLunarSprite(30002, 200);
		setBounds(30002, 0, 0, 0, Int);
	}

	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name,
			String descr, GameFont[] TDA, int sid, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.optionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.scriptOperators = new int[3];
		rsInterface.scriptDefaults = new int[3];
		rsInterface.scriptOperators[0] = 3;
		rsInterface.scriptDefaults[0] = ra1;
		rsInterface.scriptOperators[1] = 3;
		rsInterface.scriptDefaults[1] = ra2;
		rsInterface.scriptOperators[2] = 3;
		rsInterface.scriptDefaults[2] = lvl;
		rsInterface.scripts = new int[3][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[3];
		rsInterface.scripts[2][0] = 1;
		rsInterface.scripts[2][1] = 6;
		rsInterface.scripts[2][2] = 0;
		rsInterface.enabledSprite = SpriteCache.lookup(sid + 147);
		rsInterface.disabledSprite = SpriteCache.lookup(sid + 108);
		Widget INT = addInterface(ID + 1);
		INT.invisible = true;
		INT.hoverType = -1;
		setChildren(7, INT);
		addLunarSprite(ID + 2, 103);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 37, 35, 3, INT);// Rune
		setBounds(rune1, 112, 35, 4, INT);// Rune
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, INT);
	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int sid, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.optionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.scriptOperators = new int[4];
		rsInterface.scriptDefaults = new int[4];
		rsInterface.scriptOperators[0] = 3;
		rsInterface.scriptDefaults[0] = ra1;
		rsInterface.scriptOperators[1] = 3;
		rsInterface.scriptDefaults[1] = ra2;
		rsInterface.scriptOperators[2] = 3;
		rsInterface.scriptDefaults[2] = ra3;
		rsInterface.scriptOperators[3] = 3;
		rsInterface.scriptDefaults[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.enabledSprite = SpriteCache.lookup(sid + 147);
		rsInterface.disabledSprite = SpriteCache.lookup(sid + 108);
		Widget INT = addInterface(ID + 1);
		INT.invisible = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 103);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 14, 35, 3, INT);
		setBounds(rune1, 74, 35, 4, INT);
		setBounds(rune2, 130, 35, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, INT);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int sid, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.optionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.scriptOperators = new int[4];
		rsInterface.scriptDefaults = new int[4];
		rsInterface.scriptOperators[0] = 3;
		rsInterface.scriptDefaults[0] = ra1;
		rsInterface.scriptOperators[1] = 3;
		rsInterface.scriptDefaults[1] = ra2;
		rsInterface.scriptOperators[2] = 3;
		rsInterface.scriptDefaults[2] = ra3;
		rsInterface.scriptOperators[3] = 3;
		rsInterface.scriptDefaults[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.enabledSprite = SpriteCache.lookup(sid + 147);
		rsInterface.disabledSprite = SpriteCache.lookup(sid + 108);
		Widget INT = addInterface(ID + 1);
		INT.invisible = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 104);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, INT);
		setBounds(30016, 14, 48, 3, INT);
		setBounds(rune1, 74, 48, 4, INT);
		setBounds(rune2, 130, 48, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, INT);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int sid, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.optionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.scriptOperators = new int[4];
		rsInterface.scriptDefaults = new int[4];
		rsInterface.scriptOperators[0] = 3;
		rsInterface.scriptDefaults[0] = ra1;
		rsInterface.scriptOperators[1] = 3;
		rsInterface.scriptDefaults[1] = ra2;
		rsInterface.scriptOperators[2] = 3;
		rsInterface.scriptDefaults[2] = ra3;
		rsInterface.scriptOperators[3] = 3;
		rsInterface.scriptDefaults[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.enabledSprite = SpriteCache.lookup(sid + 147);
		rsInterface.disabledSprite = SpriteCache.lookup(sid + 108);
		Widget INT = addInterface(ID + 1);
		INT.invisible = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 105);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 34, 2, INT);
		setBounds(30016, 14, 61, 3, INT);
		setBounds(rune1, 74, 61, 4, INT);
		setBounds(rune2, 130, 61, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, INT);
	}

	public static void setChildren(int total, Widget i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}

	public static void configureLunar(GameFont[] tda) {
		homeTeleport();
		constructLunar();
		drawRune(30003, 1, "Fire");
		drawRune(30004, 2, "Water");
		drawRune(30005, 3, "Air");
		drawRune(30006, 4, "Earth");
		drawRune(30007, 5, "Mind");
		drawRune(30008, 6, "Body");
		drawRune(30009, 7, "Death");
		drawRune(30010, 8, "Nature");
		drawRune(30011, 9, "Chaos");
		drawRune(30012, 10, "Law");
		drawRune(30013, 11, "Cosmic");
		drawRune(30014, 12, "Blood");
		drawRune(30015, 13, "Soul");
		drawRune(30016, 14, "Astral");
		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Bake Pie",
				"Bake pies without a stove", tda, 0, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant", "Cure disease on farming patch", tda, 1,
				4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster Examine",
				"Detect the combat statistics of a\\nmonster", tda, 2, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "NPC Contact",
				"Speak with varied NPCs", tda, 3, 0, 2);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned players",
				tda, 4, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Humidify",
				"Fills certain vessels with water", tda, 5, 0, 5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Moonclan Teleport",
				"Teleports you to moonclan island", tda, 6, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Tele Group Moonclan",
				"Teleports players to Moonclan\\nisland", tda, 7, 0, 5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Ourania Teleport",
				"Teleports you to ourania rune altar", tda, 8, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", tda, 9, 0,
				5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", tda, 10, 0,
				5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "Waterbirth Teleport",
				"Teleports you to Waterbirth island", tda, 11, 0, 5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth",
				"Teleports players to Waterbirth\\nisland", tda, 12, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group",
				"Cures Poison on players", tda, 13, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy",
				"Cast on another player to see their\\nskill levels", tda, 14, 8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport",
				"Teleports you to the Barbarian\\noutpost", tda, 15, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian",
				"Teleports players to the Barbarian\\noutpost", tda, 16, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make",
				"Make glass without a furnace", tda, 17, 16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport",
				"Teleports you to Port khazard", tda, 18, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard",
				"Teleports players to Port khazard", tda, 19, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Dream",
				"Take a rest and restore hitpoints 3\\n times faster", tda, 20, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery",
				"String amulets without wool", tda, 21, 0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot\\nShare",
				"Share a potion with up to 4 nearby\\nplayers", tda, 22, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Magic Imbue",
				"Combine runes without a talisman", tda, 23, 0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil",
				"Fertilise a farming patch with super\\ncompost", tda, 24, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share",
				"Shares a potion with up to 4 nearby\\nplayers", tda, 25, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fishing Guild Teleport",
				"Teleports you to the fishing guild", tda, 26, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fishing Guild",
				"Teleports players to the Fishing\\nGuild", tda, 27, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks",
				tda, 28, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catherby Teleport",
				"Teleports you to Catherby", tda, 29, 0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catherby",
				"Teleports players to Catherby", tda, 30, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport",
				"Teleports you to Ice Plateau", tda, 31, 0, 5);
		addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau",
				"Teleports players to Ice Plateau", tda, 32, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer",
				"Spend HP and SA energy to\\n give another SA and run energy", tda, 33, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other",
				"Transfer up to 75% of hitpoints\\n to another player", tda, 34, 8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other",
				"Allows another player to rebound\\ndamage to an opponent", tda, 35, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance",
				"Rebound damage to an opponent", tda, 36, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group",
				"Transfer up to 75% of hitpoints\\n to a group", tda, 37, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap",
				"Change to another spellbook for 1\\nspell cast", tda, 38, 0, 5);
	}

	public static void constructLunar() {
		Widget Interface = addTabInterface(29999);
		setChildren(80, Interface);
		setBounds(30000, 11, 10, 0, Interface);
		setBounds(30017, 40, 9, 1, Interface);
		setBounds(30025, 71, 12, 2, Interface);
		setBounds(30032, 103, 10, 3, Interface);
		setBounds(30040, 135, 12, 4, Interface);
		setBounds(30048, 165, 10, 5, Interface);
		setBounds(30056, 8, 38, 6, Interface);
		setBounds(30064, 39, 39, 7, Interface);
		setBounds(30075, 71, 39, 8, Interface);
		setBounds(30083, 103, 39, 9, Interface);
		setBounds(30091, 135, 39, 10, Interface);
		setBounds(30099, 165, 37, 11, Interface);
		setBounds(30106, 12, 68, 12, Interface);
		setBounds(30114, 42, 68, 13, Interface);
		setBounds(30122, 71, 68, 14, Interface);
		setBounds(30130, 103, 68, 15, Interface);
		setBounds(30138, 135, 68, 16, Interface);
		setBounds(30146, 165, 68, 17, Interface);
		setBounds(30154, 14, 97, 18, Interface);
		setBounds(30162, 42, 97, 19, Interface);
		setBounds(30170, 71, 97, 20, Interface);
		setBounds(30178, 101, 97, 21, Interface);
		setBounds(30186, 135, 98, 22, Interface);
		setBounds(30194, 168, 98, 23, Interface);
		setBounds(30202, 11, 125, 24, Interface);
		setBounds(30210, 42, 124, 25, Interface);
		setBounds(30218, 74, 125, 26, Interface);
		setBounds(30226, 103, 125, 27, Interface);
		setBounds(30234, 135, 125, 28, Interface);
		setBounds(30242, 164, 126, 29, Interface);
		setBounds(30250, 10, 155, 30, Interface);
		setBounds(30258, 42, 155, 31, Interface);
		setBounds(30266, 71, 155, 32, Interface);
		setBounds(30274, 103, 155, 33, Interface);
		setBounds(30282, 136, 155, 34, Interface);
		setBounds(30290, 165, 155, 35, Interface);
		setBounds(30298, 13, 185, 36, Interface);
		setBounds(30306, 42, 185, 37, Interface);
		setBounds(30314, 71, 184, 38, Interface);
		setBounds(30322, 104, 184, 39, Interface);
		setBounds(30001, 6, 184, 40, Interface);// hover
		setBounds(30018, 5, 176, 41, Interface);// hover
		setBounds(30026, 5, 176, 42, Interface);// hover
		setBounds(30033, 5, 163, 43, Interface);// hover
		setBounds(30041, 5, 176, 44, Interface);// hover
		setBounds(30049, 5, 176, 45, Interface);// hover
		setBounds(30057, 5, 176, 46, Interface);// hover
		setBounds(30065, 5, 176, 47, Interface);// hover
		setBounds(30076, 5, 163, 48, Interface);// hover
		setBounds(30084, 5, 176, 49, Interface);// hover
		setBounds(30092, 5, 176, 50, Interface);// hover
		setBounds(30100, 5, 176, 51, Interface);// hover
		setBounds(30107, 5, 176, 52, Interface);// hover
		setBounds(30115, 5, 163, 53, Interface);// hover
		setBounds(30123, 5, 176, 54, Interface);// hover
		setBounds(30131, 5, 163, 55, Interface);// hover
		setBounds(30139, 5, 163, 56, Interface);// hover
		setBounds(30147, 5, 163, 57, Interface);// hover
		setBounds(30155, 5, 176, 58, Interface);// hover
		setBounds(30163, 5, 176, 59, Interface);// hover
		setBounds(30171, 5, 176, 60, Interface);// hover
		setBounds(30179, 5, 163, 61, Interface);// hover
		setBounds(30187, 5, 176, 62, Interface);// hover
		setBounds(30195, 5, 149, 63, Interface);// hover
		setBounds(30203, 5, 176, 64, Interface);// hover
		setBounds(30211, 5, 163, 65, Interface);// hover
		setBounds(30219, 5, 163, 66, Interface);// hover
		setBounds(30227, 5, 176, 67, Interface);// hover
		setBounds(30235, 5, 149, 68, Interface);// hover
		setBounds(30243, 5, 176, 69, Interface);// hover
		setBounds(30251, 5, 5, 70, Interface);// hover
		setBounds(30259, 5, 5, 71, Interface);// hover
		setBounds(30267, 5, 5, 72, Interface);// hover
		setBounds(30275, 5, 5, 73, Interface);// hover
		setBounds(30283, 5, 5, 74, Interface);// hover
		setBounds(30291, 5, 5, 75, Interface);// hover
		setBounds(30299, 5, 5, 76, Interface);// hover
		setBounds(30307, 5, 5, 77, Interface);// hover
		setBounds(30323, 5, 5, 78, Interface);// hover
		setBounds(30315, 5, 5, 79, Interface);// hover
	}

	public static void setBounds(int ID, int X, int Y, int frame, Widget RSinterface) {
		RSinterface.children[frame] = ID;
		RSinterface.childX[frame] = X;
		RSinterface.childY[frame] = Y;
	}

	public static void addButton(int i, int spriteId, int W, int H, String S, int AT) {		
		Widget RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parent = i;
		RSInterface.type = 5;
		RSInterface.optionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = SpriteCache.lookup(spriteId);
		RSInterface.enabledSprite = SpriteCache.lookup(spriteId);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;
	}

	public static void addConfigHover(int interfaceID, int actionType, int hoverid, int disabledSpriteId, int enabledSpriteId,
			int Width, int Height, int configFrame, int configId, String Tooltip, int hoverId2,
			int hoverSpriteId, int hoverSpriteId2, String hoverSpriteName, int hoverId3, String hoverDisabledText,
			String hoverEnabledText, int X, int Y) {
		Widget hover = addTabInterface(interfaceID);
		hover.id = interfaceID;
		hover.parent = interfaceID;
		hover.type = 5;
		hover.optionType = actionType;
		hover.contentType = 0;
		hover.opacity = 0;
		hover.hoverType = hoverid;
		hover.disabledSprite = SpriteCache.lookup(disabledSpriteId);	
		hover.enabledSprite = SpriteCache.lookup(enabledSpriteId);		
		hover.width = Width;
		hover.tooltip = Tooltip;
		hover.height = Height;
		hover.scriptOperators = new int[1];
		hover.scriptDefaults = new int[1];
		hover.scriptOperators[0] = 1;
		hover.scriptDefaults[0] = configId;
		hover.scripts = new int[1][3];
		hover.scripts[0][0] = 5;
		hover.scripts[0][1] = configFrame;
		hover.scripts[0][2] = 0;
		hover = addTabInterface(hoverid);
		hover.parent = hoverid;
		hover.id = hoverid;
		hover.type = 0;
		hover.optionType = 0;
		hover.width = 550;
		hover.height = 334;
		hover.invisible = true;
		hover.hoverType = -1;
		addSprites(hoverId2, hoverSpriteId, hoverSpriteId2, hoverSpriteName, configId, configFrame);
		addHoverBox(hoverId3, interfaceID, hoverDisabledText, hoverEnabledText, configId, configFrame);
		setChildren(2, hover);
		setBounds(hoverId2, 15, 60, 0, hover);
		setBounds(hoverId3, X, Y, 1, hover);
	}

	public static void addSprites(int ID, int disabledSpriteId, int enabledSpriteId, String name, int configId, int configFrame) {
		Widget Tab = addTabInterface(ID);		
		Tab.id = ID;
		Tab.parent = ID;
		Tab.type = 5;
		Tab.optionType = 0;
		Tab.contentType = 0;
		Tab.width = 512;
		Tab.height = 334;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.scriptOperators = new int[1];
		Tab.scriptDefaults = new int[1];
		Tab.scriptOperators[0] = 1;
		Tab.scriptDefaults[0] = configId;
		Tab.scripts = new int[1][3];
		Tab.scripts[0][0] = 5;
		Tab.scripts[0][1] = configFrame;
		Tab.scripts[0][2] = 0;
		Tab.disabledSprite = SpriteCache.lookup(disabledSpriteId);	
		Tab.enabledSprite = SpriteCache.lookup(enabledSpriteId);
	}

	public String[] tooltips;
	public boolean newScroller;
	@SuppressWarnings("unused")
	private int mOverInterToTrigger;
	@SuppressWarnings("unused")
	private boolean interfaceShown;
}
