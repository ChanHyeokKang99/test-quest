package com.justteam.test_quest_api.jwt.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final String userId;

    public boolean hasMandatory() {
        return userId != null;
    }

    @Override
    public boolean equals(Object another) {
        if( this == another) {
            return true;
        }

        if( another == null) {
            return false;
        }

        if(!getClass().isAssignableFrom(another.getClass())) {
            return false;
        }

        UserPrincipal otherPrincipal = (UserPrincipal) another;

        if(!Objects.equals(this.userId, otherPrincipal.userId)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString(){
        return getName();
    }

    @Override
    public int hashCode() {
        return userId != null? userId.hashCode() : 0;
    }

    @Override
    public String getName() {
        return userId;
    }
}