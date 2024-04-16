package com.drako.dk.handler;

/**
 * Clase abstracta que proporciona una implementación vacía de la interfaz {@link CompletionHandler}.
 * Las clases que deseen implementar la interfaz {@link CompletionHandler} pueden extender esta clase
 * para evitar tener que proporcionar una implementación para todos los métodos de la interfaz.
 *
 * @param <T> El tipo del resultado exitoso de la operación.
 */
public abstract class CompletionHandlerAdapter<T> implements CompletionHandler<T> {

    /**
     * {@inheritDoc}
     * Esta implementación vacía no realiza ninguna acción.
     *
     * @param result El resultado de la operación exitosa.
     */
    @Override
    public void onSuccessResult(T result) {

    }

    /**
     * {@inheritDoc}
     * Esta implementación vacía no realiza ninguna acción.
     *
     * @param ex La excepción que causó el error.
     */
    @Override
    public void onError(Exception ex) {

    }
}
