import api from "./api";

const TransactionService = {
    makeTransfer: (transferData) => api.post("/transactions", transferData),

    getTransactions: (accountNumber, page = 0, size = 10) =>
        api.get(`/transactions/${accountNumber}?page=${page}&size=${size}`),
};

export default TransactionService;
