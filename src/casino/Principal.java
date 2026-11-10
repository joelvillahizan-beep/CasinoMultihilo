package casino;

public class Principal {
    public static Mesa ejecutar(long banca, int turnos, int pausa, int[] resultados)
            throws InterruptedException {
        Mesa mesa = new Mesa(banca, turnos, resultados != null);
        Jugador[] jugadores = new Jugador[12];
        Ruleta ruleta = new Ruleta(mesa, pausa, resultados);
        System.out.println("USUARIO: " + System.getProperty("user.name"));
        System.out.println("12 jugadores x 1000 EUR | Banca=" + banca + " EUR | Pausa=" + pausa);
        for (int i = 0; i < jugadores.length; i++) {
            jugadores[i] = new Jugador(mesa, i);
            jugadores[i].start();
        }
        ruleta.start();
        try {
            ruleta.join();
            for (Jugador jugador : jugadores) jugador.join();
        } catch (InterruptedException e) {
            mesa.cerrar("Interrupcion del hilo principal");
            ruleta.interrupt();
            for (Jugador jugador : jugadores) jugador.interrupt();
            throw e;
        }
        mesa.resumen();
        System.out.println("HILOS FINALIZADOS: 12 jugadores + 1 ruleta");
        return mesa;
    }
    public static void main(String[] args) throws InterruptedException {
        int turnos = args.length == 0 ? 0 : Integer.parseInt(args[0]);
        if (turnos < 0) throw new IllegalArgumentException("Turnos no negativos");
        ejecutar(50000, turnos, 3000, null);
    }
}
