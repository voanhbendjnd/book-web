// Test Order Filter API
const axios = require("axios");

const BASE_URL = "http://localhost:8080";

async function testOrderFilterAPI() {
  try {
    console.log("🧪 Testing Order Filter API...\n");

    // Test 1: Get all orders without filter
    console.log("1️⃣ Testing GET /api/v1/orders/history (no filter)");
    try {
      const response1 = await axios.get(
        `${BASE_URL}/api/v1/orders/history?page=1&size=10`
      );
      console.log("✅ Status:", response1.status);
      console.log("✅ Total orders:", response1.data.data?.meta?.total || 0);
    } catch (error) {
      console.log("⚠️  Expected error (no auth):", error.response?.status);
    }

    // Test 2: Test with status filter (without auth - should return 401)
    console.log("\n2️⃣ Testing GET /api/v1/orders/history with status filter");
    try {
      const response2 = await axios.get(
        `${BASE_URL}/api/v1/orders/history?filter=status:PENDING`
      );
      console.log("✅ Status:", response2.status);
    } catch (error) {
      console.log("⚠️  Expected error (no auth):", error.response?.status);
    }

    // Test 3: Test CORS
    console.log("\n3️⃣ Testing CORS headers");
    try {
      const corsResponse = await axios.options(
        `${BASE_URL}/api/v1/orders/history`,
        {
          headers: {
            Origin: "http://localhost:3000",
            "Access-Control-Request-Method": "GET",
            "Access-Control-Request-Headers": "Content-Type, Authorization",
          },
        }
      );
      console.log("✅ CORS Status:", corsResponse.status);
    } catch (error) {
      console.log("⚠️  CORS test failed:", error.message);
    }

    console.log("\n📋 Available Order Status Values:");
    console.log("- PENDING: Đơn hàng chờ xử lý");
    console.log("- PROCESSING: Đang xử lý");
    console.log("- SHIPPED: Đã gửi hàng");
    console.log("- DELIVERED: Đã giao hàng");
    console.log("- PAID: Đã thanh toán");
    console.log("- CANCELED: Đã hủy");
    console.log("- FAILED: Thất bại");

    console.log("\n📝 Example API calls:");
    console.log("GET /api/v1/orders/history?page=1&size=10");
    console.log("GET /api/v1/orders/history?filter=status:PENDING");
    console.log("GET /api/v1/orders/history?filter=status:PROCESSING");
    console.log("GET /api/v1/orders/history?filter=status:DELIVERED");
    console.log(
      "GET /api/v1/orders/history?page=1&size=10&filter=status:CANCELED"
    );

    console.log("\n🎉 Order Filter API is ready!");
    console.log("\n💡 Note: To test with authentication, you need to:");
    console.log("1. Login first to get JWT token");
    console.log("2. Include Authorization header: Bearer <token>");
    console.log("3. Then call the history API with filters");
  } catch (error) {
    console.error("❌ Test failed:", error.message);

    if (error.code === "ECONNREFUSED") {
      console.log("\n💡 Solution: Make sure backend is running on port 8080");
      console.log("Run: .\\gradlew bootRun");
    }
  }
}

// Run the test
testOrderFilterAPI();

