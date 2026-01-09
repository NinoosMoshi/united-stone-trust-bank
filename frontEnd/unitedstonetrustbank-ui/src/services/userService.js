import api from "./api";

const UserService = {
    getMyProfile: () => api.get("/users/me"),

    updatePassword: (oldPassword, newPassword) =>
        api.put("/users/update-password", { oldPassword, newPassword }),

    uploadProfilePicture: (file) => {
        const formData = new FormData();
        formData.append("file", file);
        return api.put("/users/profile-picture", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
    },
};

export default UserService;