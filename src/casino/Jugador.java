package casino;

public class Jugador extends Thread {
    private final Mesa mesa;
    private final int id;
    public Jugador(Mesa mesa, int id) {
        super(mesa.nombre(id));
        this.mesa = mesa;
        this.id = id;
    }
    @Override public void run() {
        try {
            int turno = 1;
            while (mesa.apostar(id, turno)) {
                if (!mesa.comprobar(id, turno)) break;
                turno++;
            }
        } catch (InterruptedException e) {
            mesa.cerrar("Interrupcion de " + getName());
            interrupt();
        }
    }
}
