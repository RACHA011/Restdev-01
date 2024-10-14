package com.racha.restdev.util.constants;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    USER, // user can update read or delete self objects
    ADMIN // admin can read update delete any object
}
