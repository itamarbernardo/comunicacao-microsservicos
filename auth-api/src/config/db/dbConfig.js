import Sequelize from "sequelize";

import {
  DB_NAME,
  DB_HOST,
  DB_USER,
  DB_PASSWORD,
  DB_PORT,
} from "../constants/secrets.js";

const sequelize = new Sequelize(DB_NAME, DB_USER, DB_PASSWORD, {
  host: DB_HOST,
  port: DB_PORT,
  dialect: "postgres",
  quoteIdentifiers: false,
  define: {
    syncOnAssociation: true,
    timestamps: false,
    underscored: true,
    underscoredAll: true,
    freezeTableName: true, //Congela o nome da tabela, não add o "s" no final de cada model
  },
  pool: {
    acquire: 180000, //tempo que ele espera e fica tentando se conectar. Passou desse tempo e não conseguiu, ele dá erro
  },
});

sequelize
  .authenticate()
  .then(() => {
    console.info("Connection has been stablished!");
  })
  .catch((err) => {
    console.error("Unable to connect to the database.");
    console.error(err.message);
  });

export default sequelize;