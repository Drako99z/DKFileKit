package com.drako.dk.manager;

import com.drako.dk.handler.CompletionHandler;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase para gestionar registros de logs.
 */
public class LogManager {
    /**
     * Formateador de fecha y hora para los registros de log.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Nombre del archivo de log.
     */
    public static String LogFileName = "log.txt";

    /**
     * Instancia única de LogManager.
     */
    public static LogManager manager;

    /**
     * Manejador persistente para guardar los registros de log.
     */
    private final PersistManager persistManager;

    /**
     * Constructor privado para evitar instanciación directa.
     * Se utiliza un manejador persistente con un archivo de log predeterminado.
     */
    private LogManager() {
        this.persistManager = new PersistManager("", LogFileName);
    }

    /**
     * Obtiene la instancia única de LogManager.
     *
     * @return La instancia única de LogManager.
     */
    public static LogManager getInstance() {
        if (manager == null) {
            manager = new LogManager();
        }
        return manager;
    }

    /**
     * Obtiene un mensaje de registro formateado con el nivel de registro y la marca de tiempo actuales.
     *
     * @param message El mensaje que se incluirá en el registro.
     * @param level   El nivel de importancia del mensaje de registro.
     * @return Un mensaje de registro formateado con la marca de tiempo, el nivel y el mensaje proporcionados.
     */
    public String getLogMessage(String message, LogLevel level) {
        return String.format("[%s] [%s] %s%n", LocalDateTime.now().format(DATE_TIME_FORMATTER), level.toString(), message);
    }

    /**
     * Registra un mensaje de error en el log.
     *
     * @param ex La excepción a registrar.
     * @return {@code true} si el log se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean log(Exception ex) {
        return log(ex.toString(), LogLevel.ERROR);
    }

    /**
     * Registra un mensaje de información en el log.
     *
     * @param message El mensaje a registrar.
     * @return {@code true} si el log se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean log(String message) {
        return log(message, LogLevel.INFO);
    }

    /**
     * Registra un mensaje en el log con un nivel especificado.
     *
     * @param message El mensaje a registrar.
     * @param level   El nivel de log del mensaje.
     * @return {@code true} si el log se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean log(String message, LogLevel level) {
        String logMessage = getLogMessage(message, level);
        return persistManager.writeTextFile(logMessage, true);
    }

    /**
     * Registra un mensaje de manera asíncrona utilizando un nivel de importancia especificado.
     *
     * @param message    El mensaje que se registrará.
     * @param level      El nivel de importancia del mensaje de registro.
     * @param onComplete El manejador de finalización que se llamará una vez que la operación de registro se haya completado.
     *                   Debe proporcionarse y no puede ser nulo. La implementación de CompletionHandler se invocará con el
     *                   resultado de la operación de escritura y la ruta completa del archivo donde se escribió el registro
     *                   en caso de éxito, o con una excepción en caso de error.
     */
    public void logAsync(String message, LogLevel level, CompletionHandler<Path> onComplete) {
        String logMessage = getLogMessage(message, level);
        persistManager.writeTextFileAsync(logMessage, true, onComplete);
    }
}
