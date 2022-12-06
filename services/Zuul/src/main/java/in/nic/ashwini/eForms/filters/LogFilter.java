package in.nic.ashwini.eForms.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class LogFilter extends ZuulFilter {
	
	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return -1;
	}
	
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        //String requestId = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        //RequestContext.getCurrentContext().addZuulRequestHeader("requestId", requestId);
        String clientIp = fetchClientIp(request);
        MDC.put("clientIp", clientIp);
        MDC.put("url",request.getRequestURI());
        //MDC.put("sessionId", request.getHeader("RandomID"));
        //MDC.put("requestId", requestId);
		Map<String, List<String>> newParameterMap = new HashMap<>();
	    Map<String, String[]> parameterMap = request.getParameterMap();
	    //getting the current parameter
	    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
	      String key = entry.getKey();
	      String[] values = entry.getValue();
	      newParameterMap.put(key, Arrays.asList(values));
	    }
	    //add a new parameter
	    newParameterMap.put("clientIp",Arrays.asList(clientIp));
	    RequestContext.getCurrentContext().setRequestQueryParams(newParameterMap);
		return null;
	}

	private String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader(FilterConstants.X_FORWARDED_FOR_HEADER);
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }else {
            	remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
            }
        }
		return remoteAddr;
	}
}
