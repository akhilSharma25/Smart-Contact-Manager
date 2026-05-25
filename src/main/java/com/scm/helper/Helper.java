package com.scm.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public class Helper {

    public static String normalizeUrl(String rawValue) {
        if (rawValue == null) {
            return "";
        }

        String trimmed = rawValue.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        if (trimmed.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) {
            return trimmed;
        }

        if (trimmed.startsWith("www.")) {
            return "https://" + trimmed;
        }

        if (trimmed.contains(".")) {
            return "https://" + trimmed;
        }

        return trimmed;
    }

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        // 1. Agar OAuth2 se login hua hai (Google ya GitHub)
        if (authentication instanceof OAuth2AuthenticationToken) {

            var oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
            var clientId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

            if (clientId.equalsIgnoreCase("google")) {
                // Google user ka email hamesha 'email' attribute mein hota hai
                return oauth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {

                // GitHub logic: Email null ho sakta hai
                Object emailObj = oauth2User.getAttribute("email");
                if (emailObj != null) {
                    return emailObj.toString();
                } else {
                    // Agar email private hai, toh 'login' (username) @github.com use karo
                    return oauth2User.getAttribute("login").toString() + "@github.com";
                }
            }
        }

        // 2. Agar normal login (Email/Password) hua hai
        return authentication.getName();
    }
}