package in.nic.ashwini.eForms.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemarksValidationFilter extends ZuulFilter {
	
	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 3;
	}

	@Override
	public boolean shouldFilter() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		return request.getParameter("remarks") != null;
	}

	@Override
	public Object run() throws ZuulException {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		String remarks = request.getParameter("remarks");
		Pattern pattern = Pattern.compile("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
		Matcher matcher = pattern.matcher(remarks);

		if (!matcher.matches()) {
			log.debug("Invalid format of entered remarks");
			setFailedRequest("Invalid format of entered remarks!!!", 401);
		}
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
		String body1="{\"error\":\""+body+"\"}";  

		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setResponseStatusCode(code);
		if (ctx.getResponseBody() == null) {
			ctx.setResponseBody(body1);
			ctx.setSendZuulResponse(false);
		}
	}
}
