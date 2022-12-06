package in.nic.ashwini.ldap.logging;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements Filter {
	
	@Value("${spring.application.name}")
	private String appName;
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		MDC.put("clientIp", fetchClientIp(request));
		MDC.put("url",request.getRequestURI());
		MDC.put("appName",appName);
		filterChain.doFilter(request, response);
		MDC.clear();
	}
	
	private String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }else {
            	remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
            }
        }
		return remoteAddr;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
            throw new ServletException(request + " not HttpServletRequest");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException(request + " not HttpServletResponse");
        }
        doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
}
