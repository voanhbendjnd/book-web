// Example React Components sử dụng Session Handler

import React, { useState, useEffect } from "react";
import {
  useSessionHandler,
  usePublicAPI,
  useProtectedAPI,
  withAuth,
} from "./useSessionHandler";

// ===== COMPONENT 1: Home Page (Public APIs) =====
const HomePage = () => {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const { fetchBooks, fetchCategories } = usePublicAPI();

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);

        // Fetch books và categories - tự động handle session expired
        const [booksResponse, categoriesResponse] = await Promise.all([
          fetchBooks({ page: 1, size: 20 }),
          fetchCategories(),
        ]);

        setBooks(booksResponse.data);
        setCategories(categoriesResponse.data);
      } catch (error) {
        console.error("Error loading data:", error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [fetchBooks, fetchCategories]);

  if (loading) {
    return <div>Loading books and categories...</div>;
  }

  return (
    <div>
      <h1>Home Page</h1>
      <div>
        <h2>Books ({books.length})</h2>
        {books.map((book) => (
          <div key={book.id}>{book.title}</div>
        ))}
      </div>
      <div>
        <h2>Categories ({categories.length})</h2>
        {categories.map((category) => (
          <div key={category.id}>{category.name}</div>
        ))}
      </div>
    </div>
  );
};

// ===== COMPONENT 2: Profile Page (Protected APIs) =====
const ProfilePage = withAuth(() => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const { fetchUserAccount } = useProtectedAPI();

  useEffect(() => {
    const loadUserData = async () => {
      try {
        setLoading(true);
        const response = await fetchUserAccount();
        setUser(response.data.user);
      } catch (error) {
        console.error("Error loading user data:", error);
      } finally {
        setLoading(false);
      }
    };

    loadUserData();
  }, [fetchUserAccount]);

  if (loading) {
    return <div>Loading profile...</div>;
  }

  return (
    <div>
      <h1>Profile Page</h1>
      <div>
        <h2>Welcome, {user?.name}</h2>
        <p>Email: {user?.email}</p>
        <p>Phone: {user?.phone}</p>
        <p>Address: {user?.address}</p>
      </div>
    </div>
  );
});

// ===== COMPONENT 3: Login Page =====
const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const { login, isAuthenticated } = useSessionHandler();

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      window.location.href = "/";
    }
  }, [isAuthenticated]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const result = await login(username, password);

      if (result.success) {
        // Redirect to home page
        const redirectUrl = sessionStorage.getItem("redirectAfterLogin") || "/";
        sessionStorage.removeItem("redirectAfterLogin");
        window.location.href = redirectUrl;
      } else {
        setError(result.error.message || "Login failed");
      }
    } catch (error) {
      setError("Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={handleLogin}>
        <div>
          <input
            type="email"
            placeholder="Email"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <div style={{ color: "red" }}>{error}</div>}
        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
};

// ===== COMPONENT 4: Navigation Bar =====
const NavigationBar = () => {
  const { user, logout, isAuthenticated } = useSessionHandler();

  const handleLogout = async () => {
    await logout();
    window.location.href = "/login";
  };

  return (
    <nav>
      <div>
        <a href="/">Home</a>
        {isAuthenticated && (
          <>
            <a href="/profile">Profile</a>
            <a href="/cart">Cart</a>
            <a href="/orders">Orders</a>
            <span>Welcome, {user?.name}</span>
            <button onClick={handleLogout}>Logout</button>
          </>
        )}
        {!isAuthenticated && <a href="/login">Login</a>}
      </div>
    </nav>
  );
};

// ===== MAIN APP COMPONENT =====
const App = () => {
  const { isLoading } = useSessionHandler();

  if (isLoading) {
    return <div>Loading application...</div>;
  }

  return (
    <div>
      <NavigationBar />
      {/* Router logic here */}
      <HomePage />
    </div>
  );
};

export default App;

// ===== USAGE INSTRUCTIONS =====
/*
1. Wrap your app with SessionHandler:
   <SessionProvider>
     <App />
   </SessionProvider>

2. Use hooks in components:
   - useSessionHandler(): Main hook
   - usePublicAPI(): For books, categories
   - useProtectedAPI(): For user data, cart, orders
   - withAuth(): HOC for protected routes

3. Benefits:
   - Automatic session validation
   - Smart retry for public APIs
   - Auto-redirect on session expired
   - Clean error handling
   - TypeScript support
*/
