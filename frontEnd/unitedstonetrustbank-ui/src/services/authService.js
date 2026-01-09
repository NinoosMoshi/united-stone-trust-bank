import api from "./api";

const AuthService = {
    saveAuthData: (token, roles) => {
        localStorage.setItem("token", token);
        localStorage.setItem("roles", JSON.stringify(roles));
    },

    logout: () => {
        localStorage.removeItem("token");
        localStorage.removeItem("roles");
    },

    hasRole(role) {
        const roles = localStorage.getItem("roles");  // Get the roles from local storage '["ADMIN","USER"]'
        return roles ? JSON.parse(roles).includes(role) : false;
    },

    isAuthenticated: () => !!localStorage.getItem("token"), //Return true if a token exists, false if it doesn’t

    isAdmin() {
        return this.hasRole("ADMIN");
    },

    isCustomer() {
        return this.hasRole("CUSTOMER");
    },

    isAuditor() {
        return this.hasRole("AUDITOR");
    },

    login: (body) => api.post("/auth/login", body),
    register: (body) => api.post("/auth/register", body),
    forgetPassword: (body) => api.post("/auth/forgot-password", body),
    resetPassword: (body) => api.post("/auth/reset-password", body),
};

export default AuthService;
