const { contextBridge } = require("electron");

contextBridge.exposeInMainWorld("chazz", {
  ping: () => "pong"
});
