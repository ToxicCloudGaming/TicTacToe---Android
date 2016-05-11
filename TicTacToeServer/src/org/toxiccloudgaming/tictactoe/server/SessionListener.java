package org.toxiccloudgaming.tictactoe.server;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.toxiccloudgaming.tictactoe.server.game.GameManager;

public class SessionListener implements HttpSessionListener {
	
	private final static int SESSION_TIMEOUT = 10;

    public void sessionCreated(HttpSessionEvent event) {
    	HttpSession session = event.getSession();
    	session.setMaxInactiveInterval(SESSION_TIMEOUT);
    	
    	ServerLog logger = (ServerLog)session.getServletContext().getAttribute("ServerLog");
    	logger.log("New session started: " + session.getId(), ServerLog.LOG_INFO);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
    	HttpSession session = event.getSession();

    	ServerLog logger = (ServerLog)session.getServletContext().getAttribute("ServerLog");
		GameManager manager = (GameManager)session.getServletContext().getAttribute("GameManager");
		manager.sessionEnded(session.getId());
    }
}
