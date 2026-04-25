// Creado ayudandome de "https://websocket.org/guides/languages/javascript/"

import { WebSocket } from "vite";

export default class ClienteWebSocket {
  constructor ( url, opciones = {}) {
    this.url = url;
    this.opciones = {
      tiempoEntreReconexion: 1250,
      maxIntentosReconexion: 10,
      frecuenciaEntreLatidos: 30000,
      ...opciones,
    };
    this.intentosReconexion = 0;
    this.colaMensajes = [];
    this.manejadoresDeEventos = {};
    this.estaConectado = false;

    this.conectar();
  }

  conectar () {
    console.log( `Conectandome a ${this.url}` );

    try {
      this.ws = new WebSocket;
    } catch ( error ) {

    }
  }
}