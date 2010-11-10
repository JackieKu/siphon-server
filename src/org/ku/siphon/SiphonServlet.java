package org.ku.siphon;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class SiphonServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SiphonServlet.class.getName());

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String username;
	private String password;
	private User user;

	private class AuthFailedException extends Exception {
		private AuthFailedException(String message) {
			super(message);
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException
	{
		request = req;
		response = resp;

		// Why they can deprecate an API that is not full implemented in the new interface?
		@SuppressWarnings({ "unchecked", "deprecation" })
		Map<String, String[]> query = HttpUtils.parseQueryString(req.getQueryString());

		String type = query.get("type")[0];
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");

		// This is required to get the client work properly. :-(
		// The document doesn't say how to deal with empty email and password.
		if (!query.containsKey("email")
			|| !query.containsKey("password")
			|| (username = query.get("email")[0]).isEmpty()
			|| (password = query.get("password")[0]).isEmpty())
		{
			success();
			return;
		}

		try {
			if (type.equals("get"))
				handleGet();
			else if (type.equals("set"))
				handleSet();
			else if (type.equals("signup"))
				handleSignup();
			else if (type.equals("forgot"))
				handleForgot();
		} catch (AuthFailedException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			fail();
		}
	}

	private void checkUser() throws AuthFailedException {
		user = null;
		try {
			user = new User(username);
			if (!user.checkPassword(password))
				user = null;
		} catch (EntityNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		} finally {
			if (user == null)
				throw new AuthFailedException("auth failed.");
		}
	}

	private void handleSignup() throws IOException {
		User user = null;
		try {
			user = new User(username, password);
		} catch (NoSuchAlgorithmException e) {
			fail();
			return;
		}
		user.store();
		success();
	}

	private void handleGet() throws AuthFailedException, IOException {
		checkUser();

		String addons = user.getAddons();
		if (addons.isEmpty())
			addons = "{}";

		log.info("Get: " + addons);
		response.getWriter().print("{\"retval\": 0, \"alert_message\": null, \"status_message\": \"ok\", \"addons\": " + addons + "}");
	}

	private void handleSet() throws AuthFailedException, IOException {
		checkUser();

		BufferedReader reader = request.getReader();
		String addons = "", line;
		while ((line = reader.readLine()) != null)
			addons += line;
		user.setAddons(addons);
		user.store();

		log.info("Set: " + addons);
		success();
	}

	private void handleForgot() throws IOException {
		// not implemented
		fail();
	}

	private void success() throws IOException {
		response.getWriter().print("{\"retval\": 0}");
	}

	private void fail() throws IOException {
		response.getWriter().print("{\"retval\": 1}");
	}
}
