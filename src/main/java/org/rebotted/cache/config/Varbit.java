package org.rebotted.cache.config;

import org.rebotted.cache.FileArchive;
import org.rebotted.io.Buffer;

public final class Varbit {

	public static Varbit varbits[];
	public int setting = -1;
	public int low = -1;
	public int high = -1;
	private boolean aBoolean651;

	public static void init(FileArchive streamLoader) {
		Buffer datBuf = new Buffer(streamLoader.readFile("varbit.dat"));

		final int size = datBuf.readUShort();

		if (varbits == null) {
			varbits = new Varbit[size];
		}

		for (int index = 0; index < size; index++) {

			if (varbits[index] == null) {
				varbits[index] = new Varbit();
			}

			varbits[index].decode(datBuf);

			if (varbits[index].aBoolean651) {
				Varp.variables[varbits[index].setting].aBoolean713 = true;
			}

		}

		if (datBuf.currentPosition != datBuf.payload.length) {
			System.out.println("varbit load mismatch");
		}

	}

/*	private void decode(Buffer stream) {
		setting = stream.readUShort();
		low = stream.readUnsignedByte();
		high = stream.readUnsignedByte();

	}*/
	private void decode(Buffer stream) {
		do {
			int j = stream.readUnsignedByte();
			if (j == 0) {
				return;
			}
			if (j == 1) {
				setting = stream.readUShort();
				low = stream.readUnsignedByte();
				high = stream.readUnsignedByte();
			} else if (j == 10) {
				stream.readString();
			} else if (j == 2) {
				aBoolean651 = true;
			} else if (j == 3) {
				stream.readDWord();
			} else if (j == 4) {
				stream.readDWord();
			} else {
				System.out.println("Error unrecognised config code: " + j);
			}
		} while (true);
	}

	private Varbit() {
		aBoolean651 = false;
	}

	public int getSetting() {
		return setting;
	}

	public int getLow() {
		return low;
	}

	public int getHigh() {
		return high;
	}

}
