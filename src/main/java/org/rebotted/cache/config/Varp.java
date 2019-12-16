package org.rebotted.cache.config;

import org.rebotted.cache.FileArchive;
import org.rebotted.io.Buffer;

/**
 * Varps are used for inteface configuration ids and their functions, out of the current 725 config ids, only 9 or so of them are used.
 *
 */
public final class Varp {

	public static Varp[] variables;	
	
	private static int currentIndex;
	private static int[] configIds;	
	public int actionId;	
	public boolean aBoolean713;

	private Varp() {
		aBoolean713 = false;
	}

	public static void init(FileArchive archive) {
		Buffer buffer = new Buffer(archive.readFile("varp.dat"));
		
		currentIndex = 0;
		
		int length = buffer.readUShort();		
		
		if (variables == null) {
			variables = new Varp[length];
		}
		
		if (configIds == null) {
			configIds = new int[length];
		}
		
		for (int index = 0; index < length; index++) {
			if (variables[index] == null) {
				variables[index] = new Varp();
			}
			
			variables[index].decode(buffer, index);
			
		}
		
		if (buffer.currentPosition != buffer.payload.length) {
			System.out.println("varptype load mismatch");
		}
		
	}

	private void decode(Buffer buffer, int index) {			
		do {
			int opcode = buffer.readUnsignedByte();
			
			if (opcode == 0) {
				return;
			}
			
			if (opcode == 1) {
				buffer.readUnsignedByte();
			} else if (opcode == 2) {
				buffer.readUnsignedByte();
			} else if (opcode == 3) {
				configIds[currentIndex++] = index;					
			} else if (opcode == 4) {
			} else if (opcode == 5) {
				actionId = buffer.readUShort();
			} else if (opcode == 6) {
			} else if (opcode == 7) {
				buffer.readInt();
			} else if (opcode == 8) {
				aBoolean713 = true;
			} else if (opcode == 10) {
				buffer.readString();
			} else if (opcode == 11) {
				aBoolean713 = true;
			} else if (opcode == 12) {
				buffer.readInt();
			} else if (opcode == 13) {
			} else {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

	public static Varp[] getVariables() {
		return variables;
	}

	public static int getCurrentIndex() {
		return currentIndex;
	}

	public static int[] getConfigIds() {
		return configIds;
	}

	public int getActionId() {
		return actionId;
	}

	public boolean isaBoolean713() {
		return aBoolean713;
	}	
	
}
