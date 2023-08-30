import axios from "axios";

import { PRODUCT_API_URL } from "../../../config/constants/secrets.js";

class ProductClient {
  async checkProducStock(products, token, transactionid) {
    try {
      const headers = {
        Authorization: `Bearer ${token}`,
        transactionid
      };
      console.info(
        `Sending request to Product API with data: ${JSON.stringify(
          products
        )} and transactionID ${transactionid}`
      );
      let response = false;
      await axios
        .post(
          `${PRODUCT_API_URL}/check-stock`, { products } , { headers } )
        .then((res) => {
          console.info(
            `Success response from Product-API. TransactionID ${transactionid}`
          );
          response = true;
        })
        .catch((err) => {
          console.error(
            `Error response from Product-API. TransactionID ${transactionid}`
          );
          response = false;
        });
      return response;
    } catch (err) {
      console.error(
        `Error response from Product-API. TransactionID ${transactionid}`
      );
      return false;
    }
  }
}
export default new ProductClient();