package org.ku.siphon;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SiphonServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SiphonServlet.class.getName());

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String username;
	private String password;
	private User user;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException
	{
		@SuppressWarnings({ "unchecked", "deprecation" })
		Map<String, String[]> query = HttpUtils.parseQueryString(req.getQueryString());

		String type = query.get("type")[0];
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");

		// This is required to get the client work properly. :-(
		if (!query.containsKey("email")
			|| !query.containsKey("password")
			|| (username = query.get("email")[0]).isEmpty()
			|| (password = query.get("password")[0]).isEmpty())
		{
			response.getWriter().print("{\"retval\": 0}");
			return;
		}

		request = req;
		response = resp;

		if (type.equals("get"))
			handleGet();
		else if (type.equals("set"))
			handleSet();
		else if (type.equals("signup"))
			handleSignup();
		else if (type.equals("forgot"))
			handleForgot();
	}

	private void checkUser() throws IOException {
		try {
			user = new User(username);
			if (!user.checkPassword(password))
				throw new Exception();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			throw new IOException("auth failed.");
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException
	{
		doGet(req, resp);
	}

	private void handleSignup() throws IOException {
		User user;
		try {
			user = new User(username, password);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException();
		}
		user.store();
		response.getWriter().print("{\"retval\": 0}");
	}

	private void handleGet() throws IOException {
		checkUser();

		String addons = user.getAddons();
		if (addons.isEmpty())
			addons = "{}";

		log.info("Get: " + addons);
		response.getWriter().print("{\"retval\": 0, \"alert_message\": null, \"status_message\": \"ok\", \"addons\": " + addons + "}");
	}

	private void handleSet() throws IOException {
		checkUser();

		BufferedReader reader = request.getReader();
		String addons = "", line;
		while ((line = reader.readLine()) != null)
			addons += line;
		user.setAddons(addons);
		user.store();

		log.info("Set: " + addons);
		response.getWriter().print("{\"retval\": 0}");
	}

	private void handleForgot() throws IOException {
		response.getWriter().print("{\"retval\": 1}");
	}
}
