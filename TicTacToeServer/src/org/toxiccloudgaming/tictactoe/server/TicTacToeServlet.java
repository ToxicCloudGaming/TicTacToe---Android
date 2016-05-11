package org.toxiccloudgaming.tictactoe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.toxiccloudgaming.tictactoe.server.game.GameManager;
import org.toxiccloudgaming.tictactoe.server.player.Player;

@WebServlet("/tictactoe")
public class TicTacToeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getParameter("player_list") != null) {
			PrintWriter writer = response.getWriter();
			
			GameManager manager = (GameManager)this.getServletContext().getAttribute("GameManager");
			StringBuilder msg = new StringBuilder();
			msg.append("<html>");
			msg.append("<body>");
			msg.append("<h2>Tic-Tac-Toe Server Player List:</h2>\n");
			msg.append("<h3>Total players online: " + manager.getPlayerList().size() + "</h3><br>");
			
			for(Map.Entry<String, Player> entry : manager.getPlayerList().entrySet()) {
				Player player = entry.getValue();
				msg.append("<strong>" + player.getUsername() + ":</strong><br>");
				msg.append("Wins: " + player.getWins() + "  Losses: " + player.getLosses() + "<br>");
			}
			
			msg.append("</body>");
			msg.append("</html>");
			
			writer.print(msg.toString());
		} else if(request.getParameter("clear_games") != null) {
			GameManager manager = (GameManager)this.getServletContext().getAttribute("GameManager");
			manager.clearAllGames();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionID = request.getSession().getId();
		
		GameManager manager = (GameManager)this.getServletContext().getAttribute("GameManager");
		ServerLog logger = (ServerLog)this.getServletContext().getAttribute("ServerLog");
		
		StringBuilder sb = new StringBuilder();
		String json = null;
		String line = null;
		
		BufferedReader reader = request.getReader();
		
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		json = sb.toString();
		JSONObject jsonResult = null;
		
		try {
			JSONObject jsonObject = (JSONObject)new JSONParser().parse(json);
			
			jsonResult = manager.getActionManager().act(sessionID, jsonObject);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.print(jsonResult);
	}
}
