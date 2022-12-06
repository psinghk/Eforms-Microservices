package in.nic.ashwini.eForms.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.db.master.repositories.EmailCoordinatorsRepository;
import in.nic.ashwini.eForms.db.master.repositories.StateRepository;
import in.nic.ashwini.eForms.db.slave.repositories.EmailCoordinatorsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.StateRepositoryToRead;
import in.nic.ashwini.eForms.services.ValidationService;
import lombok.extern.slf4j.Slf4j;
import models.ErrorResponseForOrganizationValidationDto;
import models.OrganizationDto;

@Slf4j
@Component
@Order(1)
public class OrganizationValidationFilter extends OncePerRequestFilter {

	@Autowired
	private EmailCoordinatorsRepository organizationRespository;

	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private EmailCoordinatorsRepositoryToRead organizationRespositoryToRead;

	@Autowired
	private StateRepositoryToRead stateRepositoryToRead;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!isPost(request) && !isPut(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
		String body = cachedRequest.getBody();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OrganizationDto org = mapper.readValue(body, OrganizationDto.class);
		boolean isError = false;
		ErrorResponseForOrganizationValidationDto error = new ErrorResponseForOrganizationValidationDto();

		if (org.getEmployment() == null) {
			log.warn("Employment is null");
			isError = true;
			error.setEmploymentCategoryError("Employment can not be NULL");
		} else if (org.getEmployment().isEmpty()) {
			log.warn("Employment is empty");
			isError = true;
			error.setEmploymentCategoryError("Employment can not be empty");
		}
		else if (!ValidationService.isFormatValid("employment", org.getEmployment())) {
			log.debug("Invalid Employment category format for employment : {}", org.getEmployment());
			isError = true;
			error.setEmploymentCategoryError(
					"Please enter employment in correct format, Alphanumeric(,.) allowed  [limit 1-50]");
		} else {
			List<String> categories = organizationRespositoryToRead.findDinstinctCategories();
			if (categories.size() == 0) {
				log.debug("{} is a new category which does not exist in our database", org.getEmployment());
				isError = true;
				error.setEmploymentCategoryError("Invalid employment category!!!");
			} else {
				categories.replaceAll(String::toUpperCase);
				if (!categories.contains(org.getEmployment().toUpperCase())) {
					log.debug("{} is a new category which does not exist in our database", org.getEmployment());
					isError = true;
					error.setEmploymentCategoryError("Invalid employment category!!!");
				}
			}
		}
		
		if (isError) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			mapper.writeValue(response.getWriter(), error);
			return;
		}
		
		if (org.getEmployment().equalsIgnoreCase("central")) {
			if (org.getMinistry() == null) {
				log.warn("Ministry is null");
				error.setMinistryError("Ministry can not be NULL!!!");
				isError = true;
			} else if (org.getMinistry().isEmpty()) {
				log.warn("Ministry is empty");
				error.setMinistryError("Ministry can not be empty!!!");
				isError = true;
			} else if (!ValidationService.isFormatValid("ministry", org.getMinistry())) {
				log.warn("Ministry name has an invalid format");
				error.setMinistryError("Please enter ministry in correct format");
				isError = true;
			} else {
				List<String> ministries = organizationRespositoryToRead.findMinistriesByEmpCategory(org.getEmployment());
				if (ministries.size() == 0) {
					log.warn("Invalid Ministry : {} , not present in eForms", org.getMinistry());
					error.setMinistryError("Please enter correct ministry");
					isError = true;
				} else {
					ministries.replaceAll(String::toUpperCase);
					if (!ministries.contains(org.getMinistry().toUpperCase())) {
						log.warn("Invalid Ministry : {} , not present in eForms", org.getMinistry());
						error.setMinistryError("Please enter correct ministry");
						isError = true;
					}
				}
			}

			if (org.getDepartment() == null) {
				log.warn("Department is null");
				error.setDepartmentError("Department can not be NULL!!!");
				isError = true;
			} else if (org.getDepartment().isEmpty()) {
				log.warn("Department is empty");
				error.setDepartmentError("Department can not be empty!!!");
				isError = true;
			} else if (!ValidationService.isFormatValid("department", org.getDepartment())) {
				log.warn("Department name has an invalid format");
				error.setDepartmentError("Please enter department in correct format");
				isError = true;
			}
		}
		
		if (isError) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			mapper.writeValue(response.getWriter(), error);
			return;
		}

		if (org.getEmployment().equalsIgnoreCase("state")) {
			org.setState(org.getMinistry());
			if (org.getState() == null) {
				log.warn("State is null");
				error.setStateError("State can not be NULL!!!");
				isError = true;
			} else if (org.getState().isEmpty()) {
				log.warn("State is empty");
				error.setStateError("State can not be empty!!!");
				isError = true;
			} else if (!ValidationService.isFormatValid("state", org.getState())) {
				log.warn("State name has an invalid format");
				error.setStateError("Please enter state name in correct format");
				isError = true;
			} else {
				List<String> states = stateRepositoryToRead.getDistinctStateName();
				if (states.size() == 0) {
					log.warn("State name has an invalid format");
					error.setStateError("Please enter state name in correct format");
					isError = true;
				} else {
					states.replaceAll(String::toUpperCase);
					if (!states.contains(org.getState().toUpperCase())) {
						log.warn("State name has an invalid format");
						error.setStateError("Please enter state name in correct format");
						isError = true;
					}
				}
			}

			if (org.getDepartment() == null) {
				log.warn("Department is null");
				error.setDepartmentError("Department can not be NULL!!!");
				isError = true;
			} else if (org.getDepartment().isEmpty()) {
				log.warn("Department is empty");
				error.setDepartmentError("Department can not be empty!!!");
				isError = true;
			} else if (!ValidationService.isFormatValid("department", org.getDepartment())) {
				log.warn("Department name has an invalid format");
				error.setDepartmentError("Please enter department in correct format");
				isError = true;
			}
		}
		
		if (isError) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			mapper.writeValue(response.getWriter(), error);
			return;
		}

		if (org.getEmployment().equalsIgnoreCase("psu") || org.getEmployment().equalsIgnoreCase("nkn")
				|| org.getEmployment().equalsIgnoreCase("others")) {
			if (org.getOrganization() == null) {
				log.warn("Organization is null");
				error.setOrganizationError("Organization can not be NULL!!!");
				isError = true;
			} else if (org.getOrganization().isEmpty()) {
				log.warn("Organization is empty");
				error.setOrganizationError("Organization can not be empty!!!");
				isError = true;
			} else if (!ValidationService.isFormatValid("organization", org.getOrganization())) {
				log.warn("Organization name has an invalid format");
				error.setOrganizationError("Please enter organization in correct format");
				isError = true;
			}
		}

		if (isError) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			mapper.writeValue(response.getWriter(), error);
		} else {
			filterChain.doFilter(cachedRequest, response);
		}
	}

	private boolean isPost(HttpServletRequest request) {
		return request.getMethod().equalsIgnoreCase(HttpMethod.POST.toString());
	}

	private boolean isPut(HttpServletRequest request) {
		return request.getMethod().equalsIgnoreCase(HttpMethod.PUT.toString());
	}
}
