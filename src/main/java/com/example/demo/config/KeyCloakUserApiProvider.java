package com.example.demo.config;
/**
import com.example.demo.dto.UserDTO;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.utils.MediaType;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;


 * KeyCloakUserApiProvider
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 15:25:40
public class KeyCloakUserApiProvider implements RealmResourceProvider {
    private final KeycloakSession session;
    private final String defaultAttr = "merchant_id";
    private final UserMapper userMapper;

    public KeyCloakUserApiProvider(KeycloakSession session) {
        this.session = session;
        this.userMapper = new UserMapper();
    }

    public void close() {
    }

    public Object getResource() {
        return this;
    }

    @GET
    @Path("users/search-by-attr")
    @NoCache
    @Produces({ MediaType.APPLICATION_JSON })
    @Encoded
    public List<UserDTO> searchUsersByAttribute(@DefaultValue(defaultAttr) @QueryParam("attr") String attr,
                                                @QueryParam("value") String value) {
        return session.users().searchForUserByUserAttribute(attr, value, session.getContext().getRealm())
                .stream().map(e -> userMapper.mapToUserDto(e)).collect(Collectors.toList());
    }
}
 **/