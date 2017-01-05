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
        System.out.println("Inside getEndpointConfigs. Set is " + set);
        Set<ServerEndpointConfig> set1 = new HashSet<>();
        set1.add(ServerEndpointConfig.Builder
                .create(JShellWebSocketEndpoint.class, "/")
                .build());
        return set1;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
        System.out.println("Returning empty set.");
        return new HashSet();
    }
}
