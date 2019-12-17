package org.rebotted.bot.loader;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.rebotted.Client;
import org.rebotted.archive.ASMClassLoader;
import org.rebotted.archive.ClassArchive;
import org.rebotted.bot.data.APIData;
import org.rebotted.bot.data.APIManifest;
import org.rebotted.bot.data.RebottedAPI;
import org.rebotted.directory.DirectoryManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class APILoader {
    private final Client client;
    private RebottedAPI rebottedAPI;
    private APIData apiData;
    public APILoader(Client client, String link) {
        this.client = client;
        this.apiData = downloadAPI(link);
    }

    /**
     * Testing the loading of the API
     * @param args
     */
    public static void main(String[] args) {
        DirectoryManager.init();
        System.out.println(APIManifest.class.getCanonicalName());
        new APILoader(null, "https://parabot-osrs.000webhostapp.com/RebottedAPI.jar");
    }

    private APIData downloadAPI(String link) {
        try {
            byte[] bytes = getByteArray(link);
            final Map<String, byte[]> classMap = new HashMap<>();
            final Map<String, byte[]> resourceMap = new HashMap<>();
            final byte[] array = new byte[1024];
            final JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(bytes));
            ZipEntry nextEntry;
            while ((nextEntry = jarInputStream.getNextEntry()) != null) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JarInputStream jarInputStream2 = jarInputStream;
                int read;
                while ((read = jarInputStream2.read(array, 0, array.length)) != -1) {
                    jarInputStream2 = jarInputStream;
                    byteArrayOutputStream.write(array, 0, read);
                }
                if (nextEntry.getName().endsWith(".class")) {
                    classMap.put(nextEntry.getName(), byteArrayOutputStream.toByteArray());
                } else {
                    resourceMap.put(nextEntry.getName(), byteArrayOutputStream.toByteArray());
                }
            }
            final ClassArchive classArchive = new ClassArchive();
            classArchive.loadResources(resourceMap);
            classArchive.loadClasses(classMap);
            final ASMClassLoader classLoader = new ASMClassLoader(classArchive);
            for(ClassNode node : classArchive.classes.values()) {
                if (node.visibleAnnotations != null && node.visibleAnnotations.size() > 0) {
                    for (AnnotationNode annotationNode : node.visibleAnnotations) {
                        if (annotationNode.desc.equals("L" + APIManifest.class.getCanonicalName().replaceAll("\\.", "/") + ";")) {
                            final Class<?> clazz = classLoader.loadClass(node.name.replaceAll("/", "."));
                            final APIManifest manifest = clazz.getAnnotation(APIManifest.class);
                            rebottedAPI = (RebottedAPI) clazz.getConstructors()[0].newInstance(client);
                            System.out.println("Rebotted API version " + manifest.version() + " has been loaded...");
                            final APIData apiData = new APIData(clazz, manifest.version(), null, classArchive, classLoader);
                            return apiData;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String link) {
        try {
            final URL url = new URL(link);
            final DataInputStream dataInputStream = new DataInputStream(url.openStream());
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            while ((read = dataInputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            dataInputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RebottedAPI getRebottedAPI() {
        return rebottedAPI;
    }

    public APIData getApiData() {
        return apiData;
    }
}
