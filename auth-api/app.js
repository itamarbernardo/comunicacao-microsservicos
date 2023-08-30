import express from 'express'

import { createInitialData } from './src/config/db/initialData.js'

import UserRoutes from './src/modules/user/routes/UserRoutes.js'
import checkToken from './src/config/auth/checkToken.js'
import tracing from './src/config/tracing.js'

const app = express()
const env = process.env
const PORT = env.PORT || 8080
const CONTAINER_ENV = "container"

app.use(express.json())

//Endpoint pra checar o status na raiz da aplicação
app.get('/', (req, res) => {
    return res.status(200).json(getOkResponse())
})

app.get('/api/status', (req, res) => {
    return res.status(200).json(getOkResponse())
})

function getOkResponse(){
    return {
      service: "Auth-API",
      status: "up",
      httpStatus: 200
  } //Retorna um JSON de resposta
}

startApplication()
function startApplication(){
    if (env.NODE_ENV !== CONTAINER_ENV){ //Só cria os dados iniciais se não estivermos rodando em Containers porque as aplicacoes estavam subindo antes dos bancos de dados
        createInitialData() 
    }
}

app.get('/api/initial-data', (req, res) => {
    createInitialData()
    return res.status(200).json({
        message: 'Initial Data created.'
    })
})

app.use(tracing) //a partir daqui, todas as requisicoes vao precisar ter um transactionId
app.use(UserRoutes)
app.use(checkToken) //Toda requisicao vai ser verificado o token

app.listen(PORT, () => {
    console.info(`Server Start Sucessfull at port ${PORT}`)
})
