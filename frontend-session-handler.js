// Frontend Session Handler - Giải pháp chuyên nghiệp cho Single Session

// 1. Axios Interceptor để handle 401 responses
axios.interceptors.response.use(
  (response) => {
    // Nếu response thành công, không làm gì
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Chỉ handle 401 errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // Lưu URL hiện tại để redirect sau khi login
      const currentUrl = window.location.pathname + window.location.search;
      sessionStorage.setItem("redirectAfterLogin", currentUrl);

      // Clear invalid token
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");

      // Redirect về login page
      window.location.href = "/login?reason=session_expired";

      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

// 2. Function để check token validity trước khi gọi API
export const checkTokenValidity = async () => {
  const token = localStorage.getItem("accessToken");
  if (!token) return false;

  try {
    // Gọi API nhẹ để check token
    const response = await axios.get("/api/v1/users/account", {
      headers: { Authorization: `Bearer ${token}` },
    });
    return true; // Token hợp lệ
  } catch (error) {
    if (error.response?.status === 401) {
      // Token không hợp lệ, clear và redirect
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");
      window.location.href = "/login?reason=token_invalid";
      return false;
    }
    throw error; // Re-throw nếu lỗi khác
  }
};

// 3. Smart API caller - tự động handle session
export const smartApiCall = async (apiCall, options = {}) => {
  const { requireAuth = false, retryOn401 = true } = options;

  try {
    return await apiCall();
  } catch (error) {
    // Nếu API không cần auth và bị 401, thử lại không có token
    if (!requireAuth && error.response?.status === 401 && retryOn401) {
      console.log("🔄 Retrying API call without token...");

      // Remove Authorization header và thử lại
      const retryApiCall = async () => {
        const config = { ...error.config };
        delete config.headers.Authorization;
        return axios(config);
      };

      return await retryApiCall();
    }

    throw error;
  }
};

// 4. Usage examples:

// API cần authentication
const fetchUserAccount = () =>
  smartApiCall(() => axios.get("/api/v1/users/account"), { requireAuth: true });

// API public (có thể gửi token hoặc không)
const fetchBooks = () =>
  smartApiCall(() => axios.get("/api/v1/books"), {
    requireAuth: false,
    retryOn401: true,
  });

const fetchCategories = () =>
  smartApiCall(() => axios.get("/api/v1/categories"), {
    requireAuth: false,
    retryOn401: true,
  });

// 5. Component wrapper để auto-handle session
export const withSessionHandler = (Component) => {
  return (props) => {
    const [isSessionValid, setIsSessionValid] = useState(null);

    useEffect(() => {
      const checkSession = async () => {
        const isValid = await checkTokenValidity();
        setIsSessionValid(isValid);
      };

      checkSession();
    }, []);

    if (isSessionValid === null) {
      return <div>Loading...</div>; // Hoặc skeleton
    }

    if (isSessionValid === false) {
      return <div>Redirecting to login...</div>;
    }

    return <Component {...props} />;
  };
};
