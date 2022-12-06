package in.nic.ashwini.eForms.filters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import in.nic.ashwini.eForms.services.EncryptionService;
import in.nic.ashwini.eForms.services.UtilityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegNumberValidationFilter extends ZuulFilter {

	@Autowired
	private UtilityService utilityService;

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 2;
	}

	@Override
	public boolean shouldFilter() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		return request.getParameter("regNumber") != null;
	}

	@Override

	public Object run() throws ZuulException {

		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

		String requestUri = request.getRequestURI();

		Map<String, List<String>> map = RequestContext.getCurrentContext().getRequestQueryParams();

		List<String> emails = map.get("email");

		String email = emails.get(0);

		String registrationNo = request.getParameter("regNumber");

		Pattern pattern = Pattern.compile("[a-zA-Z]+-[a-zA-Z]+[0-9]+");

		Matcher matcher = pattern.matcher(registrationNo);

		if (!matcher.matches()) {

			log.debug("Invalid format of entered registration number!!!");

			setFailedRequest("Invalid format of entered registration number!!!", 401);

			return null;

		}

		if (request.getHeader("AllowedForms") != null) {

			String allowedFormsInString = EncryptionService.decrypt(request.getHeader("AllowedForms"));

			Set<String> allowedForms = Arrays.stream(allowedFormsInString.split(":"))

					.collect(Collectors.toSet());

			String serviceName = utilityService.fetchServiceName(registrationNo);

			if (!allowedForms.contains(serviceName)) {

				log.debug("Not authorized for service : {}", serviceName);

				setFailedRequest("Not authorized for service " + serviceName, 401);

				return null;

			}

			if (requestUri.contains("approve") || requestUri.contains("reject") || requestUri.contains("revert")
					|| requestUri.contains("updateRequest") || requestUri.contains("forward")
					|| requestUri.contains("pull")) {

				if (requestUri.contains("support")) {

					if (!utilityService.isRequestPendingWithSupport(registrationNo)) {

						log.debug("This request {} is not pending wih you : {}", registrationNo, email);

						setFailedRequest("This request (" + registrationNo + ") is not pending wih you", 401);

						return null;

					}

				}

				if (requestUri.contains("admin")) {

					if (!utilityService.isRequestPendingWithAdmin(registrationNo)) {

						log.debug("This request {} is not pending wih you : {}", registrationNo, email);

						setFailedRequest("This request (" + registrationNo + ") is not pending wih you", 401);

						return null;

					}
				}
			}
		} else {
			if (requestUri.contains("approve") || requestUri.contains("reject")) {
				if (!utilityService.isRequestPendingWithLoggedInUser(registrationNo, email)) {
					log.debug("This request {} is not pending wih you : {}", registrationNo, email);
					setFailedRequest("This request (" + registrationNo + ") is not pending wih you", 401);
					return null;
				}
			} else if (!utilityService.isLoggedInUserStakeHolder(registrationNo, email)) {
				log.debug("Not authorized for registration number : {}", registrationNo);
				setFailedRequest("Not authorized for registration number " + registrationNo, 401);
				return null;
			} else if(requestUri.contains("updateRequest")) {
				if(utilityService.isRequestEsigned(registrationNo, email)) {
					log.debug("This request {} was eSigned, hence it is not editable.", registrationNo);
					setFailedRequest("This request (" + registrationNo + ") was eSigned, hence it is not editable.", 401);
					return null;
				}
				
//				if (!utilityService.isRequestPendingWithLoggedInUser(registrationNo, email)) {
//					log.debug("This request {} is not pending wih you : {}", registrationNo, email);
//					setFailedRequest("This request (" + registrationNo + ") is not pending wih you", 401);
//					return null;
//				}
				
				String role = request.getParameter("role");
				
				if (!utilityService.isRequestPendingWithLoggedInUserOrNextLevelAuthority(registrationNo, email, role)) {
					log.debug("This request {} is not pending wih you {} or next level authority", registrationNo, email);
					setFailedRequest("This request (" + registrationNo + ") is not pending wih you or next level authority. Hence, not editable.", 401);
					return null;
				}
			}
		}

		// else if(!utilityService.isRegNumberMatchesWithApiCall(registrationNo)) {

		// log.debug("There is a mismatch in registration number and API call");

		// setFailedRequest("Please check your URL. It seems incorrect for the
		// registration number "+registrationNo, 401);

		// return null;

		// }

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
