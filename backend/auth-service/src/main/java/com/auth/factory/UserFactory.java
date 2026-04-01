package com.auth.factory;

import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.common.interfaces.EntityFactory;

/**
 * Factory interface for creating User entities.
 */
public interface UserFactory extends EntityFactory<User, RegisterDto> {
}
