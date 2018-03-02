# SCRT
Centralized Traffic Control system specially designed for axle-counting systems in 5" tracks

ES:
El objetivo de este proyecto es crear un sistema de control de tráfico ferroviario, de tal forma que se evite un gran número de accidentes.
Este sistema, partiendo de la posición de los trenes y el estado de los aparatos de vía, permite el acceso a un determinado cantón.
Está diseñado con la intención de mantener la mayor fluidez posible, permitiendo cierto tipo de maniobras que no presentan un gran peligro en trenes de 5 pulgadas.
Además, hay un sistema en desarrollo que permite establecer horarios para los trenes, gestionando los cruces a partir de la previsión de horario,
mostrándose en una malla gráfica.

Las características del funcionamiento del CTC son las siguientes:

La detección de trenes se realiza por contadores de ejes, estableciéndose un contador de entrada y uno de salida en cada tramo de forma dinámica,
según la posición de los desvíos. Se simula el paso de un tren sobre un contador con el botón izquierdo y derecho del ratón sobre un tramo
de vía con contador de ejes asociado.

Cada tramo de vía se ocupa cuando recibe un aviso de ocupación de uno de sus contadores asociados, discriminándose el sentido de ocupación.
Además, el tramo de vía puede estar bloqueado por alguna señal.

Los desvíos son talonables, permitiéndose también la instalación de desvíos con muelle, que no precisan de intervención.
Además, el desvío puede estar enclavado por una señal o por un tren.

Las señales están normalmente cerradas, pero pueden ser abiertas por el usuario haciendo clic sobre la señal. 
Al ordenar la apertura, se comprueba la ruta, se establece el bloqueo, se enclavan los desvíos y se comprueba si se puede abrir la señal. 
Una señal se abre si el tramo que protege está libre de trenes, el bloqueo está establecido y todos sus elementos están enclavados. 
Algunas señales permiten la apertura en Marcha a la Vista si el tren que circula por delante va en el mismo sentido.
Se permite la marcha de maniobras en 'Rebase Autorizado', siempre que se pueda circular con seguridad.
Se define como proximidad de la señal el tramo de vía anterior a la misma.
Las señales pueden funcionar de forma automática en dos grados:
Señal automática: se abre tan pronto como haya un tren en su proximidad. Se selecciona este estado manualmente.
Señal de bloqueo: sólo las señales que se encuentran en plena vía o en estaciones cerradas son de bloqueo. Se abren si hay un tren en la proximidad
o la proximidad está bloqueada por otra señal.
Para cerrar una señal, con el clic derecho sobre la señal y seleccionando 'Cerrar señal', distinguimos dos casos:
Sin tren en la proximidad, se cierra inmediatamente.
Con tren en la proximidad, se cierra en diferido con un tiempo que varía según el número de señales a las que influye el cierre de la señal.

Los itinerarios se establecen pulsando con el botón central del ratón en el punto de inicio y de fin del mismo. Se disuelven cerrando
cada una de las señales que lo componen.

Las estaciones pueden estar abiertas o cerradas. En una estación abierta, es necesario reconocer la llegada de un tren al tramo de vía
anterior a la misma. Se indica en el panel con rojo a destellos.
