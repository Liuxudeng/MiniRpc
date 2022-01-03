package com.mini.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author
 * @date
 * @description
 */
@Getter
@AllArgsConstructor
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
