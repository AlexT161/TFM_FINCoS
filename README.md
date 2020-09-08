# TFM_FINCoS++
A framework for performance analysis of complex event processing engines
FINCoS++ es una adaptación de FINCoS, su versión original se puede encontrar en https://code.google.com/archive/p/fincos/
Las principales diferencias entre FINCoS y FINCoS++ son las siguientes:
- Se ha actualizado y extendido el adaptador con motores CEP y se ha integrado un motor para procesamiento de eventos nuevo.
- Nuevas funciones automatizadas para la generación de esquemas y patrones.
- La posibilidad de generar pruebas de rendimiento de distintos motores CEP en una o diferentes máquinas simultáneamente.
- El acceso a la visualización de las estadísticas recogidas dentro de la misma aplicación.

Los directorios del repositorio actual están divididos de la siguiente manera:
- bin: Binarios necesarios para la ejecución de aplicaciones FINCoS++.
- config: Archivos de configuración de pruebas y de configuración general.
- data: Archivos de datos de ejemplo.
- lib: librerías requeridas por FINCoS++.
- log: Archivos de registro producidos por sources y sinks.
- queries: Archivos de ejemplo con patrones específicos en el lenguaje específico de cada motor CEP.
- src: Archivos con el código fuente de FINCoS++.
- etc:Librerías adicionales para la ejecución de los motores de prueba.
- licence: Licencias de FINCoS++, FINCoS y Siddhi.
