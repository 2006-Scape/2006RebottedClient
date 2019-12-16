package org.rebotted.cache.anim;

import org.rebotted.cache.FileArchive;
import org.rebotted.io.Buffer;

public final class Animation {

	public static void init(FileArchive streamLoader) {
		Buffer buffer = new Buffer(streamLoader.readFile("seq.dat"));

		int length = buffer.readUShort();
		
		if (animations == null) {
			animations = new Animation[length];
		}
		
		for (int i = 0; i < length; i++) {
			if (animations[i] == null) {
				animations[i] = new Animation();
			}

			animations[i].decode(buffer);
		}

		System.out.println("Loaded: " + length + " Animations");
	}

	public int duration(int index) {		
		int duration = durations[index];
		if (duration == 0) {
			Frame frame = Frame.method531(primaryFrames[index]);
			if (frame != null) {
				duration = durations[index] = frame.duration;
			}
		}
		
		if (duration == 0) {
			duration = 1;
		}
		return duration;
	}

	private void decode(Buffer stream) {
		int opcode;
		while ((opcode = stream.readUnsignedByte()) != 0) {

			if (opcode == 1) {
				frameCount = stream.readUShort();
				primaryFrames = new int[frameCount];
				secondaryFrames = new int[frameCount];
				durations = new int[frameCount];
				for (int i = 0; i < frameCount; i++) {					
					primaryFrames[i] = stream.readInt();
					secondaryFrames[i] = -1;
				}

				for (int i = 0; i < frameCount; i++) {					
					durations[i] = stream.readUnsignedByte();
				}

			} else if (opcode == 2) {
				loopOffset = stream.readUShort();
			} else if (opcode == 3) {
				int length = stream.readUnsignedByte();				
				interleaveOrder = new int[length + 1];
				for (int i = 0; i < length; i++) {					
					interleaveOrder[i] = stream.readUnsignedByte();
				}
				interleaveOrder[length] = 9999999;
			} else if (opcode == 4) {
				stretches = true;
			} else if (opcode == 5) {
				forcedPriority = stream.readUnsignedByte();
			} else if (opcode == 6) {
				rightHand = stream.readUShort();
			} else if (opcode == 7) {
				leftHand = stream.readUShort();
			} else if (opcode == 8) {
				maximumLoops = stream.readUnsignedByte();
			} else if (opcode == 9) {
				animatingPrecedence = stream.readUnsignedByte();
			} else if (opcode == 10) {
				priority = stream.readUnsignedByte();
			} else if (opcode == 11) {
				replayMode = stream.readUnsignedByte();
			} else if (opcode == 12) {
				stream.readInt();
			} else {
				System.out.println("Error unrecognised seq config code: " + opcode);
			}

		}
		if (frameCount == 0) {
			frameCount = 1;
			primaryFrames = new int[1];
			primaryFrames[0] = -1;
			secondaryFrames = new int[1];
			secondaryFrames[0] = -1;
			durations = new int[1];
			durations[0] = -1;
		}
		if (animatingPrecedence == -1)
			if (interleaveOrder != null) {
				animatingPrecedence = 2;
			} else {
				animatingPrecedence = 0;
			}
		if (priority == -1) {
			if (interleaveOrder != null) {
				priority = 2;
				return;
			}
			priority = 0;
		}
	}

	private Animation() {
		
	}

	public static Animation animations[];
	public int frameCount;
	public int primaryFrames[];
	public int secondaryFrames[];
	public int[] durations;
	public int loopOffset = -1;
	public int interleaveOrder[];
	public boolean stretches;
	public int forcedPriority = 5;
	
	/**
	 * Removes shield
	 */
	public int rightHand = -1;	
	
	/**
	 * Removes weapon
	 */
	public int leftHand = -1;	
	public int maximumLoops = 99;
	
	/**
	 * Stops character from moving.
	 */
	public int animatingPrecedence = -1;
	public int priority = -1;
	public int replayMode = 1;
	public static int anInt367;

}