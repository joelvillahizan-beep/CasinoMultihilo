package casino;

import java.util.Random;

public class Ruleta extends Thread {
    private final Mesa mesa;
    private final int pausa;
    private final int[] resultados;
    private final Random azar = new Random();
    public Ruleta(Mesa mesa, int pausa, int[] resultados) {
        super("Ruleta compartida");
        this.mesa = mesa;
        this.pausa = pausa;
        this.resultados = resultados;
    }
    @Override public void run() {
        try {
            int turno = 0;
            while (mesa.esperarApuestas()) {
                Thread.sleep(pausa);
                int numero = resultados == null ? azar.nextInt(37)
                        : resultados[turno % resultados.length];
                if (!mesa.publicar(numero)) break;
                if (!mesa.esperarComprobaciones()) break;
                turno++;
            }
        } catch (InterruptedException e) {
            mesa.cerrar("Interrupcion de ruleta");
            interrupt();
        }
    }
}
