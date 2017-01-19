package org.asgs.jshellconnector.websocket.config;

import org.asgs.jshellconnector.websocket.JShellWebSocketEndpoint;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by asgs on 06-01-2017.
 */
public class JShellWebSocketApplicationConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set) {
        Set<ServerEndpointConfig> endpointConfigs = new HashSet<>();
        endpointConfigs.add(ServerEndpointConfig.Builder
                .create(JShellWebSocketEndpoint.class, "/")
                .build());
        return endpointConfigs;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
        return new HashSet();
    }
}
