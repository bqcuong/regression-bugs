package com.peterphi.std.guice.web.rest.setup;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.peterphi.std.guice.apploader.GuiceConstants;
import com.peterphi.std.guice.apploader.GuiceProperties;
import com.peterphi.std.guice.apploader.GuiceRole;
import com.peterphi.std.guice.apploader.GuiceSetup;
import com.peterphi.std.guice.common.ClassScanner;
import com.peterphi.std.guice.common.ClassScannerFactory;
import com.peterphi.std.guice.common.serviceprops.composite.GuiceConfig;
import com.peterphi.std.guice.web.rest.auth.interceptor.AuthConstraintInterceptorModule;
import com.peterphi.std.guice.web.rest.auth.userprovider.WebappAuthenticationModule;
import com.peterphi.std.io.PropertyFile;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AutoJAXRSBindingGuiceRole implements GuiceRole
{
	private static final Logger log = Logger.getLogger(AutoJAXRSBindingGuiceRole.class);


	@Override
	public void adjustConfigurations(final List<PropertyFile> configs)
	{

	}


	@Override
	public void register(final Stage stage,
	                     final ClassScannerFactory scannerFactory,
	                     final GuiceConfig config,
	                     final GuiceSetup setup,
	                     final List<Module> modules,
	                     final AtomicReference<Injector> injectorRef,
	                     final MetricRegistry metrics)
	{
		// TODO remove HACK Don't run if we're within a unit test (this is an ugly hack...)
		if (!config.getBoolean(GuiceProperties.UNIT_TEST, false))
		{
			final ClassScanner scanner = scannerFactory.getInstance();

			if (scanner == null)
				throw new IllegalArgumentException("No classpath scanner available, missing scan.packages?");

			// Optionally set up JAX-RS Service and Client bindings
			if (config.getBoolean(GuiceProperties.ROLE_JAXRS_SERVER_AUTO, true))
			{
				modules.add(new JAXRSAutoRegisterServicesModule(config, scannerFactory));
			}

			// Set up authentication and authorisation logic
			{
				// Set up authentication
				{
					// Set up provider for CurrentUser
					List<String> authProviderNames = config.getList(GuiceProperties.AUTH_PROVIDER_NAMES, null);

					// If no providers set, use the default (JWT, or the Servlet's preferred auth scheme)
					if (authProviderNames == null || authProviderNames.size() == 0)
						authProviderNames = Arrays.asList(GuiceConstants.JAXRS_SERVER_WEBAUTH_JWT_PROVIDER,
						                                  GuiceConstants.JAXRS_SERVER_WEBAUTH_SERVLET_PROVIDER);

					modules.add(new WebappAuthenticationModule(metrics, authProviderNames, config));
				}

				// Optionally set up authorisation
				if (config.getBoolean(GuiceProperties.AUTH_ENABLED, true))
					modules.add(new AuthConstraintInterceptorModule(metrics, config));
			}
		}
	}


	@Override
	public void injectorCreated(final Stage stage,
	                            final ClassScannerFactory scanner,
	                            final GuiceConfig config,

	                            final GuiceSetup setup,
	                            final List<Module> modules,
	                            final AtomicReference<Injector> injectorRef,
	                            final MetricRegistry metrics)
	{

	}
}
