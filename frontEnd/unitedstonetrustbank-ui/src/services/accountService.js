import api from "./api";

const AccountService = {
    getMyAccounts: () => api.get("/accounts/me"),
};

export default AccountService;