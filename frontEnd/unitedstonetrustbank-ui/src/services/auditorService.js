import api from "./api";

const AuditorService = {
    getSystemTotals: () => api.get("/audit/totals"),

    findUserByEmail: (email) => api.get(`/audit/users?email=${email}`),

    findAccountByAccountNumber: (accountNumber) =>
        api.get(`/audit/accounts?accountNumber=${accountNumber}`),

    findTransactionsByAccountNumber: (accountNumber) =>
        api.get(`/audit/transactions/by-account?accountNumber=${accountNumber}`),

    findTransactionById: (transactionId) =>
        api.get(`/audit/transactions/by-id?transactionId=${transactionId}`),
};

export default AuditorService;
