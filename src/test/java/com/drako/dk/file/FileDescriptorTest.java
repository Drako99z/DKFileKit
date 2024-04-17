package com.drako.dk.file;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileDescriptorTest {

    static final String TEST_PATH = Paths.get("path", "to").toString().concat(FileSystems.getDefault().getSeparator());

    @Test
    public void testConstructorWithPath() {
        Path filePath = Paths.get(TEST_PATH);
        FileDescriptor fileDescriptor = new FileDescriptor(filePath);
        assertEquals(filePath, fileDescriptor.getFileFullPath());
    }

    @Test
    public void testConstructorWithFilePathAndFileName() {
        String filePath = TEST_PATH;
        String fileName = "file.txt";
        FileDescriptor fileDescriptor = new FileDescriptor(filePath, fileName);
        assertEquals(Paths.get(filePath, fileName), fileDescriptor.getFileFullPath());
    }

    @Test
    public void testConstructorWithPathAndFileName() {
        Path filePath = Paths.get(TEST_PATH);
        String fileName = "file.txt";
        FileDescriptor fileDescriptor = new FileDescriptor(filePath, fileName);
        assertEquals(filePath.resolve(fileName), fileDescriptor.getFileFullPath());
    }

    @Test
    public void testGetFullFilePath() {
        String filePath = TEST_PATH;
        String fileName = "file.txt";
        Path expectedPath = Paths.get(filePath, fileName);
        assertEquals(expectedPath, FileDescriptor.getFullFilePath(filePath, fileName));
    }

    @Test
    public void testGetFullFilePathWithNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileDescriptor.getFullFilePath(null, null);
        });
    }

    @Test
    public void testGetFullFilePathWithBlankFileName() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileDescriptor.getFullFilePath(TEST_PATH, "");
        });
    }

    @Test
    public void testSetFileName() {
        String filePath = TEST_PATH;
        String originalFileName = "original.txt";
        String newFileName = "new.txt";
        FileDescriptor fileDescriptor = new FileDescriptor(filePath, originalFileName);
        fileDescriptor.setFileName(newFileName);
        assertEquals(filePath + newFileName, fileDescriptor.getFileFullPath().toString());
    }
}