package com.easygroup.dto;

import java.util.UUID;

/**
 * Request body for sharing a list with another user.
 * Allows specifying the target either by user id or by email.
 */
public record ShareListRequest(UUID userId, String email) {}
