package org.rebotted.script.scriptdata;

import java.io.File;

public class ScriptData {

    private final Class<?> clazz;
    private final String name, author, desc;
    private final double version;
    private final SkillCategory skillCategory;
    private final int scriptId;
    private final File scriptPath;

    public ScriptData(Class<?> clazz, String name, String desc, double version, String author, SkillCategory category, File scriptPath) {
        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
        this.version = version;
        this.author = author;
        this.skillCategory = category;
        this.scriptId = -1;
        this.scriptPath = scriptPath;
    }

    public Class<?> getMainClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public double getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }

    public int getScriptId() {
        return scriptId;
    }

    public File getScriptPath() {
        return scriptPath;
    }
}