package com.drako.dk.file;

import com.drako.dk.handler.CompletionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {
    static final Path TEST_FILE_PATH = Paths.get("src", "test");
    static final String TEST_FILE_NAME = "source.txt";

    @BeforeEach
    void createTestFile() throws IOException {
        Path filePath = TEST_FILE_PATH.resolve(TEST_FILE_NAME);
        if(!Files.exists(filePath)){
            Files.createFile(filePath);
        }
    }

    @Test
    void testFileExists() throws IOException{
        Path existingFilePath = TEST_FILE_PATH.resolve(TEST_FILE_NAME);
        Path nonExistingFilePath = TEST_FILE_PATH.resolve("non_existing.txt");

        assertTrue(FileManager.fileExists(existingFilePath, null));
        assertFalse(FileManager.fileExists(nonExistingFilePath, null));
    }

    @Test
    void testCopyFile() throws ExecutionException, InterruptedException {
        Path sourcePath = TEST_FILE_PATH.resolve(TEST_FILE_NAME);
        Path destinationPath = TEST_FILE_PATH.resolve("destination.txt");

        CompletableFuture<Path> copyFuture = new CompletableFuture<>();
        FileManager.copyFile(sourcePath, destinationPath, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                copyFuture.complete(result);
            }

            @Override
            public void onError(Exception e) {
                copyFuture.completeExceptionally(e);
            }
        });

        Path copiedPath = copyFuture.get();
        assertTrue(Files.exists(copiedPath));
        assertTrue(Files.isRegularFile(copiedPath));
        try {
            Files.deleteIfExists(copiedPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMoveFile() throws ExecutionException, InterruptedException {
        Path sourcePath = TEST_FILE_PATH.resolve(TEST_FILE_NAME);
        Path destinationPath = TEST_FILE_PATH.resolve("destination.txt");

        CompletableFuture<Path> moveFuture = new CompletableFuture<>();
        FileManager.moveFile(sourcePath, destinationPath, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                moveFuture.complete(result);
            }

            @Override
            public void onError(Exception e) {
                moveFuture.completeExceptionally(e);
            }
        });

        Path movedPath = moveFuture.get();
        assertTrue(Files.exists(movedPath));
        assertTrue(Files.isRegularFile(movedPath));
        assertFalse(Files.exists(sourcePath));
        try {
            Files.deleteIfExists(movedPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteFile() throws ExecutionException, InterruptedException {
        Path filePath = TEST_FILE_PATH.resolve(TEST_FILE_NAME);

        CompletableFuture<Path> deleteFuture = new CompletableFuture<>();
        FileManager.deleteFile(filePath, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                deleteFuture.complete(result);
            }

            @Override
            public void onError(Exception e) {
                deleteFuture.completeExceptionally(e);
            }
        });

        Path deletedPath = deleteFuture.get();
        assertFalse(Files.exists(deletedPath));
    }
}