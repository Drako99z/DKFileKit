package com.drako.dk.manager;

import com.drako.dk.handler.CompletionHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class LogManagerTest {

    @Test
    void testLogAndLogAsync() throws ExecutionException, InterruptedException {
        LogManager logManager = LogManager.getInstance();
        CompletableFuture<Boolean> syncFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> asyncFuture = new CompletableFuture<>();

        boolean syncResult = logManager.log("Sync log test", LogLevel.INFO);
        assertTrue(syncResult, "Failed to log synchronously");

        logManager.logAsync("Async log test", LogLevel.INFO, new CompletionHandler<Path>() {
            @Override
            public void onSuccessResult(Path result) {
                asyncFuture.complete(true);
            }

            @Override
            public void onError(Exception ex) {
                asyncFuture.completeExceptionally(ex);
            }
        });

        assertTrue(asyncFuture.get(), "Failed to log asynchronously");
    }

    @Test
    void testGetLogMessage() {
        LogManager logManager = LogManager.getInstance();
        String logMessage = logManager.getLogMessage("Test message", LogLevel.INFO);
        assertNotNull(logMessage);
        assertTrue(logMessage.contains("Test message"));
        assertTrue(logMessage.contains(LogLevel.INFO.toString()));
    }

    @Test
    void testLogException() {
        LogManager logManager = LogManager.getInstance();
        boolean result = logManager.log(new RuntimeException("Test exception"));
        assertTrue(result, "Failed to log exception");
    }

    @Test
    void testGetInstance() {
        LogManager logManager1 = LogManager.getInstance();
        LogManager logManager2 = LogManager.getInstance();
        assertSame(logManager1, logManager2, "Instances are not the same");
    }

    @AfterAll
    static void clearLogFile(){
        try{
            Files.deleteIfExists(Path.of(System.getProperty("user.dir"), LogManager.LogFileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}