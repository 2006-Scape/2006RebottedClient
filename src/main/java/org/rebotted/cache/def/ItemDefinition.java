package org.rebotted.cache.def;

import org.rebotted.cache.FileArchive;
import org.rebotted.cache.graphics.Sprite;
import org.rebotted.collection.ReferenceCache;
import org.rebotted.draw.Rasterizer2D;
import org.rebotted.draw.Rasterizer3D;
import org.rebotted.entity.model.Model;
import org.rebotted.io.Buffer;

public final class ItemDefinition {

	public static void init(FileArchive archive) {
		dataBuf = new Buffer(archive.readFile("obj.dat"));
		Buffer idxBuf = new Buffer(archive.readFile("obj.idx"));		

		itemCount = idxBuf.readUShort();		
		streamIndices = new int[itemCount];
		
		int offset = 2;

		for (int i = 0; i < itemCount; i++) {
			streamIndices[i] = offset;
			offset += idxBuf.readUShort();
		}
		
		cache = new ItemDefinition[10];
		
		for (int i = 0; i < 10; i++) {			
			cache[i] = new ItemDefinition();
		}
		
		System.out.println("Loaded: " + itemCount + " Items");		

	}
	
	private void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUnsignedByte();
			
			if (opcode == 0) {
				return;
			} else if (opcode == 1) {
				inventoryModel = buffer.readUShort();	
			} else if (opcode == 2) {
				name = buffer.readString();
			} else if (opcode == 3) {
				description = buffer.readBytes();
			} else if (opcode == 4) {
				modelZoom = buffer.readUShort();	
			} else if (opcode == 5) {
				rotationY = buffer.readUShort();	
			} else if (opcode == 6) {
				rotationX = buffer.readUShort();
			} else if (opcode == 7) {
				translateX = buffer.readUShort();				
				if (translateX > 32767)
					translateX -= 0x10000;
			} else if (opcode == 8) {
				translateYZ = buffer.readUShort();				
				if (translateYZ > 32767)
					translateYZ -= 0x10000;
			} else if (opcode == 10) {
				buffer.readUShort();
			} else if (opcode == 11) {
				stackable = true;
			} else if (opcode == 12) {
				value = buffer.readInt();
			} else if (opcode == 16) {
				members = true;			
			} else if (opcode == 23) {
				equippedMaleModel1 = buffer.readUShort();				
				equippedMaleModelTranslationY = buffer.readSignedByte();
			} else if (opcode == 24) {			
				equippedMaleModel2 = buffer.readUShort();
			} else if (opcode == 25) {				
				equippedFemaleModel1 = buffer.readUShort();				
				equippedFemaleModelTranslationY = buffer.readSignedByte();
			} else if (opcode == 26) {			
				equippedFemaleModel2 = buffer.readUShort();
			} else if (opcode >= 30 && opcode < 35) {
				if (groundActions == null) {
					groundActions = new String[5];
				}				
				groundActions[opcode - 30] = buffer.readString();				
				if (groundActions[opcode - 30].equalsIgnoreCase("hidden")) {
					groundActions[opcode - 30] = null;
				}				
			} else if (opcode >= 35 && opcode < 40) {
				if (actions == null) {
					actions = new String[5];
				}
				actions[opcode - 35] = buffer.readString();
			} else if (opcode == 40) {
				int colors = buffer.readUnsignedByte();				
				modifiedModelColors = new int[colors];				
				originalModelColors = new int[colors];				
				for (int i = 0; i < colors; i++) {						
					modifiedModelColors[i] = buffer.readUShort();
					originalModelColors[i] = buffer.readUShort();
				}
			} else if (opcode == 78) {
				equippedMaleModel3 = buffer.readUShort();	
			} else if (opcode == 79) {
				equippedFemaleModel3 = buffer.readUShort();	
			} else if (opcode == 90) {
				equippedMaleModelDialogue1 = buffer.readUShort();
			} else if (opcode == 91) {
				equippedFemaleModelDialogue1 = buffer.readUShort();	
			} else if (opcode == 92) {
				equippedMaleModelDialogue2 = buffer.readUShort();	
			} else if (opcode == 93) {
				equippedFemaleModelDialogue2 = buffer.readUShort();	
			} else if (opcode == 95) {
				rotationZ = buffer.readUShort();
			} else if (opcode == 97) {
				unnotedItemId = buffer.readUShort();
			} else if (opcode == 98) {
				notedItemId = buffer.readUShort();	
			} else if (opcode >= 100 && opcode < 110) {
				if (stackVariantId == null) {					
					stackVariantId = new int[10];
					stackVariantSize = new int[10];					
				}
				stackVariantId[opcode - 100] = buffer.readUShort();
				stackVariantSize[opcode - 100] = buffer.readUShort();
			} else if (opcode == 110) {
				modelScaleX = buffer.readUShort();	
			} else if (opcode == 111) {
				modelScaleY = buffer.readUShort();
			} else if (opcode == 112) {
				modelScaleZ = buffer.readUShort();
			} else if (opcode == 113) {
				lightIntensity = buffer.readSignedByte();	
			} else if (opcode == 114) {
				lightMag = buffer.readSignedByte() * 5;	
			} else if (opcode == 115) {
				team = buffer.readUnsignedByte();
			}
		} while (true);
	}

	public Model getChatEquipModel(int gender) {
		int dialogueModel = equippedMaleModelDialogue1;
		int dialogueHatModel = equippedMaleModelDialogue2;
		if (gender == 1) {
			dialogueModel = equippedFemaleModelDialogue1;
			dialogueHatModel = equippedFemaleModelDialogue2;
		}
		if (dialogueModel == -1)
			return null;
		Model dialogueModel_ = Model.getModel(dialogueModel);
		if (dialogueHatModel != -1) {
			Model hatModel_ = Model.getModel(dialogueHatModel);
			Model models[] = { dialogueModel_, hatModel_ };
			dialogueModel_ = new Model(2, models);
		}
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				dialogueModel_.recolor(modifiedModelColors[i1], originalModelColors[i1]);

		}
		return dialogueModel_;
	}

	public boolean isEquippedModelCached(int gender) {
		int primaryModel = equippedMaleModel1;
		int secondaryModel = equippedMaleModel2;
		int emblem = equippedMaleModel3;
		if (gender == 1) {
			primaryModel = equippedFemaleModel1;
			secondaryModel = equippedFemaleModel2;
			emblem = equippedFemaleModel3;
		}
		if (primaryModel == -1)
			return true;
		boolean cached = true;
		if (!Model.isCached(primaryModel))
			cached = false;
		if (secondaryModel != -1 && !Model.isCached(secondaryModel))
			cached = false;
		if (emblem != -1 && !Model.isCached(emblem))
			cached = false;
		return cached;
	}

	public Model getEquippedModel(int gender) {
		int primaryModel = equippedMaleModel1;
		int secondaryModel = equippedMaleModel2;
		int emblem = equippedMaleModel3;
		if (gender == 1) {
			primaryModel = equippedFemaleModel1;
			secondaryModel = equippedFemaleModel2;
			emblem = equippedFemaleModel3;
		}
		if (primaryModel == -1)
			return null;
		Model primaryModel_ = Model.getModel(primaryModel);
		if (secondaryModel != -1)
			if (emblem != -1) {
				Model secondaryModel_ = Model.getModel(secondaryModel);
				Model emblemModel = Model.getModel(emblem);
				Model models[] = { primaryModel_, secondaryModel_, emblemModel };
				primaryModel_ = new Model(3, models);
			} else {
				Model model_2 = Model.getModel(secondaryModel);
				Model models[] = { primaryModel_, model_2 };
				primaryModel_ = new Model(2, models);
			}
		if (gender == 0 && equippedMaleModelTranslationY != 0)
			primaryModel_.translate(0, equippedMaleModelTranslationY, 0);
		if (gender == 1 && equippedFemaleModelTranslationY != 0)
			primaryModel_.translate(0, equippedFemaleModelTranslationY, 0);
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				primaryModel_.recolor(modifiedModelColors[i1], originalModelColors[i1]);

		}
		return primaryModel_;
	}

	private void setDefaults() {
		inventoryModel = 0;
		name = "Dwarf Remains";
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		modelZoom = 2000;
		rotationY = 0;
		rotationX = 0;
		rotationZ = 0;
		translateX = 0;
		translateYZ = 0;
		stackable = false;
		value = 1;
		members = false;
		groundActions = null;
		actions = null;
		equippedMaleModel1 = -1;
		equippedMaleModel2 = -1;
		equippedMaleModelTranslationY = 0;
		equippedFemaleModel1 = -1;
		equippedFemaleModel2 = -1;
		equippedFemaleModelTranslationY = 0;
		equippedMaleModel3 = -1;
		equippedFemaleModel3 = -1;
		equippedMaleModelDialogue1 = -1;
		equippedMaleModelDialogue2 = -1;
		equippedFemaleModelDialogue1 = -1;
		equippedFemaleModelDialogue2 = -1;
		stackVariantId = null;
		stackVariantSize = null;
		unnotedItemId = -1;
		notedItemId = -1;
		modelScaleX = 128;
		modelScaleY = 128;
		modelScaleZ = 128;
		lightIntensity = 0;
		lightMag = 0;
		team = 0;
	}

	public static ItemDefinition lookup(int itemId) {
		for (int count = 0; count < 10; count++) {
			if (cache[count].id == itemId) {
				return cache[count];
			}
		}

		cacheIndex = (cacheIndex + 1) % 10;
		ItemDefinition itemDef = cache[cacheIndex];
		dataBuf.currentPosition = streamIndices[itemId];
		itemDef.id = itemId;
		itemDef.setDefaults();
		itemDef.decode(dataBuf);

		if (itemDef.notedItemId != -1) {
			itemDef.toNote();
		}
		
		return itemDef;
	}

	private void toNote() {
		ItemDefinition itemDef = lookup(notedItemId);
		inventoryModel = itemDef.inventoryModel;
		modelZoom = itemDef.modelZoom;
		rotationY = itemDef.rotationY;
		rotationX = itemDef.rotationX;

		rotationZ = itemDef.rotationZ;
		translateX = itemDef.translateX;
		translateYZ = itemDef.translateYZ;
		modifiedModelColors = itemDef.modifiedModelColors;
		originalModelColors = itemDef.originalModelColors;
		ItemDefinition itemDef_1 = lookup(unnotedItemId);
		name = itemDef_1.name;
		members = itemDef_1.members;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
			s = "an";
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".").getBytes();
		stackable = true;
	}

	public static Sprite getSprite(int itemId, int stackSize, int outlineColor) {
		if (outlineColor == 0) {
			Sprite sprite = (Sprite) sprites.get(itemId);
			if (sprite != null && sprite.getMaxHeight() != stackSize && sprite.getMaxHeight() != -1) {

				sprite.unlink();
				sprite = null;
			}
			if (sprite != null)
				return sprite;
		}
		ItemDefinition itemDef = lookup(itemId);
		if (itemDef.stackVariantId == null)
			stackSize = -1;
		if (stackSize > 1) {
			int stack_item_id = -1;
			for (int j1 = 0; j1 < 10; j1++)
				if (stackSize >= itemDef.stackVariantSize[j1] && itemDef.stackVariantSize[j1] != 0)
					stack_item_id = itemDef.stackVariantId[j1];

			if (stack_item_id != -1)
				itemDef = lookup(stack_item_id);
		}
		Model model = itemDef.getModel(1);
		if (model == null)
			return null;
		Sprite sprite = null;
		if (itemDef.notedItemId != -1) {
			sprite = getSprite(itemDef.unnotedItemId, 10, -1);
			if (sprite == null)
				return null;
		}
		Sprite enabledSprite = new Sprite(32, 32);
		int centerX = Rasterizer3D.originViewX;
		int centerY = Rasterizer3D.originViewY;
		int lineOffsets[] = Rasterizer3D.scanOffsets;
		int pixels[] = Rasterizer2D.pixels;
		float depthBuffer[] = Rasterizer2D.depthBuffer;
		int width = Rasterizer2D.width;
		int height = Rasterizer2D.height;
		int vp_left = Rasterizer2D.leftX;
		int vp_right = Rasterizer2D.bottomX;
		int vp_top = Rasterizer2D.topY;
		int vp_bottom = Rasterizer2D.bottomY;
		Rasterizer3D.aBoolean1464 = false;
		Rasterizer2D.initDrawingArea(32, 32, enabledSprite.getMyPixels(), new float[32 * 32]);
		Rasterizer2D.drawBox(0, 0, 32, 32, 0);
		Rasterizer3D.useViewport();
		int k3 = itemDef.modelZoom;
		if (outlineColor == -1)
			k3 = (int) ((double) k3 * 1.5D);
		if (outlineColor > 0)
			k3 = (int) ((double) k3 * 1.04D);
		int l3 = Rasterizer3D.anIntArray1470[itemDef.rotationY] * k3 >> 16;
		int i4 = Rasterizer3D.COSINE[itemDef.rotationY] * k3 >> 16;
		model.method482(itemDef.rotationX, itemDef.rotationZ, itemDef.rotationY, itemDef.translateX,
				l3 + model.modelBaseY / 2 + itemDef.translateYZ, i4 + itemDef.translateYZ);
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--)
				if (enabledSprite.getMyPixels()[i5 + j4 * 32] == 0)
					if (i5 > 0 && enabledSprite.getMyPixels()[(i5 - 1) + j4 * 32] > 1)
						enabledSprite.getMyPixels()[i5 + j4 * 32] = 1;
					else if (j4 > 0 && enabledSprite.getMyPixels()[i5 + (j4 - 1) * 32] > 1)
						enabledSprite.getMyPixels()[i5 + j4 * 32] = 1;
					else if (i5 < 31 && enabledSprite.getMyPixels()[i5 + 1 + j4 * 32] > 1)
						enabledSprite.getMyPixels()[i5 + j4 * 32] = 1;
					else if (j4 < 31 && enabledSprite.getMyPixels()[i5 + (j4 + 1) * 32] > 1)
						enabledSprite.getMyPixels()[i5 + j4 * 32] = 1;

		}

		if (outlineColor > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--)
					if (enabledSprite.getMyPixels()[j5 + k4 * 32] == 0)
						if (j5 > 0 && enabledSprite.getMyPixels()[(j5 - 1) + k4 * 32] == 1)
							enabledSprite.getMyPixels()[j5 + k4 * 32] = outlineColor;
						else if (k4 > 0 && enabledSprite.getMyPixels()[j5 + (k4 - 1) * 32] == 1)
							enabledSprite.getMyPixels()[j5 + k4 * 32] = outlineColor;
						else if (j5 < 31 && enabledSprite.getMyPixels()[j5 + 1 + k4 * 32] == 1)
							enabledSprite.getMyPixels()[j5 + k4 * 32] = outlineColor;
						else if (k4 < 31 && enabledSprite.getMyPixels()[j5 + (k4 + 1) * 32] == 1)
							enabledSprite.getMyPixels()[j5 + k4 * 32] = outlineColor;

			}

		} else if (outlineColor == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--)
					if (enabledSprite.getMyPixels()[k5 + l4 * 32] == 0 && k5 > 0 && l4 > 0
							&& enabledSprite.getMyPixels()[(k5 - 1) + (l4 - 1) * 32] > 0)
						enabledSprite.getMyPixels()[k5 + l4 * 32] = 0x302020;

			}

		}
		if (itemDef.notedItemId != -1) {
			int old_w = sprite.getMaxWidth();
			int old_h = sprite.getMaxHeight();
			sprite.setMaxWidth(32);
			sprite.setMaxHeight(32);
			sprite.drawSprite(0, 0);
			sprite.setMaxWidth(old_w);
			sprite.setMaxHeight(old_h);
		}
		if (outlineColor == 0)
			sprites.put(enabledSprite, itemId);
		Rasterizer2D.initDrawingArea(height, width, pixels, depthBuffer);
		Rasterizer2D.setDrawingArea(vp_bottom, vp_left, vp_right, vp_top);
		Rasterizer3D.originViewX = centerX;
		Rasterizer3D.originViewY = centerY;
		Rasterizer3D.scanOffsets = lineOffsets;
		Rasterizer3D.aBoolean1464 = true;
		if (itemDef.stackable)
			enabledSprite.setMaxWidth(33);
		else
			enabledSprite.setMaxWidth(32);
		enabledSprite.setMaxHeight(stackSize);
		return enabledSprite;
	}

	public Model getModel(int stack_size) {
		if (stackVariantId != null && stack_size > 1) {
			int stack_item_id = -1;
			for (int k = 0; k < 10; k++)
				if (stack_size >= stackVariantSize[k] && stackVariantSize[k] != 0)
					stack_item_id = stackVariantId[k];

			if (stack_item_id != -1)
				return lookup(stack_item_id).getModel(1);
		}
		Model model = (Model) models.get(id);
		if (model != null)
			return model;
		model = Model.getModel(inventoryModel);
		if (model == null)
			return null;
		if (modelScaleX != 128 || modelScaleY != 128 || modelScaleZ != 128)
			model.scale(modelScaleX, modelScaleZ, modelScaleY);
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.recolor(modifiedModelColors[l], originalModelColors[l]);

		}
		model.light(64 + lightIntensity, 768 + lightMag, -50, -10, -50, true);
		model.fits_on_single_square = true;
		models.put(model, id);
		return model;
	}

	public Model getUnshadedModel(int stack_size) {
		if (stackVariantId != null && stack_size > 1) {
			int stack_item_id = -1;
			for (int count = 0; count < 10; count++)
				if (stack_size >= stackVariantSize[count] && stackVariantSize[count] != 0)
					stack_item_id = stackVariantId[count];

			if (stack_item_id != -1)
				return lookup(stack_item_id).getUnshadedModel(1);
		}
		Model model = Model.getModel(inventoryModel);
		if (model == null)
			return null;
		if (modifiedModelColors != null) {
			for (int colorPtr = 0; colorPtr < modifiedModelColors.length; colorPtr++)
				model.recolor(modifiedModelColors[colorPtr], originalModelColors[colorPtr]);

		}
		return model;
	}
	
	public static void clear() {
		models = null;
		sprites = null;
		streamIndices = null;
		cache = null;
		dataBuf = null;
	}

	public boolean isDialogueModelCached(int gender) {
		int model_1 = equippedMaleModelDialogue1;
		int model_2 = equippedMaleModelDialogue2;
		if (gender == 1) {
			model_1 = equippedFemaleModelDialogue1;
			model_2 = equippedFemaleModelDialogue2;
		}
		if (model_1 == -1)
			return true;
		boolean cached = true;
		if (!Model.isCached(model_1))
			cached = false;
		if (model_2 != -1 && !Model.isCached(model_2))
			cached = false;
		return cached;
	}

	private ItemDefinition() {
		id = -1;
	}

	private byte equippedFemaleModelTranslationY;
	public int value;
	public int[] modifiedModelColors;
	public int id;
	public static ReferenceCache sprites = new ReferenceCache(100);
	public static ReferenceCache models = new ReferenceCache(50);
	public int[] originalModelColors;
	public boolean members;
	private int equippedFemaleModel3;
	private int notedItemId;
	public int equippedFemaleModel2;
	public int equippedMaleModel1;
	private int equippedMaleModelDialogue2;
	private int modelScaleX;
	public String groundActions[];
	public int translateX;
	public String name;
	private static ItemDefinition[] cache;
	private int equippedFemaleModelDialogue2;
	public int inventoryModel;
	public int equippedMaleModelDialogue1;
	public boolean stackable;
	public byte[] description;
	public int unnotedItemId;
	private static int cacheIndex;
	public int modelZoom;
	public static boolean isMembers = true;
	private static Buffer dataBuf;
	private int lightMag;
	private int equippedMaleModel3;
	public int equippedMaleModel2;
	public String actions[];
	public int rotationY;
	private int modelScaleZ;
	private int modelScaleY;
	public int[] stackVariantId;
	public int translateYZ;//
	private static int[] streamIndices;
	private int lightIntensity;
	public int equippedFemaleModelDialogue1;
	public int rotationX;
	public int equippedFemaleModel1;
	public int[] stackVariantSize;
	public int team;
	public static int itemCount;
	public int rotationZ;	
	private byte equippedMaleModelTranslationY;
}
