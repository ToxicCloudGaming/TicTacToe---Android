package org.toxiccloudgaming.tictactoe.server;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.toxiccloudgaming.tictactoe.server.game.GameManager;

public class ServerContextListener implements ServletContextListener {
	
	private ServerLog logger;
	
    public ServerContextListener() {
    	this.logger = new ServerLog();
    }
    
    public void contextInitialized(ServletContextEvent event) {
    	final GameManager manager = new GameManager(logger);
		event.getServletContext().setAttribute("GameManager", manager);
		event.getServletContext().setAttribute("ServerLog", this.logger);
		
		this.logger.log("Tic-Tac-Toe Server started up!", ServerLog.LOG_INFO);
    }

    public void contextDestroyed(ServletContextEvent event) {
    	this.logger.log("Tic-Tac-Toe Server shutting down...", ServerLog.LOG_INFO);
    	
    	this.unregisterDrivers();
    	this.cleanup();
    	
    	this.logger.close();
    	
    	event.getServletContext().removeAttribute("GameManager");
    	event.getServletContext().removeAttribute("ServerLog");
    }
    
    public void unregisterDrivers() {
    	Enumeration<Driver> drivers = DriverManager.getDrivers();
    	while(drivers.hasMoreElements()) {
    		Driver driver = drivers.nextElement();
    
    		try {
    			DriverManager.deregisterDriver(driver);
    		} catch(Exception e) {
    			this.logger.logException(e);
    		}
    	}
    }
    
    public void cleanup() {
    	try {
    		Class<?> cls = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
    		Method method = null;
    		if(cls != null) {
    			method = cls.getMethod("shutdown");
    			method.invoke(null);
    			this.logger.log("MySQL connection cleanup thread shutdown successfully.", ServerLog.LOG_INFO);
    		}
    	} catch(Exception e) {
    		this.logger.logException(e);
    	}
    }
}
