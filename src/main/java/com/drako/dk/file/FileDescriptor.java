package com.drako.dk.file;

import java.nio.file.Path;

/**
 * La clase FileDescriptor proporciona métodos para manejar operaciones relacionadas con archivos, incluyendo la creación de rutas de archivo completas.
 */
public class FileDescriptor {
    /**
     * La ruta completa del archivo.
     */
    protected Path fileFullPath;

    /**
     * Crea un nuevo objeto FileManager con la ruta completa del archivo.
     *
     * @param fileFullPath La ruta completa del archivo.
     */
    public FileDescriptor(Path fileFullPath) {
        this.fileFullPath = fileFullPath;
    }

    /**
     * Crea un nuevo objeto FileManager con la ruta y el nombre del archivo.
     *
     * @param filePath La ruta del archivo.
     * @param fileName El nombre del archivo.
     */
    public FileDescriptor(String filePath, String fileName) {
        this.fileFullPath = getFullFilePath(filePath, fileName);
    }

    /**
     * Crea un nuevo objeto FileManager con la ruta y el nombre del archivo.
     *
     * @param filePath La ruta del archivo.
     * @param fileName El nombre del archivo.
     */
    public FileDescriptor(Path filePath, String fileName) {
        this.fileFullPath = filePath.resolve(fileName);
    }

    /**
     * Obtiene la ruta completa de un archivo a partir de la ruta y el nombre del archivo proporcionados.
     *
     * @param filePath La ruta del archivo.
     * @param fileName El nombre del archivo.
     * @return La ruta completa del archivo.
     * @throws IllegalArgumentException Si la ruta del archivo o el nombre del archivo es nulo o está vacío.
     */
    public static Path getFullFilePath(String filePath, String fileName) throws IllegalArgumentException {
        if (filePath != null && fileName != null && !fileName.isBlank())
            return Path.of(filePath, fileName);
        else
            throw new IllegalArgumentException("filePath or fileName is null or empty");
    }

    /**
     * Establece el nombre del archivo para la ruta actual.
     *
     * @param fileName El nuevo nombre del archivo.
     */
    public void setFileName(String fileName) {
        Path directory = fileFullPath.getParent();
        if (directory != null)
            fileFullPath = directory.resolve(fileName);
        else
            fileFullPath = Path.of(fileName);
    }

    /**
     * Obtiene la ruta completa del archivo.
     *
     * @return La ruta completa del archivo.
     */
    public Path getFileFullPath() {
        return fileFullPath;
    }

    /**
     * Establece la ruta completa del archivo.
     *
     * @param fileFullPath La nueva ruta completa del archivo.
     */
    public void setFileFullPath(Path fileFullPath) {
        this.fileFullPath = fileFullPath;
    }
}
