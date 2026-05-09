# App Chat tiempo real

## ¿Como ejecutar?

Antes de nada, tanto en el back como en el front, pon bien el `.env`.

En el back, para la IA puedes usar cualquier servicio compatible con la API de OpenAI (incluso IAs locales tipo LMStudio y Ollama).

La DB funciona tanto con MySQL, MariaDB y PostgreSQL y debe ser tipo `jdbc:mariadb://<host>:3306/<nombre_db>` 
La DB tiene que estar ya creada o añadir a la URL `?createDatabaseIfNotExist=true`.

### Backend

En VSCode instala la extension `vmware.vscode-boot-dev-pack`.

Ve a la nueva pestaña de la barra lateral con el icono de Spring Boot.

En la app dale al boton de play.

Si da error, pulsa `F1` en VSCode y pon `Java: Reload Projects`, y luego vuelve a darle.

### Frontend

(Necesario NodeJs: `winget install nodejs` en terminal o [nodejs.org](https://nodejs.org/es/download?utm_source=chatgpt.com) el `.msi`)

En una terminal (puede ser la de VSCode) ve a la carpeta del front.

Instala las dependencias con `npm install`.

Se ejecuta con `npm run build && npm run preview` y abre la URL que te da.

Y ya estaria.
