// package djnd.ben1607.drink_shop.config;

// import java.io.IOException;

// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.http.HttpServletRequest;

// public class DelayFilter implements Filter {
// @Override
// public void doFilter(ServletRequest request, ServletResponse response,
// FilterChain chain)
// throws IOException, ServletException {

// HttpServletRequest httpRequest = (HttpServletRequest) request;
// String delayHeader = httpRequest.getHeader("delay");

// if (delayHeader != null) {
// try {
// long delay = Long.parseLong(delayHeader);
// Thread.sleep(delay);
// } catch (InterruptedException e) {
// Thread.currentThread().interrupt();
// } catch (NumberFormatException e) {
// // bỏ qua nếu không hợp lệ
// }
// }

// chain.doFilter(request, response);
// }
// }
