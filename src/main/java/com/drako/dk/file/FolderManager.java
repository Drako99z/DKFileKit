package com.drako.dk.file;

import com.drako.dk.handler.CompletionHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FolderManager {
    /**
     * Crea una nueva carpeta en la ruta especificada.
     *
     * @param folderPath La ruta donde se creará la carpeta.
     * @param onComplete El manejador que se ejecutará después de que se haya completado la operación de creación de carpeta (opcional).
     */
    public static void createFolder(Path folderPath, CompletionHandler<Path> onComplete) {
        new Thread(() -> {
            try {
                Files.createDirectories(folderPath);
                if (onComplete != null)
                    onComplete.onSuccessResult(folderPath);
            } catch (Exception e) {
                if (onComplete != null)
                    onComplete.onError(e);
            }
        }).start();
    }

    /**
     * Elimina una carpeta y su contenido en la ruta especificada.
     *
     * @param folderPath La ruta de la carpeta que se eliminará.
     * @param onComplete El manejador que se ejecutará después de que se haya completado la operación de eliminación de carpeta (opcional).
     */
    public static void deleteFolder(Path folderPath, CompletionHandler<Path> onComplete) {
        new Thread(() -> {
            try (Stream<Path> files = Files.walk(folderPath)) {
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                if (onComplete != null)
                    onComplete.onSuccessResult(folderPath);
            } catch (Exception e) {
                if (onComplete != null)
                    onComplete.onError(e);
            }
        }).start();
    }

    /**
     * Verifica si una carpeta existe en la ruta especificada.
     *
     * @param folderPath La ruta de la carpeta a verificar.
     * @param onError    El manejador de error que se ejecutará si ocurre un error durante la operación de verificación de existencia de carpeta (opcional).
     * @return {@code true} si la carpeta existe en la ruta especificada, {@code false} si no existe o si ocurre un error durante la operación.
     */
    public static boolean folderExists(Path folderPath, Consumer<Exception> onError) {
        try {
            return Files.exists(folderPath);
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return false;
    }

    /**
     * Obtiene la ruta a la carpeta personal del usuario.
     *
     * @return La ruta a la carpeta personal del usuario.
     */
    public static Path getUserDirectory() {
        return Path.of(System.getProperty("user.home"));
    }

    /**
     * Retorna la ruta completa al elemento especificado dentro de la carpeta personal del usuario.
     *
     * @param element El nombre del directorio o archivo dentro de la carpeta personal del usuario.
     * @return La ruta completa al elemento especificado dentro de la carpeta personal del usuario.
     */
    public static Path getUserDirectory(String element) {
        return getUserDirectory().resolve(element);
    }

    /**
     * Obtiene la ruta a la carpeta del directorio de trabajo actual.
     *
     * @return La ruta a la carpeta del directorio de trabajo actual.
     */
    public static Path getCurrentDirectory() {
        return Path.of(System.getProperty("user.dir"));
    }

    /**
     * Retorna la ruta completa al elemento especificado dentro del directorio de trabajo actual.
     *
     * @param element El nombre del directorio o archivo dentro del directorio de trabajo actual.
     * @return La ruta completa al elemento especificado dentro del directorio de trabajo actual.
     */
    public static Path getCurrentDirectory(String element) {
        return getCurrentDirectory().resolve(element);
    }
}
