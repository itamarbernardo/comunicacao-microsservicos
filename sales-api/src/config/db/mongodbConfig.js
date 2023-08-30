import mongoose from "mongoose";

import { MONGO_DB_URL } from "../constants/secrets.js";

export function connectMongoDb() {
  mongoose.connect(MONGO_DB_URL, {
    useNewUrlParser: true,
    serverSelectionTimeoutMS: 180000, //Espera esse tempo e fica tentando se conectar, se não conseguir nesse tempo, ele lança um erro
  });
  mongoose.connection.on("connected", function () {
    console.info("A aplicação se conectou ao MongoDB com Sucesso!");
  });
  mongoose.connection.on("error", function () {
    console.error("Erro ao se conectar com o MongoDB!");
  });
}