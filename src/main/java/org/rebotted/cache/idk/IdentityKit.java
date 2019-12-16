package org.rebotted.cache.idk;

import org.rebotted.cache.FileArchive;
import org.rebotted.entity.model.Model;
import org.rebotted.io.Buffer;

public final class IdentityKit {
	
	public static int length;
	public static IdentityKit kits[];
	public int bodyPartId;
	private int[] bodyModels;
	private final int[] originalColors;
	private final int[] replacementColors;
	private final int[] headModels = { -1, -1, -1, -1, -1 };
	public boolean validStyle;
	
	private IdentityKit() {
		bodyPartId = -1;
		originalColors = new int[6];
		replacementColors = new int[6];
		validStyle = false;
	}

	public static void init(FileArchive archive) {
		Buffer buffer = new Buffer(archive.readFile("idk.dat"));
		
		length = buffer.readUShort();		
		if (kits == null) {
			kits = new IdentityKit[length];
		}		
		for (int id = 0; id < length; id++) {
			if (kits[id] == null) {
				kits[id] = new IdentityKit();
			}			
			kits[id].decode(buffer);
		}
	}

	private void decode(Buffer buffer) {
		while(true) {
			
			int opcode = buffer.readUnsignedByte();
			
			if (opcode == 0) {
				return;
			} else if (opcode == 1) {
				bodyPartId = buffer.readUnsignedByte();				
			} else if (opcode == 2) {
				int count = buffer.readUnsignedByte();
				bodyModels = new int[count];				
				for (int part = 0; part < count; part++) {
					bodyModels[part] = buffer.readUShort();
				}
			} else if (opcode == 3) {
				validStyle = true;
			} else if (opcode >= 40 && opcode < 50) {
				originalColors[opcode - 40] = buffer.readUShort();
			} else if (opcode >= 50 && opcode < 60) {
				replacementColors[opcode - 50] = buffer.readUShort();
			} else if (opcode >= 60 && opcode < 70) {
				headModels[opcode - 60] = buffer.readUShort();
			} else {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		}
	}

	public boolean bodyLoaded() {
		if (bodyModels == null)
			return true;
		boolean ready = true;
		for (int part = 0; part < bodyModels.length; part++)
			if (!Model.isCached(bodyModels[part]))
				ready = false;

		return ready;
	}

	public Model bodyModel() {
		if (bodyModels == null) {
			return null;
		}
		
		Model models[] = new Model[bodyModels.length];
		for (int part = 0; part < bodyModels.length; part++) {
			models[part] = Model.getModel(bodyModels[part]);
		}

		Model model;
		if (models.length == 1) {
			model = models[0];
		} else {
			model = new Model(models.length, models);
		}
		for (int part = 0; part < 6; part++) {
			if (originalColors[part] == 0) {
				break;
			}
			model.recolor(originalColors[part], replacementColors[part]);
		}
		return model;
	}

	public boolean headLoaded() {
		boolean ready = true;
		for (int part = 0; part < 5; part++) {
			if (headModels[part] != -1 && !Model.isCached(headModels[part])) {
				ready = false;
			}
		}
		return ready;
	}

	public Model headModel() {
		Model models[] = new Model[5];
		int count = 0;
		for (int part = 0; part < 5; part++) {
			if (headModels[part] != -1) {
				models[count++] = Model.getModel(headModels[part]);
			}
		}

		Model model = new Model(count, models);
		for (int part = 0; part < 6; part++) {
			if (originalColors[part] == 0) {
				break;
			}
			model.recolor(originalColors[part], replacementColors[part]);
		}
		return model;
	}
}
