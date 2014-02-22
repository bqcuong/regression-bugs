package com.peterphi.std.indexservice.rest.client.register;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.peterphi.std.guice.serviceregistry.ApplicationContextNameRegistry;
import com.peterphi.std.guice.serviceregistry.index.IndexableServiceRegistry;
import com.peterphi.std.guice.serviceregistry.index.ManualIndexableService;
import com.peterphi.std.indexservice.rest.client.IndexServiceClient;
import com.peterphi.std.indexservice.rest.type.RegistrationHeartbeatResponse;
import com.peterphi.std.indexservice.rest.type.RegistrationRequest;
import com.peterphi.std.indexservice.rest.type.RegistrationResponse;
import com.peterphi.std.indexservice.rest.type.ServiceDetails;
import com.peterphi.std.threading.Timeout;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Singleton
public class IndexRegistrationHelper
{
	private static final Logger log = Logger.getLogger(IndexRegistrationHelper.class);

	/**
	 * The highest frequency we can be asked to heartbeat at
	 */
	private static final long MIN_HEARTBEAT_INTERVAL = 10000;

	private IndexServiceRegistrationState state = IndexServiceRegistrationState.UNREGISTERED;

	@Inject
	protected IndexServiceClient indexService;

	@Inject
	@Named("local.restservices.endpoint")
	protected URI baseEndpoint;

	/**
	 * Time to wait after registration/reregistration before sending heartbeat
	 */
	protected Timeout firstHeartbeatTimeout = new Timeout(5, TimeUnit.SECONDS);
	protected Timeout defaultHeartbeatTimeout = new Timeout(30, TimeUnit.SECONDS);


	private String applicationId;
	private int registeredRevision = Integer.MIN_VALUE;


	/**
	 * Called periodically to allow index registration/reregistration/heartbeat
	 *
	 * @return
	 */
	public Timeout pulse()
	{
		switch (state)
		{
			case UNREGISTERED:
				return register();
			case REGISTERED:
				return heartbeat();
			case REREGISTRATION_REQUIRED:
				return reregister();
			default:
				throw new RuntimeException("Unknown registration state: " + state);
		}
	}

	/**
	 * Initial registration
	 *
	 * @return
	 */
	private Timeout register()
	{
		// Clear any previous application id
		this.applicationId = null;

		final int revision = IndexableServiceRegistry.getRevision();

		// Register
		final RegistrationResponse response = indexService.register(buildRegistrationRequest());
		final String applicationId = response.applicationId;

		log.debug("Index Service assigned us application id " + applicationId);

		this.applicationId = applicationId;
		this.registeredRevision = revision;
		this.state = IndexServiceRegistrationState.REGISTERED;

		return firstHeartbeatTimeout;
	}

	private Timeout reregister()
	{
		final int revision = IndexableServiceRegistry.getRevision();

		// Re-register
		final RegistrationResponse response = indexService.reregister(this.applicationId, buildRegistrationRequest());
		final String applicationId = response.applicationId; // Allow the remote service to change our application id

		log.debug("Index Service re-register assigned us application id " + applicationId);

		this.applicationId = applicationId;
		this.registeredRevision = revision;
		this.state = IndexServiceRegistrationState.REGISTERED;

		return firstHeartbeatTimeout;
	}

	private Timeout heartbeat()
	{
		// Take the necessary action if we need to re-register our services
		if (registeredRevision != IndexableServiceRegistry.getRevision())
		{
			state = IndexServiceRegistrationState.REREGISTRATION_REQUIRED;

			return reregister();
		}
		else
		{
			// Heartbeat
			final RegistrationHeartbeatResponse response = indexService.heartbeat(applicationId);

			if (response.mustReregister)
			{
				// The service requires that we re-register
				state = IndexServiceRegistrationState.REREGISTRATION_REQUIRED;

				return reregister();
			}
			else
			{
				// Heartbeat was successful. Calculate the time to the next heartbeat
				final long remaining = response.nextHeartbeatExpectedBy.getTime() - System.currentTimeMillis();

				if (remaining >= MIN_HEARTBEAT_INTERVAL)
				{
					return new Timeout(remaining);
				}
				else
				{
					return defaultHeartbeatTimeout;
				}
			}
		}
	}

	/**
	 * Called to request that we attempt to unregister the application
	 */
	public synchronized void unregister()
	{
		switch (state)
		{
			case UNREGISTERED:
				return; // no action required
			default:
				if (this.applicationId != null)
				{
					// Try to unregister (without retry)
					indexService.unregister(this.applicationId);
					this.applicationId = null;
				}

				state = IndexServiceRegistrationState.UNREGISTERED;
		}
	}

	/**
	 * Build a registration request for all currently registered REST services
	 *
	 * @return
	 */
	protected RegistrationRequest buildRegistrationRequest()
	{
		RegistrationRequest request = new RegistrationRequest();

		// Pass along the application context name if present
		// If missing this will default to null (which is ok, application name is optional)
		request.applicationName = ApplicationContextNameRegistry.getContextName();

		// Add Local Services
		for (Class<?> resource : IndexableServiceRegistry.getLocalServices())
		{
			ServiceDetails service = new ServiceDetails();
			service.iface = resource.getName();
			service.endpoint = baseEndpoint.toString();

			// TODO populate properties for this service

			request.services.add(service);
		}

		// Add Remote Services
		for (ManualIndexableService resource : IndexableServiceRegistry.getRemoteServices())
		{
			ServiceDetails service = new ServiceDetails();

			service.iface = resource.serviceInterface;
			service.endpoint = resource.endpoint;

			// TODO populate properties for this service?

			request.services.add(service);
		}

		return request;
	}
}
