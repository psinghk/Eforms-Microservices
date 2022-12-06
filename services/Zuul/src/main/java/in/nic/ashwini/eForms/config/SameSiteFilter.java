package in.nic.ashwini.eForms.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class SameSiteFilter extends GenericFilterBean {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) response;
		String cookie = resp.getHeader("Set-Cookie");
//		if (cookie != null) {
//			resp.setHeader("Set-Cookie", cookie + "; HttpOnly; SameSite=None; Secure");
//		}
		resp.setHeader("Set-Cookie", "HttpOnly; SameSite=strict; Secure");
		if(cookie != null && !cookie.equalsIgnoreCase("XSRF-TOKEN=; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure")) {
			resp.setHeader("Set-Cookie","XSRF-TOKEN=; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure");
		}
		chain.doFilter(request, response);
	}
}
