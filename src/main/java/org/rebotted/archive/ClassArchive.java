package org.rebotted.archive;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.rebotted.directory.DirectoryManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassArchive {
    public final ArrayList<String> classNames;
    public final Map<String, ClassNode> classes;
    public final Map<String, byte[]> classesBytes;
    public final Map<String, byte[]> resourcesBytes;
    public final Map<String, File> resources;

    public ClassArchive() {
        this.classNames = new ArrayList<>();
        this.classes = new HashMap<>();
        this.resources = new HashMap<>();
        this.classesBytes = new HashMap<>();
        this.resourcesBytes = new HashMap<>();

    }

    public void inheritClassArchive(ClassArchive classArchive) {
        if (classArchive == null)
            return;
        inheritClassNodeCache(classArchive);
        inheritClassNames(classArchive);
        inheritResourceCache(classArchive);
    }

    private void inheritClassNodeCache(ClassArchive classArchive) {
        for (Map.Entry<String, ClassNode> classNodes : classArchive.classes.entrySet()) {
            if (classes.containsKey(classNodes.getKey())) {
                classes.remove(classNodes.getKey());
                classes.put(classNodes.getKey(), classNodes.getValue());
            } else {
                classes.put(classNodes.getKey(), classNodes.getValue());
            }
        }
    }

    private void inheritResourceCache(ClassArchive classArchive) {
        for (Map.Entry<String, File> resource : classArchive.resources.entrySet()) {
            if (resources.containsKey(resource.getKey())) {
                resources.remove(resource.getKey());
                resources.put(resource.getKey(), resource.getValue());
            } else {
                resources.put(resource.getKey(), resource.getValue());
            }
        }
    }

    private void inheritClassNames(ClassArchive classArchive) {
        for (String s : classArchive.classNames) {
            if (!classNames.contains(s)) {
                classNames.add(s);
            }
        }
    }

    public void loadClasses(Map<String, byte[]> classesBytes) {
        for (Map.Entry<String, byte[]> entry : classesBytes.entrySet()) {
            final ClassReader cr = new ClassReader(entry.getValue());
            final ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            final byte[] b = cw.toByteArray();
            classesBytes.put(cn.name + ".class", b);
            if (!classNames.contains(cn.name.replace('/', '.'))) {
                classNames.add(cn.name.replace('/', '.'));
            }
            classes.remove(cn.name);
            classes.put(cn.name, cn);
        }
    }

    public void loadResources(Map<String, byte[]> resourcesBytes) {
        for (Map.Entry<String, byte[]> entry : resourcesBytes.entrySet()) {
            try {
                String path;
                path = DirectoryManager.TEMP_PATH + File.separator;
                File f1 = new File(path);

                final File f = File.createTempFile("bot", ".tmp", f1);
                f.deleteOnExit();
                try (FileOutputStream fileOuputStream = new FileOutputStream(f)) {
                    fileOuputStream.write(entry.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resources.put(entry.getKey(), f);
                resourcesBytes.put(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadClass(InputStream in) throws IOException {
        final ClassReader cr = new ClassReader(in);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.EXPAND_FRAMES);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        final byte[] b = cw.toByteArray();
        classesBytes.put(cn.name + ".class", b);
        if (!classNames.contains(cn.name.replace('/', '.'))) {
            classNames.add(cn.name.replace('/', '.'));
        }
        classes.remove(cn.name);
        classes.put(cn.name, cn);

    }


    public void loadResource(final String name, final InputStream in) throws IOException {
        String path;
        path = DirectoryManager.TEMP_PATH + File.separator;
        File f1 = new File(path);

        final File f = File.createTempFile("bot", ".tmp", f1);
        f.deleteOnExit();
        final byte[] fileContent = Files.readAllBytes(f.toPath());
        try (OutputStream out = new FileOutputStream(f)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
        }
        resources.put(name, f);
        resourcesBytes.put(name, fileContent);
    }

    public void addJar(final File file) {
        try {
            addJar(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addJar(final URL url) {
        try {
            addJar(url.openConnection());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addJar(final URLConnection connection) {
        try {
            final ZipInputStream zin = new ZipInputStream(connection.getInputStream());
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory())
                    continue;
                if (e.getName().endsWith(".class")) {

                    loadClass(zin);
                } else {
                    loadResource(e.getName(), zin);
                }
            }
            zin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ClassNode> getClasses() {
        return classes;
    }
}