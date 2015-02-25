package fr.ippon.tlse;

import static io.undertow.Handlers.resource;
import fr.ippon.tlse.domain.TuBasicDomain;
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

	private static Undertow	server;

	public static void main(final String[] args) throws Exception {
		ResteasyDeployment deployment = new ResteasyDeployment();
		deployment.setApplicationClass(ApplicationConfig.class.getName());

		ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
				.setAsyncSupported(true).setLoadOnStartup(1).addMapping("/*");
		resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", "/rest");

		DeploymentInfo di = new DeploymentInfo().setContextPath("/")
				.addServletContextAttribute(ResteasyDeployment.class.getName(), deployment).addServlet(resteasyServlet)
				.setDeploymentName("ResteasyUndertow").setClassLoader(ClassLoader.getSystemClassLoader());
		// .setResourceManager(new FileResourceManager(new File("src/main/webapp/"), 1024));

		DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(di);
		deploymentManager.deploy();

		PathHandler path = Handlers.path(
				resource(new FileResourceManager(new File("../Jeda/src/main/webapp"), 1024))
				.setDirectoryListingEnabled(true)).addPrefixPath("/rest", deploymentManager.start());

		// Undertow.builder() .addHttpListener(8080, "localhost") .setHandler(resource(new FileResourceManager(new File("/tmp/test"), 100))
		// .setDirectoryListingEnabled(false) .setWelcomeFiles("other.html")) .build(); server.start();

		// server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(deploymentManager.start()).build();
		server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path).build();
		server.start();

		ApplicationUtils.SINGLETON.registerNewBusinessService(TuBasicDomain.class.getSimpleName(),
				new MockTuBasicBuniess());
	}

	//
	// private void watchTargetCLass() throws ServletException, ClassNotFoundException, InstantiationException,
	// IllegalAccessException, IOException {
	//
	// Path toWatch = Paths.get("target/classes");
	// UndertowContainer u = new UndertowContainer();
	// u.watchDirectoryPath(toWatch);
	//
	// }
	//
	// public void watchDirectoryPath(Path path) throws IOException, ClassNotFoundException, InstantiationException,
	// IllegalAccessException, ServletException {
	// // Sanity check - Check if path is a folder
	// try {
	// Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
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
	//
	// lstPath.add(dir);
	//
	// return FileVisitResult.CONTINUE;
	// }
	//
	// });
	// log.info("DTOMAP HASHCODE {}", DtoMapper.class.hashCode());
	// watchAllDir(lstPath);
	//
	// }
	//
	// private void watchAllDir(List<Path> lstPathpath) throws ClassNotFoundException, IOException,
	// InstantiationException, IllegalAccessException, ServletException {
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
	// String packageName = StringUtils.right(pathStr, pathStr.length() - "target/classes".length() - 1);
	// packageName = packageName.replace("/", ".");
	// lstService.put(packageName, service);
	// log.info("Watching package: {}", packageName);
	//
	// // We register the path to the service
	// // We watch for creation events
	// path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
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
	// if (OVERFLOW == kind) {
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
	//
	// // Class<?> newClass = this.getClass().getClassLoader()
	// // .loadClass(watchService.getKey() + "." + className);
	//
	// URL[] urls = { this.getClass().getProtectionDomain().getCodeSource().getLocation(),
	// modClass.getProtectionDomain().getCodeSource().getLocation() };
	// ClassLoader delegateParent = modClass.getClassLoader().getParent();
	// try (URLClassLoader cl = new URLClassLoader(urls, delegateParent)) {
	// Class<?> reloaded = cl.loadClass(modClass.getName());
	// System.out.printf("reloaded my class: Class@%x%n", reloaded.hashCode());
	// System.out.println("Different classes: " + (modClass != reloaded));
	// log.info("DTOMAP HASHCODE {}", DtoMapper.class.hashCode());
	// log.info("DTOMAP HASHCODE {}", reloaded.hashCode());
	//
	// server.stop();
	// DtoMapper.SINGLETON.resetCache();
	// ApplicationUtils.SINGLETON.resetCacheClass();
	//
	// Thread currentThread = Thread.currentThread();
	// currentThread.setContextClassLoader(cl.getSystemClassLoader());
	//
	// @SuppressWarnings("unchecked")
	// Class<UndertowContainer> curr = (Class<UndertowContainer>) cl.loadClass(this
	// .getClass().getName());
	// UndertowContainer u = curr.newInstance();
	// u.runUndertowServer();
	//
	// }
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
