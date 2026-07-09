import api from "../../service/api.js";

export const TABLE_HEADER = ['DATE', 'AMOUNT', 'REMITTANCE','METHOD', 'CATEGORY'];

export const fetchTransactions =async (page=0,size=10)  => {
  return await api.get('transactions/recent', {
    params: {page, size}
  });
}