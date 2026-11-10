package casino;

public class Banca {
    private long saldo;
    public Banca(long saldo) { this.saldo = saldo; }
    public synchronized long getSaldo() { return saldo; }
    public synchronized void cobrar(long cantidad) { saldo += cantidad; }
    public synchronized boolean pagar(long cantidad) {
        if (cantidad < 0 || cantidad > saldo) return false;
        saldo -= cantidad;
        return true;
    }
}
