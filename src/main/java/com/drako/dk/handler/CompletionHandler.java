package com.drako.dk.handler;

/**
 * Interfaz funcional para manejar el resultado exitoso o el error de una operación asíncrona.
 *
 * @param <T> El tipo del resultado exitoso de la operación.
 */
public interface CompletionHandler<T> {
    /**
     * Se llama cuando la operación se completa con éxito.
     *
     * @param result El resultado de la operación exitosa.
     */
    void onSuccessResult(T result);

    /**
     * Se llama cuando la operación falla.
     *
     * @param ex La excepción que causó el error.
     */
    void onError(Exception ex);
}
