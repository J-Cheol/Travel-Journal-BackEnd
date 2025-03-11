package com.traveljournal.domain.auth.dto;

import lombok.Builder;

@Builder
public record GoogleIdTokenInfo (
        String sub,
        String email,
        String nickname,
        String profile_image_url

){ }
