package me.seon.bootjpaboard.util;

import me.seon.bootjpaboard.domain.User;

import javax.servlet.http.HttpSession;

public class HttpSessionUtil {

	public static final String USER_SEESION_KEY = "sessionUser";

	public static boolean isLoginUser(HttpSession session) {
		Object sessionedUser = session.getAttribute(USER_SEESION_KEY);
		return sessionedUser != null;
	}

	public static User getUserFormSession(HttpSession session) {
		if (!isLoginUser(session)) return null;
		return (User) session.getAttribute(USER_SEESION_KEY);
	}

	public static void setSession(HttpSession session, User user) {
		session.setAttribute(USER_SEESION_KEY, user);
	}

	public static void deleteSession(HttpSession session) {
		session.removeAttribute(USER_SEESION_KEY);
	}

}
