package com.final_project_spring.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.final_project_spring.authentication.JwtService;
import com.final_project_spring.constant.CustomerConstants;
import com.final_project_spring.dto.AuthenticationRequest;
import com.final_project_spring.dto.AuthenticationResponse;
import com.final_project_spring.dto.RegisterRequest;
import com.final_project_spring.entity.Token;
import com.final_project_spring.entity.TokenType;
import com.final_project_spring.entity.User;
import com.final_project_spring.repository.TokenRepository;
import com.final_project_spring.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private EmailSenderService emailSenderService;

	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder().firstname(request.getFirstName()).lastname(request.getLastName())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole()).build();
		var savedUser = repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		saveUserToken(savedUser, jwtToken);

		String activationToken = UUID.randomUUID().toString();
		saveActivationToken(savedUser, activationToken); // Сохраняем токен активации

		String activationLink = "http://localhost:8181/api/activate?token=" + activationToken;
		emailSenderService.sendSimpleEmail(
				savedUser.getEmail(),
				"Подтверждение регистрации",
				"Для активации вашего аккаунта, пожалуйста, перейдите по следующей ссылке: " + activationLink
		);

		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	private void saveActivationToken(User user, String token) {
		Token activationToken = new Token();
		activationToken.setUser(user);
		activationToken.setToken(token);
		activationToken.setExpired(false);
		activationToken.setRevoked(false);
		tokenRepository.save(activationToken);
	}


//	public AuthenticationResponse register(RegisterRequest request) {
//		var user = User.builder()
//				.firstname(request.getFirstName())
//				.lastname(request.getLastName())
//				.email(request.getEmail())
//				.password(passwordEncoder.encode(request.getPassword()))
//				.role(request.getRole())
//				.build();
//		var savedUser = repository.save(user);
//
//		String activationToken = UUID.randomUUID().toString();
//		saveActivationToken(savedUser, activationToken);
//
//
//
//		String activationLink = "http://localhost:8181/api/activate?token=" + activationToken;
//
//		emailSenderService.sendSimpleEmail(
//				savedUser.getEmail(),
//				"Подтверждение регистрации",
//				"Для активации вашего аккаунта, пожалуйста, перейдите по следующей ссылке: " + activationLink
//		);
//
//		return AuthenticationResponse.builder()
//				.accessToken(jwtService.generateToken(savedUser))
//				.refreshToken(jwtService.generateRefreshToken(savedUser))
//				.build();
//	}
//
//	private void saveActivationToken(User user, String token) {
//		Token activationToken = new Token();
//		activationToken.setUser(user);
//		activationToken.setToken(token);
//		activationToken.setExpired(false);
//		activationToken.setRevoked(false);
//		tokenRepository.save(activationToken);
//	}





	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}


	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}


	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith(CustomerConstants.BEARER_)) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.repository.findByEmail(userEmail).orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}
}
