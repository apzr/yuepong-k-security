package com.example.demo.config;
/**
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;


 * KeyCloakUserApiProviderFactory
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 15:21:34

public class KeyCloakUserApiProviderFactory implements RealmResourceProviderFactory {
    public static final String ID = "userapi-rest";

    public RealmResourceProvider create(KeycloakSession session) {
        return new KeyCloakUserApiProvider(session);
    }

    @Override
    public void init(Config.Scope scope) {
    }
    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }
    @Override
    public void close() {
    }
    @Override
    public String getId() {
        return ID;
    }
}
 **/