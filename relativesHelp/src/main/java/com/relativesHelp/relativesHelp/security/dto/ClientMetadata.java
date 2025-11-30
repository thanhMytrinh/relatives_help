package com.relativesHelp.relativesHelp.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientMetadata {
    private final String ipAddress;
    private final String userAgent;
    private final String deviceInfo;

    public static ClientMetadata empty() {
        return ClientMetadata.builder().build();
    }
}

