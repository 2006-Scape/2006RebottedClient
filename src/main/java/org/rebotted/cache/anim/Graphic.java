package org.rebotted.cache.anim;
import org.rebotted.cache.FileArchive;
import org.rebotted.collection.ReferenceCache;
import org.rebotted.entity.model.Model;
import org.rebotted.io.Buffer;

public final class Graphic {

	public static void init(FileArchive archive) {		
		Buffer buffer = new Buffer(archive.readFile("spotanim.dat"));
		final int length = buffer.readUShort();
		if (cache == null) {
			cache = new Graphic[length];
		}
		for (int i = 0; i < length; i++) {			
			if (cache[i] == null) {
				cache[i] = new Graphic();
			}
			cache[i].id = i;
			cache[i].decode(buffer);
		}
		
		System.out.println("Loaded: "+length+" Graphics");
	}

	public void decode(Buffer buffer) {
		while(true) {
			int opcode = buffer.readUnsignedByte();
			
			if (opcode == 0) {
				return;
			} else if (opcode == 1) {
				modelId = buffer.readUShort();
			} else if (opcode == 2) {
				animationId = buffer.readUShort();
				if (Animation.animations != null) {
					animationSequence = Animation.animations[animationId];
				}
			} else if (opcode == 4) {
				resizeXY = buffer.readUShort();
			} else if (opcode == 5) {
				resizeZ = buffer.readUShort();
			} else if (opcode == 6) {
				rotation = buffer.readUShort();
			} else if (opcode == 7) {
				modelBrightness = buffer.readUShort();
			} else if (opcode == 8) {
				modelShadow = buffer.readUShort();				
			} else if (opcode == 40) {
				int length = buffer.readUnsignedByte();				
				for (int i = 0; i < length; i++) {					
					originalModelColours[i] = buffer.readUShort();
					modifiedModelColours[i] = buffer.readUShort();
				}
			} else {
				System.out.println("Error unrecognised spotanim config code: "
						+ opcode);
			}
		}
	}

	public Model getModel() {
		Model model = (Model) models.get(id);
		if (model != null)
			return model;
		model = Model.getModel(modelId);
		if (model == null)
			return null;
		for (int i = 0; i < 6; i++)
			if (originalModelColours[0] != 0)
				model.recolor(originalModelColours[i], modifiedModelColours[i]);

		models.put(model, id);
		return model;
	}

	private Graphic() {
		animationId = -1;
		originalModelColours = new int[6];
		modifiedModelColours = new int[6];
		resizeXY = 128;
		resizeZ = 128;
	}

	public static Graphic cache[];
	private int id;	
	private int modelId;
	private int animationId;
	public Animation animationSequence;
	private final int[] originalModelColours;
	private final int[] modifiedModelColours;
	public int resizeXY;
	public int resizeZ;
	public int rotation;
	public int modelBrightness;
	public int modelShadow;
	public static ReferenceCache models = new ReferenceCache(30);
}
