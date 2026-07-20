package com.supermarkettracker.infrastructure.web.security;

import com.supermarkettracker.application.service.TokenInvalidoException;
import com.supermarkettracker.application.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lê o cabeçalho {@code Authorization: Bearer <token>}, valida o access token
 * via {@link TokenService} e popula o {@link SecurityContextHolder}.
 *
 * <p>Requisições sem token seguem sem autenticação — cabe às regras de
 * autorização decidir se o recurso é público ou retorna 401/403. Tokens
 * inválidos são simplesmente ignorados (o contexto fica vazio).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(PREFIXO)) {
            String token = header.substring(PREFIXO.length()).trim();
            try {
                TokenService.TokenPayload payload = tokenService.validar(token);
                if (!tokenService.ehRefresh(payload)) {
                    var authorities = payload.papel() == null
                            ? List.<SimpleGrantedAuthority>of()
                            : List.of(new SimpleGrantedAuthority("ROLE_" + payload.papel()));
                    var authentication = new UsernamePasswordAuthenticationToken(payload, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (TokenInvalidoException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
