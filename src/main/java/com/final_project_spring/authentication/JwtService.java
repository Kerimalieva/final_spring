package com.final_project_spring.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	public boolean isTokenValid(String token, UserDetails userDetails);

	public String extractUsername(String token);

	public String generateToken(UserDetails userDetails);

	public String generateRefreshToken(UserDetails userDetails);
}
