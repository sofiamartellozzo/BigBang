package it.polimi.tiw.bigbang.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckQuery implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String loginpath = req.getServletContext().getContextPath() + "/login";
		if (req.getRequestURI().equals(req.getServletContext().getContextPath() + "/")) {
			res.sendRedirect(loginpath);
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
}
