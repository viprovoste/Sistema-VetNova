package vetnova.ventas.exception;

/** Se lanza cuando un microservicio externo (Inventario o Autenticacion) no responde o falla. */
public class ServicioExternoException extends RuntimeException {
    public ServicioExternoException(String message) {
        super(message);
    }
}
