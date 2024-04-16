package com.drako.dk.manager;

import com.drako.dk.handler.CompletionHandler;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * La clase FileManager proporciona funcionalidades para la gestión de archivos, incluyendo operaciones de
 * copiar, mover y eliminar archivos de forma asíncrona.
 */
public class FileManager {
    /**
     * Copia un archivo desde la ruta de origen a la ruta de destino en un hilo secundario.
     *
     * @param sourcePath      La ruta del archivo de origen que se copiará.
     * @param destinationPath La ruta del archivo de destino donde se copiará el archivo.
     * @param onComplete      El manejador que se ejecutará después de que se haya completado la operación de copia de archivo (opcional).
     */
    public static void copyFile(Path sourcePath, Path destinationPath, CompletionHandler<Path> onComplete) {
        new Thread(() -> {
            try {
                Path targetPath = Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                if (onComplete != null)
                    onComplete.onSuccessResult(targetPath);
            } catch (IOException e) {
                if (onComplete != null)
                    onComplete.onError(e);
            }
        }).start();
    }

    /**
     * Mueve un archivo desde la ruta de origen a la ruta de destino en un hilo secundario.
     *
     * @param sourcePath      La ruta del archivo de origen que se moverá.
     * @param destinationPath La ruta del archivo de destino donde se moverá el archivo.
     * @param onComplete      El manejador que se ejecutará después de que se haya completado la operación de movimiento de archivo (opcional).
     */
    public static void moveFile(Path sourcePath, Path destinationPath, CompletionHandler<Path> onComplete) {
        new Thread(() -> {
            try {
                Path targetPath = Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                if (onComplete != null)
                    onComplete.onSuccessResult(targetPath);
            } catch (IOException e) {
                if (onComplete != null)
                    onComplete.onError(e);
            }
        }).start();
    }

    /**
     * Elimina un archivo en la ruta especificada en un hilo secundario.
     *
     * @param filePath   La ruta del archivo que se eliminará.
     * @param onComplete El manejador que se ejecutará después de que se haya completado la operación de eliminación de archivo (opcional).
     */
    public static void deleteFile(Path filePath, CompletionHandler<Path> onComplete) {
        new Thread(() -> {
            try {
                Files.delete(filePath);
                if (onComplete != null)
                    onComplete.onSuccessResult(filePath);
            } catch (IOException e) {
                if (onComplete != null)
                    onComplete.onError(e);
            }
        }).start();
    }

    /**
     * Verifica si un archivo existe en la ruta especificada.
     *
     * @param filePath La ruta del archivo a verificar.
     * @param onError  El manejador de error que se ejecutará si ocurre un error durante la operación de verificación de existencia de archivo (opcional).
     * @return {@code true} si el archivo existe en la ruta especificada, {@code false} si no existe o si ocurre un error durante la operación.
     */
    public static boolean fileExists(Path filePath, Consumer<Exception> onError) {
        try {
            return Files.exists(filePath);
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return false;
    }

    /**
     * Lista todos los archivos o directorios en la carpeta especificada de forma opcional recursiva.
     *
     * @param folderPath La ruta de la carpeta de la cual se listarán los archivos o directorios.
     * @param includeDirectories Indica si se deben incluir directorios en la lista (true) o no (false).
     * @param recursive Indica si la búsqueda debe ser recursiva, incluyendo subdirectorios (true) o no (false).
     * @param onError Un consumidor de excepciones que se invocará si ocurre un error durante el proceso de listado.
     *                Puede ser nulo si no se desea manejar las excepciones.
     * @return Una lista de objetos Path que representan los archivos o directorios en la carpeta especificada.
     *         Si ocurre un error al acceder a la carpeta o si la carpeta no existe, se devuelve una lista vacía.
     */
    public static List<Path> listFilesInFolder(Path folderPath, boolean includeDirectories, boolean recursive, Consumer<Exception> onError) {
        List<Path> fileList = new ArrayList<>();
        try {
            if (recursive) {
                try (var stream = Files.walk(folderPath, FileVisitOption.FOLLOW_LINKS)) {
                    stream.filter(path -> includeDirectories || Files.isRegularFile(path))
                            .forEach(fileList::add);
                }
            } else {
                try (var stream = Files.list(folderPath)) {
                    stream.filter(path -> includeDirectories || Files.isRegularFile(path))
                            .forEach(fileList::add);
                }
            }
        } catch (IOException e) {
            if(onError != null)
                onError.accept(e);
        }
        return fileList;
    }
}