package in.nic.ashwini.eForms.filters;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaptchaFilter extends ZuulFilter {

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0)
			return true;
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("captcha")) {
				if (!cookie.getValue().equals(request.getParameter("captcha"))) {
					log.debug("Invalid captcha");
					setFailedRequest("Invalid captcha. Please try again.", 401);
					break;
				}
			}
		}
//		if(!request.getParameter("captcha").equals(request.getSession().getAttribute("captcha").toString())) {
//			log.debug("Invalid captcha");
//			setFailedRequest("Invalid captcha. Please try again.", 401);
//		}
		return null;
	}

	/**
	 * Reports an error message given a response body and code.
	 * 
	 * @param body
	 * @param code
	 */
	private void setFailedRequest(String body, int code) {
		log.debug("Reporting error ({}): {}", code, body);
		String body1 = "{\"error\":\"" + body + "\"}";

		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setResponseStatusCode(code);
		if (ctx.getResponseBody() == null) {
			ctx.setResponseBody(body1);
			ctx.setSendZuulResponse(false);
		}
	}

}
