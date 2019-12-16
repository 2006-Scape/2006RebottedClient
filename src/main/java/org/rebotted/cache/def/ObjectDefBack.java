package org.rebotted.cache.def;

import org.rebotted.Client;
import org.rebotted.cache.FileArchive;
import org.rebotted.cache.anim.Frame;
import org.rebotted.cache.config.Varbit;
import org.rebotted.collection.ReferenceCache;
import org.rebotted.entity.model.Model;
import org.rebotted.io.Buffer;
import org.rebotted.net.requester.ResourceProvider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class ObjectDefBack {

    public static final Model[] aModelArray741s = new Model[4];
    public static boolean lowMemory;
    public static Buffer dataBuf;
    public static int[] streamIndices;
    public static Client clientInstance;
    public static int cacheIndex;
    public static ReferenceCache models = new ReferenceCache(30);
    public static ObjectDefinition[] cache;
    public static ReferenceCache baseModels = new ReferenceCache(500);
    private static int totalObjects;
    public boolean obstructsGround;
    public byte ambientLighting;
    public int translateX;
    public String name;
    public int scaleZ;
    public byte lightDiffusion;
    public int objectSizeX;
    public int translateY;
    public int minimapFunction;
    public int[] originalModelColors;
    public int scaleX;
    public int varp;
    public boolean inverted;
    public int type;
    public boolean impenetrable;
    public int mapscene;
    public int[] childrenIDs;
    public int supportItems;
    public int objectSizeY;
    public boolean contouredGround;
    public boolean occludes;
    public boolean hollow;
    public boolean solid;
    public int surroundings;
    public boolean delayShading;
    public int scaleY;
    public int[] modelIds;
    public int varbit;
    public int decorDisplacement;
    public int[] modelTypes;
    public byte[] description;
    public boolean isInteractive;
    public boolean castsShadow;
    public int animation;
    public int translateZ;
    public int[] modifiedModelColors;
    public String[] interactions;
    private short[] originalTexture;
    private short[] modifiedTexture;


    public static void init(FileArchive streamLoader) throws IOException {
        dataBuf = new Buffer(streamLoader.readFile("loc.dat"));
        Buffer idxBuf = new Buffer(streamLoader.readFile("loc.idx"));
        totalObjects = idxBuf.readUShort();
        streamIndices = new int[totalObjects];
        int offset = 2;
        for (int index = 0; index < totalObjects; index++) {
            streamIndices[index] = offset;
            offset += idxBuf.readUShort();
        }
        cache = new ObjectDefinition[20];
        for (int index = 0; index < 20; index++) {
            cache[index] = new ObjectDefinition();
        }

//		for(int i = 0; i < totalObjects; i++) {
//			ObjectDefinition def = ObjectDefinition.lookup(i);
//
//			if (def == null || def.name == null) {
//				continue;
//			}
//
//			System.out.println(i + " " + def.name);
//
//		}

        System.out.println("Loaded: " + totalObjects + " Objects");
    }

    public static void dumpNames() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./Cache/object_names.txt"));
        for (int i = 0; i < totalObjects; i++) {
            ObjectDefinition def = lookup(i);
            String name = def == null ? "null" : def.name;
            writer.write("ID: " + i + ", name: " + name + "");
            writer.newLine();
        }
        writer.close();
    }

    public static ObjectDefinition lookup(int id) {
        if (id > streamIndices.length) {
            id = streamIndices.length - 1;
        }
        for (int index = 0; index < 20; index++) {
            if (cache[index].type == id) {
                return cache[index];
            }
        }

        cacheIndex = (cacheIndex + 1) % 20;
        ObjectDefinition def = cache[cacheIndex];
        dataBuf.currentPosition = streamIndices[id];
        def.type = id;
        def.reset();
        //def.decode(dataBuf);

        //Disable delayed shading.
        //Cheap fix for: edgeville ditch, raids, wintertodt fire etc
        //Fixes black square on the model
        def.delayShading = false;
        return def;
    }

    public static void clear() {
        baseModels = null;
        models = null;
        streamIndices = null;
        cache = null;
        dataBuf = null;
    }

    private void decode(Buffer stream) {
        while (true) {
            int flag = -1;

            int type = stream.readUnsignedByte();

            if (type == 0) {
                break;
            } else if (type == 1) {
                int length = stream.readUnsignedByte();
                if (length > 0) {
                    if (modelIds == null || lowMemory) {
                        modelTypes = new int[length];
                        modelIds = new int[length];
                        for (int i = 0; i < length; i++) {
                            modelIds[i] = stream.readUShort();
                            modelTypes[i] = stream.readUnsignedByte();
                        }
                    } else {
                        stream.currentPosition += length * 3;
                    }
                }
            } else if (type == 2) {
                name = stream.readString();
            } else if (type == 3) {
                description = stream.readBytes();
            } else if (type == 5) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null || lowMemory) {
                        modelTypes = null;
                        modelIds = new int[len];
                        for (int l1 = 0; l1 < len; l1++)
                            modelIds[l1] = stream.readUShort();
                    } else {
                        stream.currentPosition += len * 2;
                    }
                }
            } else if (type == 14) {
                objectSizeX = stream.readUnsignedByte();
            } else if (type == 15) {
                objectSizeY = stream.readUnsignedByte();
            } else if (type == 17) {
                solid = false;
            } else if (type == 18) {
                impenetrable = false;
            } else if (type == 19) {
                isInteractive = (stream.readUnsignedByte() == 1);
            } else if (type == 21) {
                contouredGround = true;
            } else if (type == 22) {
                delayShading = false;
            } else if (type == 23) {
                occludes = true;
            } else if (type == 24) {
                animation = stream.readUShort();
                if (animation == 65535)
                    animation = -1;
            } else if (type == 28) {
                decorDisplacement = stream.readUnsignedByte();
            } else if (type == 29) {
                ambientLighting = stream.readSignedByte();
            } else if (type == 39) {
                lightDiffusion = stream.readSignedByte();
            } else if (type >= 30 && type < 39) {
                if (interactions == null) {
                    interactions = new String[5];
                }
                if(type - 30 < 6) {
                    interactions[type - 30] = stream.readString();
                    if (interactions[type - 30].equalsIgnoreCase("hidden")) {
                        interactions[type - 30] = null;
                    }
                }

            } else if (type == 40) {
                int i1 = stream.readUnsignedByte();
                modifiedModelColors = new int[i1];
                originalModelColors = new int[i1];
                for (int i2 = 0; i2 < i1; i2++) {
                    modifiedModelColors[i2] = stream.readUShort();
                    originalModelColors[i2] = stream.readUShort();
                }
            } else if (type == 60) {
                minimapFunction = stream.readUShort();
            } else if (type == 62) {
                inverted = true;
            } else if (type == 64) {
                castsShadow = false;
            } else if (type == 65) {
                scaleX = stream.readUShort();
            } else if (type == 66) {
                scaleY = stream.readUShort();
            } else if (type == 67) {
                scaleZ = stream.readUShort();
            } else if (type == 68) {
                mapscene = stream.readUShort();
            } else if (type == 69) {
                surroundings = stream.readUnsignedByte();
            } else if (type == 70) {
                translateX = stream.readSignedWord();
            } else if (type == 71) {
                translateY = stream.readSignedWord();
            } else if (type == 72) {
                translateZ = stream.readSignedWord();
            } else if (type == 73) {
                obstructsGround = true;
            } else if (type == 74) {
                hollow = true;
            } else if(type == 75) {
                supportItems = dataBuf.readUnsignedByte();
            } else if (type == 77) {
                varp = stream.readUShort();
                if (varp == 65535) {
                    varp = -1;
                }
                varbit = stream.readUShort();
                if (varbit == 65535) {
                    varbit = -1;
                }
                int length = stream.readUnsignedByte();
                childrenIDs = new int[length];
                for (int i = 0; i < length; i++) {
                    childrenIDs[i] = stream.readUShort();
                    if (childrenIDs[i] == 65535) {
                        childrenIDs[i] = -1;
                    }
                }
            }

            if (flag == -1 && name != "null" && name != null) {
                isInteractive = modelIds != null
                        && (modelTypes == null || modelTypes[0] == 10);
                if (interactions != null)
                    isInteractive = true;
            }
            if (hollow) {
                solid = false;
                impenetrable = false;
            }
            if (supportItems == -1)
                supportItems = solid ? 1 : 0;
        }

    }
    public void reset() {
        modelIds = null;
        modelTypes = null;
        name = null;
        description = null;
        modifiedModelColors = null;
        originalModelColors = null;
        objectSizeX = 1;
        objectSizeY = 1;
        solid = true;
        impenetrable = true;
        isInteractive = false;
        contouredGround = false;
        delayShading = false;
        occludes = false;
        animation = -1;
        decorDisplacement = 16;
        ambientLighting = 0;
        lightDiffusion = 0;
        interactions = null;
        minimapFunction = -1;
        mapscene = -1;
        inverted = false;
        castsShadow = true;
        scaleX = 128;
        scaleY = 128;
        scaleZ = 128;
        surroundings = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        obstructsGround = false;
        hollow = false;
        supportItems = -1;
        varbit = -1;
        varp = -1;
        childrenIDs = null;
    }

    public void loadModels(ResourceProvider archive) {
        if (modelIds == null)
            return;
        for (int index = 0; index < modelIds.length; index++)
            archive.loadExtra(modelIds[index] & 0xffff, 0);
    }

    public boolean method577(int i) {
        if (modelTypes == null) {
            if (modelIds == null)
                return true;
            if (i != 10)
                return true;
            boolean flag1 = true;
            for (int k = 0; k < modelIds.length; k++)
                flag1 &= Model.isCached(modelIds[k] & 0xffff);

            return flag1;
        }
        for (int j = 0; j < modelTypes.length; j++)
            if (modelTypes[j] == i)
                return Model.isCached(modelIds[j] & 0xffff);

        return true;
    }

    public Model modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
        Model model = model(type, frameId, orientation);
        if (model == null)
            return null;
        if (contouredGround || delayShading)
            model = new Model(contouredGround, delayShading, model);
        if (contouredGround) {
            int y = (aY + bY + cY + dY) / 4;
            for (int vertex = 0; vertex < model.numVertices; vertex++) {
                int x = model.vertexX[vertex];
                int z = model.vertexZ[vertex];
                int l2 = aY + ((bY - aY) * (x + 64)) / 128;
                int i3 = dY + ((cY - dY) * (x + 64)) / 128;
                int j3 = l2 + ((i3 - l2) * (z + 64)) / 128;
                model.vertexY[vertex] += j3 - y;
            }

            model.computeSphericalBounds();
        }

        return model;
    }

    public boolean method579() {
        if (modelIds == null)
            return true;
        boolean flag1 = true;
        for (int i = 0; i < modelIds.length; i++)
            flag1 &= Model.isCached(modelIds[i] & 0xffff);
        return flag1;
    }

    public ObjectDefinition method580() {
        int i = -1;
        if (varbit != -1) {
            Varbit varBit = Varbit.varbits[varbit];
            int j = varBit.getSetting();
            int k = varBit.getLow();
            int l = varBit.getHigh();
            int i1 = Client.BIT_MASKS[l - k];
            i = clientInstance.settings[j] >> k & i1;
        } else if (varp != -1)
            i = clientInstance.settings[varp];
        if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
            return null;
        else
            return lookup(childrenIDs[i]);
    }

    public Model model(int j, int k, int l) {
        Model model = null;
        long l1;
        if (modelTypes == null) {
            if (j != 10)
                return null;
            l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) models.get(l1);
            if (model_1 != null) {
                return model_1;
            }
            if (modelIds == null)
                return null;
            boolean flag1 = inverted ^ (l > 3);
            int k1 = modelIds.length;
            for (int i2 = 0; i2 < k1; i2++) {
                int l2 = modelIds[i2];
                if (flag1)
                    l2 += 0x10000;
                model = (Model) baseModels.get(l2);
                if (model == null) {
                    model = Model.getModel(l2 & 0xffff);
                    if (model == null)
                        return null;
                    if (flag1)
                        model.method477();
                    baseModels.put(model, l2);
                }
                if (k1 > 1)
                    aModelArray741s[i2] = model;
            }

            if (k1 > 1)
                model = new Model(k1, aModelArray741s);
        } else {
            int i1 = -1;
            for (int j1 = 0; j1 < modelTypes.length; j1++) {
                if (modelTypes[j1] != j)
                    continue;
                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;
            l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            Model model_2 = (Model) models.get(l1);
            if (model_2 != null) {
                return model_2;
            }
            if (modelIds == null) {
                return null;
            }
            int j2 = modelIds[i1];
            boolean flag3 = inverted ^ (l > 3);
            if (flag3)
                j2 += 0x10000;
            model = (Model) baseModels.get(j2);
            if (model == null) {
                model = Model.getModel(j2 & 0xffff);
                if (model == null)
                    return null;
                if (flag3)
                    model.method477();
                baseModels.put(model, j2);
            }
        }
        boolean flag;
        flag = scaleX != 128 || scaleY != 128 || scaleZ != 128;
        boolean flag2;
        flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
        Model model_3 = new Model(modifiedModelColors == null,
                Frame.noAnimationInProgress(k), l == 0 && k == -1 && !flag
                && !flag2, model);
        if (k != -1) {
            model_3.skin();
            model_3.applyTransform(k);
            model_3.faceGroups = null;
            model_3.vertexGroups = null;
        }
        while (l-- > 0)
            model_3.rotate90Degrees();
        if (modifiedModelColors != null) {
            for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
                model_3.recolor(modifiedModelColors[k2],
                        originalModelColors[k2]);

        }
	/*	if (modifiedTexture != null) {
			for (int k2 = 0; k2 < modifiedTexture.length; k2++)
				model_3.retexture(modifiedTexture[k2], originalTexture[k2],
						-1);
		}*/
        if (flag)
            model_3.scale(scaleX, scaleZ, scaleY);
        if (flag2)
            model_3.translate(translateX, translateY, translateZ);
        //	model_3.light(84, 1500, -90, -280, -70, !delayShading);
        model_3.light(64 + ambientLighting, 1300 + (lightDiffusion * 5), -90, -280, -70, !delayShading);
        if (supportItems == 1)
            model_3.itemDropHeight = model_3.modelBaseY;
        models.put(model_3, l1);
        return model_3;
    }

}