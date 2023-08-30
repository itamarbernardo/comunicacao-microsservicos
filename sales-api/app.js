import express from 'express'

import { connectMongoDb } from './src/config/db/mongodbConfig.js'
import { createInitialData } from './src/config/db/initialData.js'
import checkToken from './src/config/auth/checkToken.js'
import { connectRabbitMq } from "./src/config/rabbitmq/rabbitConfig.js";
import { sendMessageToProductStockUpdateQueue } from './src/modules/product/rabbitmq/productStockUpdateSender.js';
import orderRoutes from './src/modules/sales/routes/OrderRoutes.js'
import tracing from './src/config/tracing.js';

const app = express()
const env = process.env
const PORT = env.PORT || 8082
const CONTAINER_ENV = "container";
const THREE_MINUTES = 180000;

startApplication();

async function startApplication() {
  if (CONTAINER_ENV === env.NODE_ENV) {
    console.info("Esperando os containers do RabbitMQ e do MongoDB iniciarem...");
    setInterval(() => {
      connectMongoDb();
      connectRabbitMq();
    }, THREE_MINUTES);
  } else {
    connectMongoDb();
    createInitialData();
    connectRabbitMq();
  }
}

app.use(express.json())

app.get("/api/initial-data", async (req, res) => {
    await createInitialData();
    return res.json({ message: "Data created." });
  });

  app.get('/', (req, res) => { //Endpoint pra checar o status na raiz da aplicação
    return res.status(200).json(getOkResponse())
})
  app.get('/api/status', (req, res) => {
    return res.status(200).json(getOkResponse())
})

function getOkResponse(){
  return {
    service: "Sales-API",
    status: "up",
    httpStatus: 200
} //Retorna um JSON de resposta
}

app.use(tracing) //A partir daqui, toda requisicao tem que ter um transactionId  
app.use(checkToken) //A partir daqui, qualquer requisicao vai precisar conter o token Authorization com um token de acesso válido
app.use(orderRoutes)



app.listen(PORT, () => {
    console.info(`Server Start Sucessfull at port ${PORT}`)
})
