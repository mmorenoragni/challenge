# challenge : app creada en base al enunciado propuesto para el chalenge tecnico.

## Descripción de uso:
1. Requerimientos tecnicos:
  - Instalar Redis Cache (https://redis.io/).
  - Instalar RabbitMq (https://www.rabbitmq.com/)
  - Instalar PostgresSql (https://www.postgresql.org/)
2. Crear base de datos 'postgres' y crear la tabla request_information, para la cual se puede usar el siguiente script :
``` create table request_information (id serial constraint request_information_pk primary key, url_information      varchar not null, response_information text); alter table request_information owner to postgres; create unique index request_information_id_uindex on request_information (id);```

3. Correr los servicios de Redis y RabbitMq (verificar dependiendo el sistema operativo de la PC donde se iniciará la aplicación)
4. Para correr la application se debe compilar usando la tarea gradle bootJar ```./gradlew bootJar``` y luego correr el jar moviendose al directiorio build/libs y correr el commando ```java -jar challenge-001-SNAPSHOT.jar```
5. Validar que la aplicación esta Up realizando un get a ```http://localhost:8080/ping``` y esperar que la respuesta sea "pong"


## Documentación de la application:
 Para ver la documentación de la application se debe correr la aplicación e ingresar a ```http://localhost:8080/swagger-ui/index.html```
 
## Ejemplos de apicall permitidos en la api:
1. Para obtener el resultado de la suma de dos numeros más un porcentaje aplicado a dicha suma, se debe realiza el apicall de la siguiente manera:
 - en **cUrl** : ``` curl --location --request GET 'http://localhost:8080/challenge/addition?firstNum=20&secondNum=10'```
 - en **Shell wGet** ```wget --no-check-certificate --quiet \ --method GET \ --timeout=0 \ --header '' \ 'http://localhost:8080/challenge/addition?firstNum=20&secondNum=10'```