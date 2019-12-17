package org.rebotted.script.loader;

import org.rebotted.archive.ASMClassLoader;
import org.rebotted.archive.ClassArchive;
import org.rebotted.bot.data.APIData;
import org.rebotted.directory.DirectoryManager;
import org.rebotted.script.scriptdata.ScriptData;
import org.rebotted.script.scriptdata.ScriptManifest;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ScriptLoader {
    private APIData apiData;

    public ScriptLoader(APIData apiData) {
        this.apiData = apiData;
    }

    private final List<ScriptData> scripts = new ArrayList<>();

    public List<ScriptData> getScripts() {
        scripts.clear();
        scripts.addAll(loadLocalScripts());
        return scripts;
    }

    private final List<ScriptData> loadLocalScripts() {
        final List<ScriptData> scripts = new ArrayList<>();
        try {
            for (File file : DirectoryManager.getInstance().getRootDirectory().getSubDirectory(DirectoryManager.SCRIPTS).getFiles()) {
                if (file.getAbsolutePath().endsWith(".jar")) {
                    final ClassArchive classArchive = new ClassArchive();
                    classArchive.inheritClassArchive(apiData.getClassArchive());
                    classArchive.addJar(file);
                    final ASMClassLoader classLoader = new ASMClassLoader(classArchive);
                    classLoader.inheritClassLoader(apiData.getClassLoader());
                    try (JarInputStream inputStream = new JarInputStream(new FileInputStream(file))) {
                        JarEntry jarEntry;
                        while ((jarEntry = inputStream.getNextJarEntry()) != null) {
                            if (jarEntry.getName().endsWith(".class") && !jarEntry.getName().contains("$")) {
                                final String classPackage = jarEntry.getName().replace(".class", "");
                                final Class<?> clazz = classLoader.loadClass(classPackage.replaceAll("/", "."));
                                if (clazz.isAnnotationPresent(ScriptManifest.class)) {
                                    final ScriptManifest manifest = clazz.getAnnotation(ScriptManifest.class);
                                    final ScriptData scriptData = new ScriptData(clazz, manifest.name(), manifest.description(), manifest.version(), manifest.author(), manifest.category(), file);
                                    scripts.add(scriptData);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scripts;
    }

}
