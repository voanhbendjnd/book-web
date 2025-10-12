// Test script để kiểm tra tính năng single session
// Chạy script này để test: node test-single-session.js

const axios = require("axios");

const BASE_URL = "http://localhost:8080/api/v1";
const TEST_USER = {
  username: "test@example.com", // Thay đổi email này thành email có sẵn trong DB
  password: "password123", // Thay đổi password này thành password đúng
};

// Function để login và lưu token
async function login(username, password) {
  try {
    const response = await axios.post(`${BASE_URL}/auth/login`, {
      username: username,
      password: password,
    });

    console.log("Login thành công:", response.data.user.email);
    console.log(
      "Access Token:",
      response.data.accessToken.substring(0, 50) + "..."
    );

    return {
      accessToken: response.data.accessToken,
      refreshToken: response.headers["set-cookie"]?.find((cookie) =>
        cookie.includes("refresh_token")
      ),
    };
  } catch (error) {
    console.error("Login thất bại:", error.response?.data || error.message);
    return null;
  }
}

// Function để gọi API protected
async function callProtectedAPI(accessToken) {
  try {
    const response = await axios.get(`${BASE_URL}/users/account`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    console.log("API call thành công:", response.data.user.email);
    return true;
  } catch (error) {
    console.error(
      "API call thất bại:",
      error.response?.status,
      error.response?.data || error.message
    );
    return false;
  }
}

// Test single session
async function testSingleSession() {
  console.log("=== TESTING SINGLE SESSION FUNCTIONALITY ===\n");

  // Login lần đầu
  console.log("1. Login lần đầu...");
  const session1 = await login(TEST_USER.username, TEST_USER.password);

  if (!session1) {
    console.log("Không thể login, kết thúc test.");
    return;
  }

  // Test API với session đầu tiên
  console.log("\n2. Test API với session đầu tiên...");
  const api1Success = await callProtectedAPI(session1.accessToken);

  // Đợi 2 giây
  console.log("\n3. Đợi 2 giây...");
  await new Promise((resolve) => setTimeout(resolve, 2000));

  // Login lần thứ 2 (sẽ invalidate session đầu tiên)
  console.log("\n4. Login lần thứ 2 (sẽ invalidate session đầu tiên)...");
  const session2 = await login(TEST_USER.username, TEST_USER.password);

  if (!session2) {
    console.log("Không thể login lần 2, kết thúc test.");
    return;
  }

  // Test API với session thứ 2
  console.log("\n5. Test API với session thứ 2...");
  const api2Success = await callProtectedAPI(session2.accessToken);

  // Test API với session đầu tiên (sẽ bị reject)
  console.log("\n6. Test API với session đầu tiên (sẽ bị reject)...");
  const api1AfterLogin2Success = await callProtectedAPI(session1.accessToken);

  // Kết quả
  console.log("\n=== KẾT QUẢ TEST ===");
  console.log(
    `Session 1 API call ban đầu: ${api1Success ? "THÀNH CÔNG" : "THẤT BẠI"}`
  );
  console.log(`Session 2 API call: ${api2Success ? "THÀNH CÔNG" : "THẤT BẠI"}`);
  console.log(
    `Session 1 API call sau login 2: ${
      api1AfterLogin2Success ? "THÀNH CÔNG (LỖI!)" : "THẤT BẠI (ĐÚNG!)"
    }`
  );

  if (!api1AfterLogin2Success && api2Success) {
    console.log("\n✅ TEST THÀNH CÔNG: Single session hoạt động đúng!");
    console.log("   - Session đầu tiên bị invalidate khi login lần 2");
    console.log("   - Session thứ 2 hoạt động bình thường");
  } else {
    console.log("\n❌ TEST THẤT BẠI: Single session không hoạt động đúng!");
  }
}

// Chạy test
testSingleSession().catch(console.error);
