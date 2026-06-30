package vetnova.ventas.exception;

public class StockNoDisponibleException extends RuntimeException {
    public StockNoDisponibleException(String message) {
        super(message);
    }
}
