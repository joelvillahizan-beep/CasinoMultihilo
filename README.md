# Casino multihilo - UD02

Java 17 o superior, sin dependencias. Abrir esta carpeta en IntelliJ.
Ejecutar `Casino Demo (3 turnos)` o `Pruebas Casino`.
Principal sin argumentos mantiene la simulación activa hasta que no hay jugadores con fondos o la banca no puede pagar. El argumento opcional limita los turnos.

## Decisiones
12 jugadores Thread (4 número, 4 paridad, 4 martingala), una Ruleta Thread y una Banca. Mesa es el monitor: synchronized, wait en bucles y notifyAll. No se utilizan Lock, Semaphore ni utilidades concurrentes.
Se descuenta la apuesta al aceptarla; 360/20 EUR son premios brutos para una apuesta de 10. El cero pierde siempre. Las elecciones se mantienen durante la partida. Martingala duplica tras perder y vuelve a 10 tras ganar; si no alcanza el saldo, deja de apostar pero sigue confirmando turnos.
Antes de pagar se comprueba la suma de todos los premios. Si la banca no alcanza, se anula la ronda completa, se devuelven las apuestas y se cierra. Esta política evita favorecer al primer hilo y nunca produce deuda ni pagos parciales.
Cada ronda conserva banca + jugadores = 62000 EUR en la configuración normal.

## Pruebas
PruebasCasino utiliza resultados prefijados y pausa cero solo para verificar los casos límite. En ejecución normal se usa Random y una pausa de 3000 ms antes de cada tirada; la coordinación y las impresiones añaden un pequeño tiempo adicional.
Prueba cero, premios y martingala, insolvencia con banca de 100 EUR y agotamiento de saldos. Todas lanzan los 13 hilos y esperan su terminación.
