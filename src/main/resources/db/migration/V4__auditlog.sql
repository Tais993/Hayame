CREATE TABLE audit_log
(
    case_id         BIGINT AUTO_INCREMENT,
    author          BIGINT       NULL,
    target          BIGINT       NULL,
    reason          VARCHAR(500) NULL,
    type            INT          NOT NULL,
    expire_time     TIMESTAMP    NULL,
    creation_time   TIMESTAMP    NOT NULL,
    attachment_urls TEXT         NULL,
    CONSTRAINT audit_log_pk
        UNIQUE (case_id)
);

CREATE UNIQUE INDEX audit_log_case_id_uindex
    ON audit_log (case_id);

