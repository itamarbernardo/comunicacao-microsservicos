import Sequelize from "sequelize";

// import {
//   DB_NAME,
//   DB_HOST,
//   DB_USER,
//   DB_PASSWORD,
//   DB_PORT,
// } from "../constants/secrets.js";

const sequelize = new Sequelize('auth-db', 'postgres', '1234', {
  host: 'localhost',
//   port: DB_PORT,
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
    acquire: 180000,
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