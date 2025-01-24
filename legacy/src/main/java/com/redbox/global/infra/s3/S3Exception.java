package com.redbox.global.infra.s3;

public class S3Exception extends RuntimeException {
    public S3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
