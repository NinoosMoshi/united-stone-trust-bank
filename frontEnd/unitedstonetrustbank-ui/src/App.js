import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Register from './pages/Register';
import Login from './pages/Login';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Profile from './pages/Profile';
import UpdateProfile from './pages/UpdateProfile';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import Transactions from './pages/Transactions';
import Transfer from './pages/Transfer';
import AuditorDashboard from './pages/AuditorDashboard';
import { AuditorRoute, CustomerRoute } from './services/guard/Guard';


function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/" element={<Home />} />

        <Route path="/profile" element={<CustomerRoute element={<Profile />} />} />
        <Route path="/update-profile" element={<CustomerRoute element={<UpdateProfile />} />} />

        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        <Route path="/transactions" element={<CustomerRoute element={<Transactions />} />} />
        <Route path="/transfer" element={<CustomerRoute element={<Transfer />} />} />

        <Route path="/auditor-dashboard" element={<AuditorRoute element={<AuditorDashboard />} />} />

        <Route path="*" element={<NotFound />} />
      </Routes>

      <Footer />
    </BrowserRouter>
  );
}

export default App;
