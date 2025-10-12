// Debug script để kiểm tra session validation
const axios = require("axios");

const BASE_URL = "http://localhost:8080/api/v1";
const TEST_USER = {
  username: "admin@example.com", // Thay đổi email này
  password: "admin123", // Thay đổi password này
};

// Function để login
async function login(username, password) {
  try {
    const response = await axios.post(`${BASE_URL}/auth/login`, {
      username: username,
      password: password,
    });

    console.log("✅ Login thành công:", response.data.user.email);
    console.log(
      "🔑 Access Token:",
      response.data.accessToken.substring(0, 50) + "..."
    );

    return {
      accessToken: response.data.accessToken,
      user: response.data.user,
    };
  } catch (error) {
    console.error("❌ Login thất bại:", error.response?.data || error.message);
    return null;
  }
}

// Function để gọi API protected
async function callProtectedAPI(accessToken, sessionName) {
  try {
    const response = await axios.get(`${BASE_URL}/users/account`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    console.log(
      `✅ ${sessionName} API call thành công:`,
      response.data.user.email
    );
    return true;
  } catch (error) {
    console.error(
      `❌ ${sessionName} API call thất bại:`,
      error.response?.status,
      error.response?.data || error.message
    );
    return false;
  }
}

// Function để decode JWT token (chỉ để debug)
function decodeJWT(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );

    const decoded = JSON.parse(jsonPayload);
    console.log("🔍 JWT Claims:");
    console.log("  - Email:", decoded.sub);
    console.log("  - SessionId:", decoded.sessionId);
    console.log("  - Expires:", new Date(decoded.exp * 1000));
    return decoded;
  } catch (error) {
    console.error("❌ Không thể decode JWT:", error.message);
    return null;
  }
}

// Test session validation
async function testSessionValidation() {
  console.log("=== DEBUGGING SESSION VALIDATION ===\n");

  // Login lần đầu
  console.log("1. Login lần đầu...");
  const session1 = await login(TEST_USER.username, TEST_USER.password);

  if (!session1) {
    console.log("❌ Không thể login, kết thúc test.");
    return;
  }

  // Decode JWT để xem sessionId
  console.log("\n2. Decode JWT token...");
  const jwt1 = decodeJWT(session1.accessToken);

  // Test API với session đầu tiên
  console.log("\n3. Test API với session đầu tiên...");
  const api1Success = await callProtectedAPI(session1.accessToken, "Session 1");

  // Đợi 2 giây
  console.log("\n4. Đợi 2 giây...");
  await new Promise((resolve) => setTimeout(resolve, 2000));

  // Login lần thứ 2 (sẽ invalidate session đầu tiên)
  console.log("\n5. Login lần thứ 2 (sẽ invalidate session đầu tiên)...");
  const session2 = await login(TEST_USER.username, TEST_USER.password);

  if (!session2) {
    console.log("❌ Không thể login lần 2, kết thúc test.");
    return;
  }

  // Decode JWT mới để xem sessionId mới
  console.log("\n6. Decode JWT token mới...");
  const jwt2 = decodeJWT(session2.accessToken);

  // So sánh sessionId
  if (jwt1 && jwt2) {
    console.log("\n7. So sánh sessionId:");
    console.log(`  - Session 1 ID: ${jwt1.sessionId}`);
    console.log(`  - Session 2 ID: ${jwt2.sessionId}`);
    console.log(
      `  - Khác nhau: ${
        jwt1.sessionId !== jwt2.sessionId ? "✅ CÓ" : "❌ KHÔNG"
      }`
    );
  }

  // Test API với session thứ 2
  console.log("\n8. Test API với session thứ 2...");
  const api2Success = await callProtectedAPI(session2.accessToken, "Session 2");

  // Test API với session đầu tiên (sẽ bị reject)
  console.log("\n9. Test API với session đầu tiên (sẽ bị reject)...");
  const api1AfterLogin2Success = await callProtectedAPI(
    session1.accessToken,
    "Session 1 (sau login 2)"
  );

  // Kết quả
  console.log("\n=== KẾT QUẢ DEBUG ===");
  console.log(
    `Session 1 API call ban đầu: ${
      api1Success ? "✅ THÀNH CÔNG" : "❌ THẤT BẠI"
    }`
  );
  console.log(
    `Session 2 API call: ${api2Success ? "✅ THÀNH CÔNG" : "❌ THẤT BẠI"}`
  );
  console.log(
    `Session 1 API call sau login 2: ${
      api1AfterLogin2Success ? "❌ THÀNH CÔNG (LỖI!)" : "✅ THẤT BẠI (ĐÚNG!)"
    }`
  );

  if (!api1AfterLogin2Success && api2Success) {
    console.log("\n🎉 SUCCESS: Session validation hoạt động đúng!");
  } else {
    console.log("\n🚨 FAILED: Session validation không hoạt động!");
    console.log("🔧 Cần kiểm tra:");
    console.log(
      "  1. CustomJwtAuthenticationConverter có được inject đúng không"
    );
    console.log("  2. SessionManager.isValidSession() có hoạt động đúng không");
    console.log("  3. Database có update sessionId đúng không");
  }
}

// Chạy test
testSessionValidation().catch(console.error);
