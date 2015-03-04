package fr.ippon.tlse;

import static io.undertow.Handlers.resource;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.persistence.MongoPersistenceManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

@Slf4j
public class UndertowContainer {

	public static class DynamicClassLoader extends ClassLoader {
		public DynamicClassLoader(ClassLoader parent) {
			super(parent);
		}

		private Set<String>	setClassReloadable	= new HashSet<>();

		@Override
		public Class loadClass(String name) throws ClassNotFoundException {
			if (!setClassReloadable.contains(name)) {
				if (name.contains("fr.ippon.tlse")) {
					setClassReloadable.add(name);
				}
				return super.loadClass(name);
			}

			log.debug("reload class: {}", name);
			Class<?> modClass = Class.forName(name);
			Class reloaded = modClass;
			try {
				URL[] urls = { this.getClass().getProtectionDomain().getCodeSource().getLocation(),
						modClass.getProtectionDomain().getCodeSource().getLocation() };
				ClassLoader delegateParent = modClass.getClassLoader().getParent();
				try (URLClassLoader cl = new URLClassLoader(urls, delegateParent)) {
					reloaded = cl.loadClass(modClass.getName());
					log.debug("reloaded my class: Class@{}", reloaded.hashCode());
					log.debug("Different classes: {}" + (modClass != reloaded));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return reloaded;
		}
	}

	private Undertow	server;

	// private DynamicClassLoader dCl = null;

	public static void main(final String[] args) throws Exception {
		DynamicClassLoader cls = new UndertowContainer.DynamicClassLoader(Thread.currentThread()
				.getContextClassLoader());
		Thread.currentThread().setContextClassLoader(cls);
		UndertowContainer u = new UndertowContainer();
		// u.dCl = new UndertowContainer.DynamicClassLoader(ClassLoader.getSystemClassLoader());
		// ApplicationUtils.SINGLETON.setClassP(ClassPath.from(u.dCl));
		// ApplicationUtils.SINGLETON.setCls(u.dCl);

		u.runUndertow(cls);
		// u.watchTargetCLass();
	}

	private synchronized void runUndertow(ClassLoader cls) throws Exception {
		if (server != null) {
			server.stop();
		}
		ResteasyDeployment deployment = new ResteasyDeployment();
		deployment.setApplicationClass(ApplicationConfig.class.getName());

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

		ApplicationUtils.SINGLETON.registerNewBusinessService(TuBasicDomain.class, new MockTuBasicBuniess());

		DB db = new Fongo("Test").getDB("Database");
		MongoPersistenceManager.setDatabase(db);
		MongoPersistenceManager.setDatabaseName("Database");
	}
	//
	// private void watchTargetCLass() throws Exception {
	// Path toWatch = Paths.get("target/test-classes");
	// watchDirectoryPath(toWatch);
	//
	// }
	//
	// public void watchDirectoryPath(Path path) throws Exception {
	// // Sanity check - Check if path is a folder
	// try {
	// Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", LinkOption.NOFOLLOW_LINKS);
	// if (!isFolder) {
	// throw new IllegalArgumentException("Path: " + path + " is not a folder");
	// }
	// } catch (IOException ioe) {
	// // Folder does not exists
	// ioe.printStackTrace();
	// }
	//
	// log.info("Watching path: {}", path);
	// List<Path> lstPath = new ArrayList<Path>();
	// // We obtain the file system of the Path
	// Files.walkFileTree(path, new SimpleFileVisitor<Path>()
	// {
	// @Override
	// public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	// return FileVisitResult.CONTINUE;
	// }
	//
	// @Override
	// public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	// log.info("Watching path: {}", dir);
	// lstPath.add(dir);
	// return FileVisitResult.CONTINUE;
	// }
	//
	// });
	// log.info("DTOMAP HASHCODE {}", DtoMapper.class.hashCode());
	// watchAllDir(lstPath);
	//
	// }
	//
	// private void watchAllDir(List<Path> lstPathpath) throws Exception {
	//
	// Map<String, WatchService> lstService = new HashMap<>(lstPathpath.size());
	// for (Path path : lstPathpath) {
	// // We create the new WatchService using the new try() block
	//
	// try {
	//
	// WatchService service = path.getFileSystem().newWatchService();
	// log.info("Watching service: {}", path);
	//
	// String pathStr = path.toString();
	// String packageName = StringUtils.right(pathStr, pathStr.length() - "target/test-classes".length() - 1);
	// packageName = packageName.replace("/", ".");
	// lstService.put(packageName, service);
	// log.info("Watching package: {}", packageName);
	//
	// // We register the path to the service
	// // We watch for creation events
	// path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
	// StandardWatchEventKinds.ENTRY_DELETE);
	//
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	//
	// }
	// // Start the infinite polling loop
	// WatchKey key = null;
	// try {
	// while (true) {
	// for (Entry<String, WatchService> watchService : lstService.entrySet()) {
	// key = watchService.getValue().poll(100, TimeUnit.MILLISECONDS);
	// if (key != null) {
	// // Dequeueing events
	// Kind<?> kind = null;
	// for (WatchEvent<?> watchEvent : key.pollEvents()) {
	// // Get the type of the event
	// kind = watchEvent.kind();
	// if (StandardWatchEventKinds.OVERFLOW == kind) {
	// continue; // loop
	// } else {
	// // A new Path was created
	// Path newPath = ((WatchEvent<Path>) watchEvent).context();
	// if (!newPath.toString().contains("$") && newPath.toString().endsWith(".class")) {
	// String className = StringUtils.left(newPath.toString(),
	// newPath.toString().indexOf(".class"));
	// // Output
	// log.info("Path was {}: {}", kind.name(), watchService.getKey() + "." + className);
	// Class<?> modClass = Class.forName(watchService.getKey() + "." + className);
	// dCl.reloadClass(modClass);
	// //
	// // // Class<?> newClass = this.getClass().getClassLoader()
	// // // .loadClass(watchService.getKey() + "." + className);
	// //
	// // URL[] urls = { this.getClass().getProtectionDomain().getCodeSource().getLocation(),
	// // modClass.getProtectionDomain().getCodeSource().getLocation() };
	// // ClassLoader delegateParent = modClass.getClassLoader().getParent();
	// // try (URLClassLoader cl = new URLClassLoader(urls, delegateParent)) {
	// // Class<?> reloaded = cl.loadClass(modClass.getName());
	// // System.out.printf("reloaded my class: Class@%x%n", reloaded.hashCode());
	// // System.out.println("Different classes: " + (modClass != reloaded));
	// // log.info("DTOMAP HASHCODE {}", DtoMapper.class.hashCode());
	// // log.info("DTOMAP HASHCODE {}", reloaded.hashCode());
	// //
	// // DtoMapper.SINGLETON.resetCache();
	// // ApplicationUtils.SINGLETON.resetCacheClass();
	// //
	// runUndertow(dCl);
	// // }
	//
	// }
	// }
	// }
	//
	// if (!key.reset()) {
	// break; // loop
	// }
	// }
	// }
	// }
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// }
}
