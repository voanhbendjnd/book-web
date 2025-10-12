// React Hook để handle Single Session - Giải pháp chuyên nghiệp nhất

import { useState, useEffect, useCallback } from "react";
import axios from "axios";

/**
 * Custom Hook để handle session management
 *
 * Features:
 * - Auto-detect session expired
 * - Smart API calling (retry without token for public APIs)
 * - Auto-redirect khi session invalid
 * - Loading states
 */
export const useSessionHandler = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(null); // null = loading, true/false = known state
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // Check session validity
  const checkSession = useCallback(async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      setIsAuthenticated(false);
      setUser(null);
      setIsLoading(false);
      return false;
    }

    try {
      const response = await axios.get("/api/v1/users/account", {
        headers: { Authorization: `Bearer ${token}` },
      });

      setIsAuthenticated(true);
      setUser(response.data.user);
      setIsLoading(false);
      return true;
    } catch (error) {
      if (error.response?.status === 401) {
        // Session expired
        clearSession();
        setIsAuthenticated(false);
        setUser(null);
        setIsLoading(false);
        return false;
      }
      throw error;
    }
  }, []);

  // Clear session data
  const clearSession = useCallback(() => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");
    delete axios.defaults.headers.common["Authorization"];
  }, []);

  // Login function
  const login = useCallback(async (username, password) => {
    try {
      const response = await axios.post("/api/v1/auth/login", {
        username,
        password,
      });

      const { user, accessToken } = response.data;

      // Save to localStorage
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("user", JSON.stringify(user));

      // Set axios default header
      axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

      setIsAuthenticated(true);
      setUser(user);

      return { success: true, user, accessToken };
    } catch (error) {
      return { success: false, error: error.response?.data || error.message };
    }
  }, []);

  // Logout function
  const logout = useCallback(async () => {
    try {
      await axios.post("/api/v1/auth/logout");
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      clearSession();
      setIsAuthenticated(false);
      setUser(null);
    }
  }, [clearSession]);

  // Smart API caller
  const apiCall = useCallback(
    async (apiFunction, options = {}) => {
      const { requireAuth = false, retryOn401 = true } = options;

      try {
        return await apiFunction();
      } catch (error) {
        // Handle 401 for public APIs
        if (!requireAuth && error.response?.status === 401 && retryOn401) {
          console.log("🔄 Retrying API call without token...");

          // Remove Authorization header and retry
          const retryFunction = async () => {
            const config = { ...error.config };
            delete config.headers.Authorization;
            return axios(config);
          };

          try {
            return await retryFunction();
          } catch (retryError) {
            // If still fails, it's a real error
            throw retryError;
          }
        }

        // For protected APIs or other errors
        if (error.response?.status === 401) {
          clearSession();
          setIsAuthenticated(false);
          setUser(null);

          // Redirect to login if it's a protected API
          if (requireAuth) {
            window.location.href = "/login?reason=session_expired";
          }
        }

        throw error;
      }
    },
    [clearSession]
  );

  // Initialize on mount
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    const savedUser = localStorage.getItem("user");

    if (token) {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      if (savedUser) {
        setUser(JSON.parse(savedUser));
      }
      checkSession();
    } else {
      setIsAuthenticated(false);
      setIsLoading(false);
    }
  }, [checkSession]);

  return {
    isAuthenticated,
    user,
    isLoading,
    login,
    logout,
    checkSession,
    apiCall,
    clearSession,
  };
};

/**
 * Higher-Order Component để protect routes
 */
export const withAuth = (Component) => {
  return (props) => {
    const { isAuthenticated, isLoading } = useSessionHandler();

    if (isLoading) {
      return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
      window.location.href = "/login";
      return <div>Redirecting to login...</div>;
    }

    return <Component {...props} />;
  };
};

/**
 * Hook cho public APIs (books, categories)
 */
export const usePublicAPI = () => {
  const { apiCall } = useSessionHandler();

  const fetchBooks = useCallback(
    (params = {}) => {
      return apiCall(() => axios.get("/api/v1/books", { params }), {
        requireAuth: false,
        retryOn401: true,
      });
    },
    [apiCall]
  );

  const fetchCategories = useCallback(() => {
    return apiCall(() => axios.get("/api/v1/categories"), {
      requireAuth: false,
      retryOn401: true,
    });
  }, [apiCall]);

  return {
    fetchBooks,
    fetchCategories,
  };
};

/**
 * Hook cho protected APIs (account, cart, orders)
 */
export const useProtectedAPI = () => {
  const { apiCall } = useSessionHandler();

  const fetchUserAccount = useCallback(() => {
    return apiCall(() => axios.get("/api/v1/users/account"), {
      requireAuth: true,
    });
  }, [apiCall]);

  const fetchCart = useCallback(() => {
    return apiCall(() => axios.get("/api/v1/carts"), {
      requireAuth: true,
    });
  }, [apiCall]);

  const fetchOrders = useCallback(() => {
    return apiCall(() => axios.get("/api/v1/orders"), {
      requireAuth: true,
    });
  }, [apiCall]);

  return {
    fetchUserAccount,
    fetchCart,
    fetchOrders,
  };
};
