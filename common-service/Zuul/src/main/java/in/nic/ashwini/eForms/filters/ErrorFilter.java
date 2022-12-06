package in.nic.ashwini.eForms.filters;

import java.net.SocketTimeoutException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.netflix.client.ClientException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorFilter extends ZuulFilter {
	private static final String FILTER_TYPE = "error";
	private static final int FILTER_ORDER = -1;
	private static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";

	@Value("${error.path:/error}")
	private String errorPath;
	
	@Override
	public String filterType() {
		return FILTER_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext context = RequestContext.getCurrentContext();
		Throwable ex = context.getThrowable();
		return ex instanceof ZuulException && !context.getBoolean(SEND_ERROR_FILTER_RAN, false);
	}
	
	@Override
	public Object run() {
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			ExceptionHolder exception = findZuulException(ctx.getThrowable());
			HttpServletRequest request = ctx.getRequest();

			request.setAttribute("javax.servlet.error.status_code",
					exception.getStatusCode());

			log.warn("Error during filtering", exception.getThrowable());
			request.setAttribute("javax.servlet.error.exception",
					exception.getThrowable());

			if (StringUtils.hasText(exception.getErrorCause())) {
				request.setAttribute("javax.servlet.error.message",
						exception.getErrorCause());
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher(this.errorPath);
			if (dispatcher != null) {
				ctx.set(SEND_ERROR_FILTER_RAN, true);
				if (!ctx.getResponse().isCommitted()) {
					ctx.setResponseStatusCode(exception.getStatusCode());
					dispatcher.forward(request, ctx.getResponse());
				}
			}
		}
		catch (Exception ex) {
			ReflectionUtils.rethrowRuntimeException(ex);
		}
		return null;
	}

	protected ExceptionHolder findZuulException(Throwable throwable) {
		if (throwable.getCause() instanceof ZuulRuntimeException) {
			Throwable cause = null;
			if (throwable.getCause().getCause() != null) {
				cause = throwable.getCause().getCause().getCause();
			}
			if (cause instanceof ClientException && cause.getCause() != null
					&& cause.getCause().getCause() instanceof SocketTimeoutException) {

				ZuulException zuulException = new ZuulException("", 504,
						 "Service seems down or slow in responding!!! Please try again after some time!!!");
				return new ZuulExceptionHolder(zuulException);
			}
			// this was a failure initiated by one of the local filters
			if (throwable.getCause().getCause() instanceof ZuulException) {
				return new ZuulExceptionHolder(
						(ZuulException) throwable.getCause().getCause());
			}
		}

		if (throwable.getCause() instanceof ZuulException) {
			// wrapped zuul exception
			return new ZuulExceptionHolder((ZuulException) throwable.getCause());
		}

		if (throwable instanceof ZuulException) {
			// exception thrown by zuul lifecycle
			return new ZuulExceptionHolder((ZuulException) throwable);
		}

		// fallback
		return new DefaultExceptionHolder(throwable);
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	protected interface ExceptionHolder {

		Throwable getThrowable();

		default int getStatusCode() {
			return HttpStatus.INTERNAL_SERVER_ERROR.value();
		}

		default String getErrorCause() {
			return null;
		}

	}

	protected static class DefaultExceptionHolder implements ExceptionHolder {

		private final Throwable throwable;

		public DefaultExceptionHolder(Throwable throwable) {
			this.throwable = throwable;
		}

		@Override
		public Throwable getThrowable() {
			return this.throwable;
		}

	}

	protected static class ZuulExceptionHolder implements ExceptionHolder {

		private final ZuulException exception;

		public ZuulExceptionHolder(ZuulException exception) {
			this.exception = exception;
		}

		@Override
		public Throwable getThrowable() {
			return this.exception;
		}

		@Override
		public int getStatusCode() {
			return this.exception.nStatusCode;
		}

		@Override
		public String getErrorCause() {
			return this.exception.errorCause;
		}

	}
}
