package com.team.comma.util.s3.exception;

public class S3Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public S3Exception(String message) {
        super(message);
    }
}
