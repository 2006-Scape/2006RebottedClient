package org.rebotted.bot.data;

import org.rebotted.archive.ClassArchive;

import java.io.File;

public class APIData {

    private final Class<?> clazz;
    private final double version;
    private final File apiPath;
    private final ClassLoader classLoader;
    private final ClassArchive classArchive;

    public APIData(Class<?> clazz, double version, File apiPath, ClassArchive classArchive, ClassLoader classLoader) {
        this.clazz = clazz;
        this.version = version;
        this.apiPath = apiPath;
        this.classArchive = classArchive;
        this.classLoader = classLoader;
    }

    public Class<?> getMainClass() {
        return clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public double getVersion() {
        return version;
    }

    public File getApiPath() {
        return apiPath;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ClassArchive getClassArchive() {
        return classArchive;
    }
}
