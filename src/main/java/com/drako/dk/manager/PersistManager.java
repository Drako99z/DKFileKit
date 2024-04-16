package com.drako.dk.manager;

import com.drako.dk.file.FileDescriptor;
import com.drako.dk.handler.CompletionHandler;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * La clase PersistManager facilita la persistencia de datos al proporcionar métodos para almacenar y recuperar objetos serializables en archivos binarios, así como para escribir y leer contenido de texto en archivos.
 */
public class PersistManager extends FileDescriptor {

    /**
     * Crea un nuevo objeto PersistManager con la ruta completa del archivo.
     *
     * @param fileFullPath La ruta completa del archivo.
     */
    public PersistManager(Path fileFullPath) {
        super(fileFullPath);
    }

    /**
     * Crea un nuevo objeto PersistManager con la ruta y el nombre del archivo.
     *
     * @param filePath La ruta del archivo.
     * @param fileName El nombre del archivo.
     */
    public PersistManager(String filePath, String fileName) {
        super(filePath, fileName);
    }

    /**
     * Guarda un objeto {@link Serializable} en un archivo binario.
     * Se proporciona un controlador para manejar cualquier excepción que ocurra durante el proceso de guardado.
     *
     * @param object El objeto a ser guardado. Debe ser serializable.
     * @param <T>    El tipo del objeto a ser guardado, que debe implementar la interfaz Serializable.
     * @return {@code true} si el objeto se guardó exitosamente; de lo contrario, {@code false}.
     */
    public <T extends Serializable> boolean saveObject(T object) {
        return saveObject(object, null);
    }

    /**
     * Guarda un objeto {@link Serializable} en un archivo binario.
     * Se proporciona un controlador para manejar cualquier excepción que ocurra durante el proceso de guardado.
     *
     * @param object  El objeto a ser guardado. Debe ser serializable.
     * @param onError Un consumidor de excepciones que se invocará si ocurre un error durante el proceso de guardado.
     *                Puede ser nulo si no se desea manejar las excepciones.
     * @param <T>     El tipo del objeto a ser guardado, que debe implementar la interfaz Serializable.
     * @return {@code true} si el objeto se guardó exitosamente; de lo contrario, {@code false}.
     */
    public <T extends Serializable> boolean saveObject(T object, Consumer<Exception> onError) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileFullPath.toFile()))) {
            outputStream.writeObject(object);
            return true;
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return false;
    }

    /**
     * Guarda un objeto serializable de manera asíncrona en un archivo.
     *
     * @param object     El objeto serializable que se va a guardar.
     * @param onComplete El manejador de finalización que se llamará una vez que la operación de guardado se haya completado.
     *                   Debe proporcionarse y no puede ser nulo. La implementación de CompletionHandler se invocará con el
     *                   resultado de la operación de guardado y la ruta completa del archivo donde se guardó el objeto
     *                   en caso de éxito, o con una excepción en caso de error.
     * @param <T>        El tipo del objeto serializable.
     */
    public <T extends Serializable> void saveObjectAsync(T object, CompletionHandler<Path> onComplete) {
        if (onComplete == null) {
            throw new IllegalArgumentException("CompletionHandler must not be null.");
        }
        new Thread(() -> {
            boolean result = saveObject(object, onComplete::onError);
            if (result)
                onComplete.onSuccessResult(fileFullPath);
        }).start();
    }

    /**
     * Lee un objeto serializado desde un archivo binario.
     * Se proporciona un controlador para manejar cualquier excepción que ocurra durante el proceso de lectura.
     *
     * @param objectClass El tipo de clase del objeto que se espera leer.
     * @param <T>         El tipo del objeto a ser leído, que debe implementar la interfaz {@link Serializable}.
     * @return Un {@link Optional} que contiene el objeto leído si se realizó la lectura correctamente; de lo contrario, un Optional vacío.
     */
    public <T extends Serializable> Optional<T> readObject(Class<T> objectClass) {
        return readObject(objectClass, null);
    }

    /**
     * Lee un objeto serializado desde un archivo binario.
     * Se proporciona un controlador para manejar cualquier excepción que ocurra durante el proceso de lectura.
     *
     * @param objectClass El tipo de clase del objeto que se espera leer.
     * @param onError     Un consumidor de excepciones que se invocará si ocurre un error durante el proceso de lectura.
     *                    Puede ser nulo si no se desea manejar las excepciones.
     * @param <T>         El tipo del objeto a ser leído, que debe implementar la interfaz {@link Serializable}.
     * @return Un {@link Optional} que contiene el objeto leído si se realizó la lectura correctamente; de lo contrario, un Optional vacío.
     */
    public <T extends Serializable> Optional<T> readObject(Class<T> objectClass, Consumer<Exception> onError) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileFullPath.toFile()))) {
            Object objectRes = inputStream.readObject();
            return Optional.of(objectClass.cast(objectRes));
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return Optional.empty();
    }

    /**
     * Lee un objeto serializable de manera asíncrona desde un archivo.
     *
     * @param objectClass El tipo de clase del objeto que se espera leer.
     * @param onComplete  El manejador de finalización que se llamará una vez que la operación de lectura se haya completado.
     *                    Debe proporcionarse y no puede ser nulo. La implementación de CompletionHandler se invocará con el
     *                    objeto leído en caso de éxito, o con una excepción en caso de error.
     * @param <T>         El tipo del objeto serializable que se espera leer.
     */
    public <T extends Serializable> void readObjectAsync(Class<T> objectClass, CompletionHandler<T> onComplete) {
        if (onComplete == null) {
            throw new IllegalArgumentException("CompletionHandler must not be null.");
        }
        new Thread(() -> {
            readObject(objectClass, onComplete::onError)
                    .ifPresent(onComplete::onSuccessResult);
        }).start();
    }

    /**
     * Escribe el contenido en un archivo de texto.
     *
     * @param content El contenido que se escribirá en el archivo.
     * @return {@code true} si el contenido se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean writeTextFile(String content) {
        return writeTextFile(content, false, null);
    }

    /**
     * Escribe el contenido en un archivo de texto.
     *
     * @param content El contenido que se escribirá en el archivo.
     * @param append  Indica si se debe añadir el contenido al final del archivo existente o reemplazarlo.
     * @return {@code true} si el contenido se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean writeTextFile(String content, boolean append) {
        return writeTextFile(content, append, null);
    }

    /**
     * Escribe el contenido en un archivo de texto.
     *
     * @param content El contenido que se escribirá en el archivo.
     * @param append  Indica si se debe añadir el contenido al final del archivo existente o reemplazarlo.
     * @param onError Un consumidor de excepciones que se invocará si ocurre un error durante el proceso de escritura.
     *                Puede ser nulo si no se desea manejar las excepciones.
     * @return {@code true} si el contenido se escribió correctamente en el archivo; de lo contrario, {@code false}.
     */
    public boolean writeTextFile(String content, boolean append, Consumer<Exception> onError) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileFullPath.toFile(), append))) {
            writer.write(content);
            return true;
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return false;
    }

    /**
     * Escribe el contenido de manera asíncrona en un archivo de texto.
     *
     * @param content    El contenido que se escribirá en el archivo de texto.
     * @param append     Indica si se debe añadir el contenido al final del archivo existente o reemplazarlo.
     * @param onComplete El manejador de finalización que se llamará una vez que la operación de escritura se haya completado.
     *                   Debe proporcionarse y no puede ser nulo. La implementación de CompletionHandler se invocará con el
     *                   resultado de la operación de escritura y la ruta completa del archivo donde se escribió el contenido
     *                   en caso de éxito, o con una excepción en caso de error.
     */
    public void writeTextFileAsync(String content, boolean append, CompletionHandler<Path> onComplete) {
        if (onComplete == null) {
            throw new IllegalArgumentException("CompletionHandler must not be null.");
        }
        new Thread(() -> {
            boolean result = writeTextFile(content, append, onComplete::onError);
            if (result)
                onComplete.onSuccessResult(fileFullPath);
        }).start();
    }

    /**
     * Lee el contenido de un archivo de texto.
     *
     * @return Un {@link Optional} que contiene el contenido del archivo de texto si la lectura se realizó correctamente; de lo contrario, un Optional vacío.
     */
    public Optional<String> readTextFile() {
        return readTextFile(null);
    }

    /**
     * Lee el contenido de un archivo de texto.
     *
     * @param onError Un consumidor de excepciones que se invocará si ocurre un error durante el proceso de lectura.
     *                Puede ser nulo si no se desea manejar las excepciones.
     * @return Un {@link Optional} que contiene el contenido del archivo de texto si la lectura se realizó correctamente; de lo contrario, un Optional vacío.
     */
    public Optional<String> readTextFile(Consumer<Exception> onError) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileFullPath.toFile()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return Optional.of(stringBuilder.toString());
        } catch (Exception e) {
            if (onError != null)
                onError.accept(e);
        }
        return Optional.empty();
    }

    /**
     * Lee el contenido de manera asíncrona desde un archivo de texto.
     *
     * @param onComplete El manejador de finalización que se llamará una vez que la operación de lectura se haya completado.
     *                   Debe proporcionarse y no puede ser nulo. La implementación de CompletionHandler se invocará con el
     *                   contenido del archivo leído en caso de éxito, o con una excepción en caso de error.
     */
    public void readTextFileAsync(CompletionHandler<String> onComplete) {
        if (onComplete == null) {
            throw new IllegalArgumentException("CompletionHandler must not be null.");
        }
        new Thread(() -> {
            readTextFile(onComplete::onError)
                    .ifPresent(onComplete::onSuccessResult);
        }).start();
    }
}
