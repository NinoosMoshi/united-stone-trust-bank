import { Navigate, useLocation } from "react-router-dom";
import AuthService from "../authService";



export const CustomerRoute = ({ element: Component }) => {
    const location = useLocation();
    return AuthService.isCustomer() ? (Component) : (<Navigate to='/login' replace state={{ from: location }} />)
}

export const AuditorRoute = ({ element: Component }) => {
    const location = useLocation();
    return AuthService.isAdmin() || AuthService.isAuditor() ?
        (Component)
        :
        (<Navigate to='/login' replace state={{ from: location }} />)
}




// 🧭 useLocation()

// useLocation is a React Router hook that gives you information about the current URL the user is trying to visit.

// It returns an object like this:

// {
//   pathname: "/customer/dashboard",
//   search: "?page=2",
//   hash: "",
//   state: null,
//   key: "abc123"
// }


// ✅ Example:
// If the user tries to visit
// http://localhost:3000/customer/dashboard,
// then:

// const location = useLocation();
// console.log(location.pathname);
// Output: "/customer/dashboard"


// So location tells you where the user currently is (or was trying to go).

// 🔁 replace
// replace prevents the user from going “back” to the protected route after being redirected.


// 📦 state={{ from: location }}
// pass data through navigation

// state={{ from: location }}
// That means you’re telling the login page: “Hey, this user came from this specific page (stored in location).”

