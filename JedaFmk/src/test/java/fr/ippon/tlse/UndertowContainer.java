package fr.ippon.tlse;

import static io.undertow.Handlers.resource;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

import java.io.File;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class UndertowContainer {

	private Undertow	server;

	public static void main(final String[] args) throws Exception {

		UndertowContainer u = new UndertowContainer();

		u.runUndertow(Thread.currentThread().getContextClassLoader());
	}

	private synchronized void runUndertow(ClassLoader cls) throws Exception {
		if (server != null) {
			server.stop();
		}
		ResteasyDeployment deployment = new ResteasyDeployment();
		deployment.setApplicationClass(ApplicationConfigTest.class.getName());

		ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
				.setAsyncSupported(true).setLoadOnStartup(1).addMapping("/*");
		resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", "/rest");

		DeploymentInfo di = new DeploymentInfo().setContextPath("/")
				.addServletContextAttribute(ResteasyDeployment.class.getName(), deployment).addServlet(resteasyServlet)
				.setDeploymentName("ResteasyUndertow").setClassLoader(cls);

		DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(di);
		deploymentManager.deploy();

		PathHandler path = Handlers.path(
				resource(new FileResourceManager(new File("../Jeda/src/main/webapp"), 1024))
						.setDirectoryListingEnabled(true)).addPrefixPath("/rest", deploymentManager.start());

		server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path).build();
		server.start();

	}
}
