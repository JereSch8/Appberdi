### Features

- Georeferenciacion, detecta cuando un usuario entra a un sitio de interés;
- Mediateca;
- Reproducir contenido multimedia;
- Seleccionar un sitio y ver información sobre el mismo;
- Obtener listado de sitios y visualizarlos en el mapa;

# AppBerdi

![](https://instagram.fcor11-1.fna.fbcdn.net/v/t51.2885-19/s320x320/163925808_1912100895637818_3727461534330314760_n.jpg?tp=1&_nc_ht=instagram.fcor11-1.fna.fbcdn.net&_nc_ohc=WdiT-05rrmsAX_eC6fT&edm=ABfd0MgAAAAA&ccb=7-4&oh=6105047f2e4fb810cf9c627046c3286e&oe=608A372E&_nc_sid=7bff83)


## Arquitectura

Para la arquitectura utilizamos el patron de presentacion MVVM, ya que  para la magnitud del proyecto va a ser suficiente, separamos el `pacakge domain` del resto para abstraer la logica de negocio del framework de android tratando de no agregarle complejidad innecesaria.

##Base de datos [Firebase](https://firebase.google.com/ "Firebase")
Se utiliza **Firebase** el *SDK de Google* para la gestion de datos de la aplicacion, se eligio esta herramienta por la gran versatilidad que nos proporciona a la hora de de desarrollar.
Para almacenar los datos utilizamos una Base de datos NOSQL en tiempo real que nos provee Firebase, ésta se llama  [Firestore](https://firebase.google.com/docs/firestore?hl=es) ya tenemos posibilidad de trabajar con la data de manera online y offline, es decir, podemos saber en que estado nos encontramos y trabajar de distinta manera segun corresponda, tambien tenemos la posibilidad de habilitar o deshabilitar las consultas a la nube (esta podría ser una forma de forzar a que trabaje con el caché y que no consuma innecesariamente datos). 

### Motivo de uso
Firestore trabaja con documentos en lugar de JSON lo que nos pareció mas facil de modelar y a su vez nos permite realizar algunas querys. Mientras que la otra alternativa de Firebase, [Realtime](https://firebase.google.com/docs/database?hl=es) trabaja con JSON y tiene limitaciones en cuanto a realizar querys. 


### Manejo de datos

La idea es que los usuarios se descarguen el paquete de 'información base'  para que puedan utilizar la aplicación la primera vez que ingresan (ya que estimamos que se realizara cuando se descarga la aplicación y tendrán conexión a internet), luego esta información queda cacheada y las próximas veces se van a consultar los datos offline (los que está en caché) con posibilidad de actualizarlos cuando haya nueva información ó información actualizada en la DB.
En el caso de las atracciones, que va hacer algo que se actualiza con mas frecuencia probablemente, hagamos posible configurar un tamaño limite de datos descargados y lo mismo para los datos que se puedan almacenar en caché, así puede controlar los datos utilizados por la app y esto está vinculado con la segunda parte del manejo de ficheros que son los contenidos multimedia, en la 'información base' solo vamos a descargar las referencias a tales contenidos una vez que tengan que ser mostrados se descargarían (la configuración previa de cache va a determinar si quedan almacenados esos contenidos para posteriores visitas o no).
De esta manera consideramos que va ser la más adecuada de trabajar con el almacenamiento y las descargas, ademas de esta manera le damos bastante flexibilidad al usuario para que administre el uso de datos como lo crea conveniente, aunque de cualquier manera va a venir preconfigurado con un limite generoso como para que entre todo lo que sea necesario.

### Diagrama
                    
```seq
Dispositivo->Repositorio: Solicita información 
Repositorio-->Dispositivo: Si existe, retorna caché
Note right of Dispositivo: Si no existe la \n información de manera local 
Repositorio->Nube: Solicita información 
Nube->Repositorio: Retorna información
Repositorio-->Dispositivo: Retorna información solicitada
```

# Redes Sociales

#### [Instagram](https://www.instagram.com/appberdi)
