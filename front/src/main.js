import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "@/css/reset.css";
import "@/css/global.css";
import "@/config.js";

const app = createApp( App );

app.use( router );

app.mount( "body" );
