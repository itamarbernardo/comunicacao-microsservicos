import { v4 as uuidv4 } from "uuid";
import { BAD_REQUEST } from "./constants/httpStatus.js";

//Vai ser obrigatorio todas as requisicoes terem um transactionId
export default (req, res, next) => {
  let { transactionid } = req.headers;
  if (!transactionid) {
    return res.status(BAD_REQUEST).json({
      status: BAD_REQUEST,
      message: "The transactionid header is required.",
    });
  }
  req.headers.serviceid = uuidv4(); //Gera um Id aleatório para o serviço - esse Id vai circular somente nessa requisição
  return next(); //passa pra frente a requisição -> Isso é um middleware (fica no meio), então tem que ter o next
};