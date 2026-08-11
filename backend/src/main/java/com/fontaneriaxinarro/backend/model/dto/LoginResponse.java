package com.fontaneriaxinarro.backend.model.dto;

public record LoginResponse(
    String token,
    String username,
    String fullname
) {}
