package com.peterphi.std.azure.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.microsoft.azure.Azure;
import com.microsoft.azure.management.compute.VirtualMachines;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import com.peterphi.std.azure.AzureVMControl;
import com.peterphi.std.azure.VMControl;

/**
 * Created by bmcleod on 05/09/2016.
 */
public class AzureModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		bind(ServiceClientCredentials.class).toProvider(ServiceClientCredentialsProvider.class);
		bind(Azure.class).toProvider(AzureProvider.class);
		bind(VMControl.class).to(AzureVMControl.class).in(Singleton.class);
	}


	@Provides
	@Inject
	public VirtualMachines provideVirtualMachinesManagement(Azure azure)
	{
		return azure.virtualMachines();
	}
}
