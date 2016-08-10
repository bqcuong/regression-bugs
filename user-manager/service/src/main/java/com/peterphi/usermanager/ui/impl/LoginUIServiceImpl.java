package com.peterphi.usermanager.ui.impl;

import com.peterphi.usermanager.db.dao.hibernate.UserDaoImpl;
import com.peterphi.usermanager.db.entity.UserEntity;
import com.peterphi.usermanager.guice.authentication.UserAuthenticationService;
import com.peterphi.usermanager.guice.authentication.UserLogin;
import com.peterphi.usermanager.ui.api.LoginUIService;
import com.google.inject.Inject;
import com.peterphi.std.guice.common.auth.annotations.AuthConstraint;
import com.peterphi.std.guice.web.HttpCallContext;
import com.peterphi.std.guice.web.rest.templating.TemplateCall;
import com.peterphi.std.guice.web.rest.templating.Templater;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

public class LoginUIServiceImpl implements LoginUIService
{
	/**
	 * Approximately 1 year in seconds
	 */
	private static final int ONE_YEAR = 8765 * 60 * 60;

	@Inject
	Templater templater;

	@Inject
	UserDaoImpl accountDao;

	@Inject
	UserLogin login;

	@Inject
	UserAuthenticationService authenticationService;


	@Override
	@AuthConstraint(skip = true, comment = "login page")
	public String getLogin(String returnTo, String errorText)
	{
		if (login.isLoggedIn())
		{
			throw new IllegalArgumentException("You are already logged in!");
		}
		else
		{
			TemplateCall call = templater.template("login");

			call.set("returnTo", returnTo);
			call.set("errorText", errorText);

			return call.process();
		}
	}


	@AuthConstraint(skip = true, comment = "login page")
	@Override
	public Response doLogin(String returnTo, String user, String password)
	{
		if (login.isLoggedIn())
		{
			throw new IllegalArgumentException("You are already logged in!");
		}
		else
		{
			final UserEntity account = authenticationService.authenticate(user, password, false);

			if (account != null)
			{
				// Successful login
				login.reload(account);

				final Response.ResponseBuilder builder;

				if (returnTo != null)
					builder = Response.seeOther(URI.create(returnTo));
				else
					builder = Response.seeOther(URI.create("/"));

				// If this account has a Session Reconnect Key we should give it to the browser
				if (account.getSessionReconnectKey() != null)
				{
					NewCookie cookie = new NewCookie(UserLogin.SESSION_RECONNECT_COOKIE,
					                                 account.getSessionReconnectKey(),
					                                 null,
					                                 null,
					                                 null,
					                                 ONE_YEAR,
					                                 false);

					builder.cookie(cookie);
				}

				return builder.build();
			}
			else
			{
				// Send the user back to the login page
				final String page = getLogin(returnTo, "E-mail/password incorrect");

				return Response.status(403).entity(page).build();
			}
		}
	}


	@Override
	public Response doLogout(String returnTo)
	{
		// Change the session reconnect key (if one is used)
		if (login.isLoggedIn())
			accountDao.changeSessionReconnectKey(login.getId());

		// Invalidate the current session
		HttpSession session = HttpCallContext.get().getRequest().getSession(false);

		if (session != null)
			session.invalidate();

		// Clear the login (in case the session isn't correctly invalidated)
		login.clear();

		if (StringUtils.isEmpty(returnTo))
			return Response.seeOther(URI.create("/")).build();
		else
			return Response.seeOther(URI.create(returnTo)).build();
	}
}
