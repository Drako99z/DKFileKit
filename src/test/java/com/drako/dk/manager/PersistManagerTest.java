package com.drako.dk.manager;

import com.drako.dk.handler.CompletionHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PersistManagerTest {
    static final String TEST_FILE_PATH = Paths.get("src", "test").toString();
    static final String OBJECT_FILE_NAME = "testObject.bin";
    static final String OBJECT_ASYNC_FILE_NAME = "testObjectAsync.bin";
    static final String TEXT_FILE_NAME = "testTextFile.txt";
    static final String TEXT_ASYNC_FILE_NAME = "testTextFileAsync.txt";

    @Test
    void testSaveAndReadObject() {
        PersistManager persistManager = new PersistManager(TEST_FILE_PATH, OBJECT_FILE_NAME);
        Serializable objectToSave = "Hello, world!";

        boolean saveResult = persistManager.saveObject(objectToSave);
        Optional<String> readResult = persistManager.readObject(String.class);

        assertTrue(saveResult);
        assertTrue(readResult.isPresent());
        assertEquals(objectToSave, readResult.get());
    }

    @Test
    void testWriteAndReadTextFile() {
        PersistManager persistManager = new PersistManager(TEST_FILE_PATH, TEXT_FILE_NAME);
        String contentToWrite = "This is a test content for text file.\n";

        boolean writeResult = persistManager.writeTextFile(contentToWrite);
        Optional<String> readResult = persistManager.readTextFile();

        assertTrue(writeResult);
        assertTrue(readResult.isPresent());
        assertEquals(contentToWrite, readResult.get());
    }

    @Test
    void testSaveAndReadObjectAsync() throws InterruptedException {
        PersistManager persistManager = new PersistManager(TEST_FILE_PATH, OBJECT_ASYNC_FILE_NAME);
        String objectToSave = "Hello, async world!";
        CountDownLatch latch = new CountDownLatch(1);

        persistManager.saveObjectAsync(objectToSave, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                fail("Async save failed: " + ex.getMessage());
            }
        });

        latch.await(5, TimeUnit.MILLISECONDS);

        Optional<String> readResult = persistManager.readObject(String.class);
        assertTrue(readResult.isPresent());
        assertEquals(objectToSave, readResult.get());
    }

    @Test
    void testWriteAndReadTextFileAsync() throws InterruptedException {
        PersistManager persistManager = new PersistManager(TEST_FILE_PATH, TEXT_ASYNC_FILE_NAME);
        String contentToWrite = "This is a test content for async text file.\n";
        CountDownLatch latch = new CountDownLatch(1);

        persistManager.writeTextFileAsync(contentToWrite, false, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                fail("Async write failed: " + ex.getMessage());
            }
        });

        latch.await(5, TimeUnit.MILLISECONDS);

        Optional<String> readResult = persistManager.readTextFile();
        assertTrue(readResult.isPresent());
        assertEquals(contentToWrite, readResult.get());
    }

    @AfterAll
    static void clearFiles(){
        try{
            Files.deleteIfExists(Path.of(TEST_FILE_PATH, OBJECT_FILE_NAME));
            Files.deleteIfExists(Path.of(TEST_FILE_PATH, TEXT_FILE_NAME));
            Files.deleteIfExists(Path.of(TEST_FILE_PATH, OBJECT_ASYNC_FILE_NAME));
            Files.deleteIfExists(Path.of(TEST_FILE_PATH, TEXT_ASYNC_FILE_NAME));
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}