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

class FolderManagerTest {
    static final Path TEST_FOLDER_PATH = Paths.get("src", "test", "folder_test");

    @BeforeEach
    void createTestFolder() throws IOException {
        Path folderPath = TEST_FOLDER_PATH;
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }
    }

    @Test
    void testFolderExists() throws IOException {
        Path existingFolderPath = TEST_FOLDER_PATH;
        Path nonExistingFolderPath = TEST_FOLDER_PATH.resolve("non_existing_folder");

        assertTrue(FolderManager.folderExists(existingFolderPath, null));
        assertFalse(FolderManager.folderExists(nonExistingFolderPath, null));
    }

    @Test
    void testCreateFolder() throws ExecutionException, InterruptedException {
        Path newFolderPath = TEST_FOLDER_PATH.resolve("new_folder");

        CompletableFuture<Path> createFuture = new CompletableFuture<>();
        FolderManager.createFolder(newFolderPath, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                createFuture.complete(result);
            }

            @Override
            public void onError(Exception e) {
                createFuture.completeExceptionally(e);
            }
        });

        Path createdPath = createFuture.get();
        assertTrue(Files.exists(createdPath));
        assertTrue(Files.isDirectory(createdPath));
        try {
            Files.deleteIfExists(createdPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteFolder() throws ExecutionException, InterruptedException {
        Path folderPath = TEST_FOLDER_PATH;

        CompletableFuture<Path> deleteFuture = new CompletableFuture<>();
        FolderManager.deleteFolder(folderPath, new CompletionHandler<Path>() {
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