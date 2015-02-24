package com.peterphi.std.guice.restclient.resteasy.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.peterphi.std.annotation.ServiceName;
import com.peterphi.std.guice.apploader.GuiceConstants;
import com.peterphi.std.guice.restclient.JAXRSProxyClientFactory;
import com.peterphi.std.guice.restclient.annotations.FastFailServiceClient;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

@Singleton
public class ResteasyProxyClientFactoryImpl implements JAXRSProxyClientFactory
{
	@Inject
	ResteasyClientFactoryImpl clientFactory;

	@Inject
	Configuration config;


	public static String getConfiguredBoundServiceName(final Configuration config, Class<?> iface, String... names)
	{
		if (names == null || names.length == 0)
		{
			if (iface == null)
				throw new IllegalArgumentException("If not specifying service names you must provide a service interface");
			else
				names = getServiceNames(iface);
		}

		for (String name : names)
		{
			if (name == null)
				continue;

			if (config.containsKey("service." + name + ".endpoint"))
				return name;
		}

		return null;
	}


	@Override
	public ResteasyWebTarget getWebTarget(final String... names)
	{
		return getWebTarget(false, names);
	}


	private ResteasyWebTarget getWebTarget(final boolean defaultFastFail, final String... names)
	{
		final String name = getConfiguredBoundServiceName(config, null, names);

		if (name == null)
			throw new IllegalArgumentException("Cannot find service in configuration by any of these names: " +
			                                   Arrays.asList(names));

		final String endpoint = config.getString("service." + name + ".endpoint", null);
		final URI uri = URI.create(endpoint);

		// TODO allow other per-service configuration?
		final String username = config.getString("service." + name + ".username", getUsername(uri));
		final String password = config.getString("service." + name + ".password", getPassword(uri));
		final boolean fastFail = config.getBoolean("service." + name + ".fast-fail", defaultFastFail);
		final String authType = config.getString("service." + name + ".auth-type", GuiceConstants.JAXRS_CLIENT_AUTH_DEFAULT);

		final boolean preemptiveAuth;
		if (authType.equalsIgnoreCase(GuiceConstants.JAXRS_CLIENT_AUTH_DEFAULT))
			preemptiveAuth = false;
		else if (authType.equalsIgnoreCase(GuiceConstants.JAXRS_CLIENT_AUTH_PREEMPT))
			preemptiveAuth = true;
		else
			throw new IllegalArgumentException("Illegal auth-type for service " + name + ": " + authType);

		return createWebTarget(uri, fastFail, username, password, preemptiveAuth);
	}


	@Override
	public <T> T getClient(final Class<T> iface, final String... names)
	{
		final boolean fastFail = iface.isAnnotationPresent(FastFailServiceClient.class);

		return getWebTarget(fastFail, names).proxy(iface);
	}


	@Override
	public <T> T getClient(final Class<T> iface)
	{
		return getClient(iface, getServiceNames(iface));
	}


	/**
	 * Computes the default set of names for a service based on an interface class. The names produced are an ordered list:
	 * <ul>
	 * <li>The fully qualified class name</li>
	 * <li>If present, the {@link com.peterphi.std.annotation.ServiceName} annotation on the class (OR if not specified on the
	 * class, the {@link com.peterphi.std.annotation.ServiceName} specified on the package)</li>
	 * <li>The simple name of the class (the class name without the package prefix)</li>
	 * </ul>
	 *
	 * @param iface
	 * 		a JAX-RS service interface
	 *
	 * @return An array containing one or more names that could be used for the class; may contain nulls (which should be ignored)
	 */
	private static String[] getServiceNames(Class<?> iface)
	{
		Objects.requireNonNull(iface, "Missing param: iface!");

		return new String[]{iface.getName(), getServiceName(iface), iface.getSimpleName()};
	}


	private static String getServiceName(Class<?> iface)
	{
		Objects.requireNonNull(iface, "Missing param: iface!");

		if (iface.isAnnotationPresent(ServiceName.class))
		{
			return iface.getAnnotation(ServiceName.class).value();
		}
		else if (iface.getPackage().isAnnotationPresent(ServiceName.class))
		{
			return iface.getPackage().getAnnotation(ServiceName.class).value();
		}
		else
		{
			return null; // No special name
		}
	}


	@Override
	public ResteasyWebTarget createWebTarget(final URI endpoint, String username, String password)
	{
		return createWebTarget(endpoint, username, password, true);
	}


	public ResteasyWebTarget createWebTarget(final URI endpoint, String username, String password, boolean preemptiveAuth)
	{
		return createWebTarget(endpoint, false, username, password, preemptiveAuth);
	}


	ResteasyWebTarget createWebTarget(final URI endpoint,
	                                  boolean fastFail,
	                                  String username,
	                                  String password,
	                                  boolean preemptiveAuth)
	{
		if (username != null || password != null || StringUtils.isNotEmpty(endpoint.getAuthority()))
		{
			final AuthScope scope = new AuthScope(endpoint.getHost(), AuthScope.ANY_PORT);

			final Credentials credentials;
			if (username != null || password != null)
				credentials = new UsernamePasswordCredentials(username, password);
			else
				credentials = new UsernamePasswordCredentials(endpoint.getAuthority());

			return clientFactory.getOrCreateClient(fastFail, scope, credentials, preemptiveAuth, null).target(endpoint);
		}
		else
			return clientFactory.getOrCreateClient(fastFail, null, null, false, null).target(endpoint);
	}


	@Override
	public <T> T createClient(final Class<T> iface, final String endpoint)
	{
		return createClient(iface, URI.create(endpoint));
	}


	@Override
	public <T> T createClient(Class<T> iface, URI endpoint)
	{
		return createClient(iface, endpoint, false);
	}


	@Override
	public <T> T createClient(final Class<T> iface, final URI endpoint, final boolean preemptiveAuth)
	{
		return createClientWithPasswordAuth(iface, endpoint, getUsername(endpoint), getPassword(endpoint), preemptiveAuth);
	}


	@Override
	@Deprecated
	public <T> T createClientWithPasswordAuth(Class<T> iface, URI endpoint, String username, String password)
	{
		return createClientWithPasswordAuth(iface, endpoint, username, password, false);
	}


	@Override
	public <T> T createClientWithPasswordAuth(final Class<T> iface,
	                                          final URI endpoint,
	                                          final String username,
	                                          final String password,
	                                          final boolean preemptiveAuth)
	{
		final boolean fastFail = iface.isAnnotationPresent(FastFailServiceClient.class);

		return createWebTarget(endpoint, fastFail, username, password, preemptiveAuth).proxy(iface);
	}


	private static String getUsername(URI endpoint)
	{
		final String info = endpoint.getUserInfo();

		if (StringUtils.isEmpty(info))
			return null;
		else if (info.indexOf(':') != -1)
			return info.split(":", 2)[0];
		else
			return null;
	}


	private static String getPassword(URI endpoint)
	{
		final String info = endpoint.getUserInfo();

		if (StringUtils.isEmpty(info))
			return null;
		else if (info.indexOf(':') != -1)
			return info.split(":", 2)[1];
		else
			return null;
	}
}
