package br.ufrn.imd.valoris.exception;

public class NotEnoughAccountBalanceException extends RuntimeException {
    public NotEnoughAccountBalanceException(String message) { super(message); }
}
