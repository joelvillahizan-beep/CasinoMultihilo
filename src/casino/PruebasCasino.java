package casino;

public class PruebasCasino {
    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println("PRUEBAS CONTROLADAS: resultados fijados, pausa 0 ms");
        Mesa cero = Principal.ejecutar(50000, 1, 0, new int[]{0});
        exigir(cero.fondos() == 50120, "El cero debe perder todas las apuestas");
        for (int i = 0; i < 12; i++) exigir(cero.saldo(i) == 990, "Saldo tras cero");
        System.out.println("OK 1: cero pierde; banca 50120; jugadores 990");
        Mesa martingala = Principal.ejecutar(50000, 3, 0, new int[]{0, 17, 2});
        exigir(martingala.saldo(8) == 1680, "Martingala 10,20,10");
        exigir(martingala.saldo(0) == 1330, "Premio de numero");
        exigir(martingala.saldo(4) == 990, "Premio de paridad");
        System.out.println("OK 2: premios 36x y 2x; martingala duplica y reinicia");
        Mesa insolvencia = Principal.ejecutar(100, 1, 0, new int[]{17});
        exigir(insolvencia.motivo().startsWith("Banca insuficiente"), "Cierre por banca");
        exigir(insolvencia.fondos() == 100, "Devolucion completa");
        for (int i = 0; i < 12; i++) exigir(insolvencia.saldo(i) == 1000, "Reembolso");
        System.out.println("OK 3: banca insuficiente; sin pagos parciales; reembolso");
        Mesa retirados = Principal.ejecutar(50000, 110, 0, new int[]{0});
        exigir(retirados.motivo().equals("Ningun jugador puede apostar"), "Cierre sin jugadores");
        exigir(retirados.saldo(8) == 370, "No apuesta 640 sin saldo suficiente");
        System.out.println("OK 4: sin saldo, sin negativos y sin bloqueo");
        System.out.println("RESUMEN: cero OK | premios y martingala OK");
        System.out.println("RESUMEN: insolvencia OK | agotamiento de saldo OK");
        System.out.println("USUARIO: " + System.getProperty("user.name"));
        System.out.println("RESULTADO: 4/4 pruebas OK");
    }
}
