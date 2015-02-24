package com.peterphi.std.guice.web.rest.auth;

import com.peterphi.std.guice.common.auth.iface.AccessRefuser;
import com.peterphi.std.guice.common.auth.iface.CurrentUser;
import com.peterphi.std.guice.restclient.exception.RestException;
import com.peterphi.std.guice.web.HttpCallContext;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * An implementation of {@link com.peterphi.std.guice.common.auth.iface.CurrentUser} using the user attached to the context's
 * {@link javax.servlet.http.HttpServletRequest}
 */
class HttpCallUser implements CurrentUser
{
	@Override
	public boolean isAnonymous()
	{
		HttpServletRequest request = HttpCallContext.get().getRequest();

		return request.getUserPrincipal() == null;
	}


	@Override
	public String getName()
	{
		HttpServletRequest request = HttpCallContext.get().getRequest();

		Principal principal = request.getUserPrincipal();

		if (principal != null)
			return principal.getName();
		else
			return null;
	}


	@Override
	public String getUsername()
	{
		return getName();
	}


	@Override
	public boolean hasRole(final String role)
	{
		HttpServletRequest request = HttpCallContext.get().getRequest();

		return request.isUserInRole(role);
	}


	@Override
	public AccessRefuser getAccessRefuser()
	{
		return (constraint, user) -> {
			if (user.isAnonymous())
				return new RestException(401, "You must log in to access this resource");
			else
				return new RestException(403, "Access denied by rule: " + constraint.comment());
		};
	}
}
