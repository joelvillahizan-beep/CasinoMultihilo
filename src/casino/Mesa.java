package casino;

import java.util.Random;

public class Mesa {
    private final Banca banca;
    private final long[] saldos = new long[12];
    private final long[] siguientes = new long[12];
    private final long[] apuestas = new long[12];
    private final int[] elecciones = new int[12];
    private final int limite;
    private final long totalInicial;
    private boolean activa = true, resultadoListo = false;
    private int turno = 1, recibidas = 0, comprobadas = 0, ganador;
    private String motivo = "";

    public Mesa(long fondos, int limite, boolean prueba) {
        banca = new Banca(fondos);
        totalInicial = fondos + 12000;
        this.limite = limite;
        Random azar = new Random();
        for (int i = 0; i < 12; i++) {
            saldos[i] = 1000;
            siguientes[i] = 10;
            elecciones[i] = prueba ? (i >= 4 && i < 8 ? 0 : 17)
                    : (i >= 4 && i < 8 ? azar.nextInt(2) : 1 + azar.nextInt(36));
        }
    }
    public String nombre(int i) {
        return (i < 4 ? "Numero-" : i < 8 ? "ParImpar-" : "Martingala-") + (i + 1);
    }
    public synchronized boolean apostar(int i, int esperado) throws InterruptedException {
        while (activa && (turno != esperado || resultadoListo)) wait();
        if (!activa) return false;
        long cantidad = siguientes[i];
        if (cantidad <= saldos[i]) {
            apuestas[i] = cantidad;
            saldos[i] -= cantidad;
            banca.cobrar(cantidad);
            String destino = i >= 4 && i < 8
                    ? (elecciones[i] == 0 ? "PAR" : "IMPAR") : "numero " + elecciones[i];
            System.out.println("T" + turno + " " + nombre(i) + " apuesta " + cantidad
                    + " EUR a " + destino);
        } else {
            apuestas[i] = 0;
            System.out.println("T" + turno + " " + nombre(i) + " sin saldo para " + cantidad);
        }
        recibidas++;
        notifyAll();
        return true;
    }
    public synchronized boolean esperarApuestas() throws InterruptedException {
        while (activa && recibidas < 12) wait();
        if (!activa) return false;
        long total = 0;
        for (long apuesta : apuestas) total += apuesta;
        if (total == 0) cerrar("Ningun jugador puede apostar");
        return activa;
    }
    private long premio(int i) {
        if (ganador == 0 || apuestas[i] == 0) return 0;
        boolean paridad = i >= 4 && i < 8;
        boolean acierto = paridad ? ganador % 2 == elecciones[i] : ganador == elecciones[i];
        return acierto ? apuestas[i] * (paridad ? 2 : 36) : 0;
    }
    public synchronized boolean publicar(int numero) {
        if (!activa) return false;
        ganador = numero;
        long premios = 0;
        for (int i = 0; i < 12; i++) premios += premio(i);
        System.out.println("\nTIRADA " + turno + ": " + ganador);
        if (premios > banca.getSaldo()) {
            System.out.println("Premios=" + premios + " EUR; banca=" + banca.getSaldo());
            cerrar("Banca insuficiente: ronda anulada y apuestas devueltas");
            return false;
        }
        resultadoListo = true;
        notifyAll();
        return true;
    }
    public synchronized boolean comprobar(int i, int esperado) throws InterruptedException {
        while (activa && (turno != esperado || !resultadoListo)) wait();
        if (!activa) return false;
        long premio = premio(i);
        long apuesta = apuestas[i];
        if (!banca.pagar(premio)) throw new IllegalStateException("Pago sin fondos");
        saldos[i] += premio;
        if (i >= 8 && apuesta > 0) siguientes[i] = premio > 0 ? 10 : apuesta * 2;
        System.out.println(nombre(i) + (apuesta == 0 ? " no juega" : premio > 0
                ? " GANA; premio=" + premio : " pierde") + "; saldo=" + saldos[i]
                + " EUR" + (i >= 8 ? "; siguiente=" + siguientes[i] : ""));
        apuestas[i] = 0;
        comprobadas++;
        notifyAll();
        return true;
    }
    public synchronized boolean esperarComprobaciones() throws InterruptedException {
        while (activa && comprobadas < 12) wait();
        if (!activa) return false;
        verificar();
        System.out.println("CIERRE T" + turno + " | banca=" + banca.getSaldo()
                + " EUR | total=" + total() + " EUR | 12 comprobaciones\n");
        if (limite > 0 && turno >= limite) {
            cerrar("Limite de demostracion alcanzado");
        } else {
            turno++;
            recibidas = comprobadas = 0;
            resultadoListo = false;
            notifyAll();
        }
        return activa;
    }
    public synchronized void cerrar(String causa) {
        if (!activa) return;
        for (int i = 0; i < 12; i++) {
            if (!banca.pagar(apuestas[i])) throw new IllegalStateException("Devolucion imposible");
            saldos[i] += apuestas[i];
            apuestas[i] = 0;
        }
        activa = false;
        motivo = causa;
        notifyAll();
    }
    public synchronized long total() {
        long total = banca.getSaldo();
        for (long saldo : saldos) total += saldo;
        return total;
    }
    public synchronized long saldo(int i) { return saldos[i]; }
    public synchronized long fondos() { return banca.getSaldo(); }
    public synchronized String motivo() { return motivo; }
    public synchronized void verificar() {
        if (total() != totalInicial || banca.getSaldo() < 0)
            throw new IllegalStateException("Contabilidad incorrecta");
        for (long saldo : saldos) if (saldo < 0)
            throw new IllegalStateException("Saldo negativo");
    }
    public synchronized void resumen() {
        verificar();
        System.out.println("FIN | " + motivo);
        System.out.println("Banca=" + banca.getSaldo() + " EUR; total conservado=" + total());
    }
}
