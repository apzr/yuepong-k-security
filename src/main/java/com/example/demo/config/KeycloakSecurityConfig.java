package com.example.demo.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.annotation.Resource;

/**
 * KeycloakSecurityConfig
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/16 13:59:17
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Value("${keycloak.auth-server-url}")
	private String AUTH_URL;

	@Value("${keycloak.realm}")
	private String REALM;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
//      http.authorizeRequests()
//          .antMatchers("/html/*").hasRole("VIEWER")
//          .antMatchers("/api/*").hasRole("API")
//          .anyRequest()
//          .permitAll();
        http.authorizeRequests().anyRequest().permitAll();
        http.csrf().disable();//Invalid CSRF token found for http://127.0.0.1:8888/api/user/create
        http.cors().disable();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    public Keycloak keyCloak() {
        return KeycloakBuilder.builder()
                .serverUrl(AUTH_URL)
                .realm("master")
                .username("admin")
                .password("admin")
				.clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();
    }

    @Bean
    public RealmResource realmResource(Keycloak keyCloak){
        return keyCloak.realm(REALM);
    }

    @Bean
    public UsersResource usersResource(RealmResource realmResource) {
		return realmResource.users();
	}

	@Bean
	public RolesResource rolesResource(RealmResource realmResource) {
		return realmResource.roles();
	}

	@Bean
	public RoleByIdResource roleByIdResource(RealmResource realmResource) {
		return realmResource.rolesById();
	}

	@Bean
	public GroupsResource groupsResource(RealmResource realmResource) {
		return realmResource.groups();
	}

}