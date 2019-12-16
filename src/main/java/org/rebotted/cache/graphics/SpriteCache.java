package org.rebotted.cache.graphics;

import java.io.*;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.rebotted.io.Buffer;
import org.rebotted.sign.SignLink;
import org.rebotted.util.FileUtils;

public final class SpriteCache {

	private static Sprite[] sprites;

	private static int totalSprites;
	private static int i = 0;
	public static void load() {
		Buffer data = new Buffer(FileUtils.readFile(SignLink.findcachedir() + "main_file_sprites.dat"));

		try (DataInputStream dataFile = new DataInputStream(new XZCompressorInputStream(new ByteArrayInputStream(data.payload)))) {

			int totalSprites = dataFile.readInt();

			sprites = new Sprite[totalSprites];

			for (int index = 0; index < totalSprites; index++) {
				sprites[index] = SpriteCache.decode(dataFile);

				sprites[index].setTransparency(255, 0, 255);
			}
			System.out.println("Sprites Loaded: " + totalSprites);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Sprite decode(DataInputStream dat) throws IOException {

		Sprite sprite = new Sprite();

		while (true) {

			byte opcode = dat.readByte();

			if (opcode == 0) {
				return sprite;
			} else if (opcode == 1) {
				sprite.setId(dat.readShort());
			} else if (opcode == 2) {
				sprite.setName(dat.readUTF());
			} else if (opcode == 3) {
				sprite.setMyWidth(dat.readShort());
			} else if (opcode == 4) {
				sprite.setMyHeight(dat.readShort());
			} else if (opcode == 5) {
				sprite.setOffsetX(dat.readShort());
			} else if (opcode == 6) {
				sprite.setOffsetY(dat.readShort());
			} else if (opcode == 7) {

				int indexLength = dat.readInt();

				int[] pixels = new int[indexLength];

				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = dat.readInt();
				}

				sprite.setMyPixels(pixels);
			}
		}
	}

	public Sprite[] getSprites() {
		return sprites;
	}

	public static Sprite lookup(int id) {
		if (id < 0 || id > sprites.length) {
			throw new IllegalArgumentException(String.format("Invalid id: %d while trying to lookup a sprite.", id));
		}
		return sprites[id];
	}

	public static void set(int id, Sprite sprite) {
		if (id < 0 || id > sprites.length) {
			throw new IllegalArgumentException(String.format("Invalid id: %d while trying to lookup a sprite.", id));
		}
		sprites[id] = sprite;
	}

	public int totalSprites() {
		return totalSprites;
	}

}