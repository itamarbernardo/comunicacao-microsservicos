import { Router } from "express";

import UserController from "../controller/UserController.js";
import checkToken from "../../../config/auth/checkToken.js";

const router = new Router();

router.post("/api/user/auth", UserController.getAccessToken);

//Passamos um middleware pra Router
router.use(checkToken); //Daqui pra baixo, vamos precisar de autenticacao para acessar as rotas abaixo:
router.get("/api/user/email/:email", UserController.findByEmail);

export default router;