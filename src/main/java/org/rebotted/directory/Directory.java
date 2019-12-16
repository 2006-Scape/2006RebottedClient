package org.rebotted.directory;


import org.rebotted.directory.exceptions.InvalidDirectoryNameException;
import org.rebotted.directory.exceptions.InvalidFileNameException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;

public class Directory {

    private final File directory;

    public Directory(String path) {
        this.directory = new File(path);
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new InvalidPathException(path, "The specified path is not a directory.");
            }
        }
    }

    public Directory(File file) {
        this(file.getAbsolutePath());
    }

    public boolean create() throws IOException {
        return directory.mkdirs();
    }

    public URI toURI() {
        return directory.toURI();
    }

    public boolean exists() {
        return directory.exists();
    }

    public String getPath() {
        return directory.getAbsolutePath();
    }

    public String getName() {
        return directory.getName();
    }

    public boolean isSubDirectoryOf(Directory directory) {
        return directory.getPath().startsWith(getPath()) && !directory.getPath().equals(getPath());
    }

    public boolean isSubDirectoryOf(String path) {
        final Directory subDirectory = new Directory(path);
        return isSubDirectoryOf(subDirectory);
    }

    public boolean isParentDirectoryOf(Directory directory) {
        return directory.getPath().startsWith(getPath()) && !directory.getPath().equals(getPath());
    }

    public boolean isParentDirectoryOf(String name) {
        final Directory directory = new Directory(name);
        return isParentDirectoryOf(directory);
    }

    public boolean createFile(String path) throws IOException {
        final File file = new File(path);
        return createFile(file);
    }

    public boolean createFile(File file) throws IOException {
        if (file.exists()) {
            return true;
        }
        return file.createNewFile();
    }

    public boolean createSubDirectory(String name) throws IOException {
        final Directory directory = new Directory(getPath() + File.separator + name);
        if (isParentDirectoryOf(directory)) {
            return directory.create();
        }
        return false;
    }

    public File[] getAllFiles() {
        final ArrayList<File> files = new ArrayList<File>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                final Directory childDirectory = new Directory(file);
                files.addAll(Arrays.asList(childDirectory.getAllFiles()));
                continue;
            }
            files.add(file);
        }
        return files.toArray(new File[files.size()]);
    }

    public File[] getFiles() {
        final ArrayList<File> files = new ArrayList<File>();
        for (File file : directory.listFiles()) {
            if (!file.isDirectory() && file.getParent().equals(getPath())) {
                files.add(file);
            }
        }
        return files.toArray(new File[files.size()]);
    }

    public File getFile(String name) throws InvalidFileNameException {
        for (File file : getFiles()) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        throw new InvalidFileNameException(name);
    }

    public Directory[] getSubDirectories() {
        final ArrayList<Directory> directories = new ArrayList<Directory>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory() && file.getParent().equals(getPath())) {
                directories.add(new Directory(file.getPath()));
            }
        }
        return directories.toArray(new Directory[directories.size()]);
    }

    public Directory getSubDirectory(String name) throws InvalidDirectoryNameException {
        for (Directory directory : getSubDirectories()) {
            if (directory.getName().equals(name)) {
                return directory;
            }
        }
        throw new InvalidDirectoryNameException(name);
    }

}
