package com.final_project_spring.service;

import java.io.IOException;

import com.final_project_spring.dto.AuthenticationRequest;
import com.final_project_spring.dto.AuthenticationResponse;
import com.final_project_spring.dto.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthenticationService {

	public AuthenticationResponse register(RegisterRequest request);

	public AuthenticationResponse authenticate(AuthenticationRequest request);

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
