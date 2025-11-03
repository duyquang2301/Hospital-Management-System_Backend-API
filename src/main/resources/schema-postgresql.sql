-- T_ACCOUNT_APPROVAL
CREATE SEQUENCE IF NOT EXISTS public.ACCOUNT_APPROVAL_ID_SEQ
    START WITH 1
    INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS t_account_approval
(
    id                    BIGINT      NOT NULL DEFAULT nextval('account_approval_id_seq'::regclass),
    admin_id              BIGINT      NOT NULL,
    account_status        varchar(20) NOT NULL DEFAULT 'PENDING'::character varying,
    date_request_approval timestamptz NOT NULL DEFAULT now(),
    date_accept_approval  timestamptz,
    updated_id            BIGINT,
    PRIMARY KEY (id)
);

-- T_ADMIN
CREATE SEQUENCE IF NOT EXISTS public.ADMIN_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS t_admin
(
    id                      BIGINT      NOT NULL DEFAULT nextval('admin_id_seq'::regclass),
    account_id              varchar(50) NOT NULL,
    password                text        NOT NULL,
    account_type            varchar(1)  NOT NULL DEFAULT '1'::character varying,
    date_created            timestamptz NOT NULL DEFAULT now(),
    date_freezed            timestamptz,
    date_deleted            timestamptz,
    date_last_login         timestamptz,
    hospital_name           varchar(50),
    hospital_address        varchar(255),
    hospital_address_detail varchar(255),
    hospital_phone_number   varchar(50),
    frozen_yn               varchar(1)  NOT NULL DEFAULT 'N'::character varying,
    deleted_yn              varchar(1)  NOT NULL DEFAULT 'N'::character varying,
    hospital_post_code      varchar(10),
    hospital_id             BIGINT,
    PRIMARY KEY (id)
);

-- T_ADMIN_FORUM
CREATE SEQUENCE IF NOT EXISTS public.ADMIN_FORUM_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS t_admin_forum
(
    id              BIGINT      NOT NULL DEFAULT nextval('admin_forum_id_seq'::regclass),
    type            varchar(50) NOT NULL DEFAULT 'NOTICE'::character varying,
    title           varchar(255),
    description     text,
    date_created    timestamptz NOT NULL DEFAULT now(),
    date_updated    timestamptz,
    date_deleted    timestamptz,
    date_started    timestamptz,
    date_ended      timestamptz,
    date_sent       timestamptz,
    created_id      BIGINT,
    updated_id      BIGINT,
    image_group_id  BIGINT,
    state           varchar(20),
    exposed_rank    BIGINT,
    category        varchar(50),
    term_category   VARCHAR(50),
    detail_category VARCHAR(50)[],
    view_count      BIGINT               DEFAULT 0,
    hospital_id     BIGINT,
    PRIMARY KEY (id)
);

-- T_ADMIN_FORUM_RECEIVED
CREATE SEQUENCE IF NOT EXISTS public.ADMIN_FORUM_RECEIVED_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS t_admin_forum_received
(
    id             BIGINT      NOT NULL DEFAULT nextval('admin_forum_received_id_seq'::regclass),
    admin_forum_id BIGINT      NOT NULL,
    received_id    BIGINT      NOT NULL,
    date_sent      timestamptz NOT NULL DEFAULT now(),
    date_read      timestamptz,
    PRIMARY KEY (id)
);

-- T_USER
CREATE SEQUENCE IF NOT EXISTS USER_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS USER_WITHDRAW_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_USER
(
    id                  BIGINT      NOT NULL DEFAULT nextval('USER_ID_SEQ'::regclass) PRIMARY KEY,
    name                VARCHAR(50),
    nickname            VARCHAR(150),
    login_key           VARCHAR(255),
    login_type          varchar(100),
    date_birth          varchar(50),
    gender              varchar(10),
    category            VARCHAR(150)[],
    date_created        TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_deleted        TIMESTAMPTZ,
    date_last_logged_in TIMESTAMPTZ          DEFAULT NOW()::TIMESTAMPTZ,
    state               VARCHAR(20)          DEFAULT 'CREATED',
    verified_yn         VARCHAR(1)  NOT NULL DEFAULT 'N',
    point               BIGINT      NOT NULL DEFAULT 0,
    device_token        VARCHAR(255),
    marketing_terms     VARCHAR(1)  NOT NULL DEFAULT 'N',
    phone_number        VARCHAR(20),
    city                VARCHAR(50),
    district            VARCHAR(150)[],
    image_group_id      BIGINT,
    banned_yn           VARCHAR(1)           DEFAULT 'N',
    date_banned         TIMESTAMPTZ
);

-- T_HOSPITAL
CREATE SEQUENCE IF NOT EXISTS HOSPITAL_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_HOSPITAL
(
    id               BIGINT       NOT NULL DEFAULT nextval('HOSPITAL_ID_SEQ'::regclass) PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    post_code        varchar(10),
    address          varchar(255) NOT NULL,
    address_detail   TEXT,
    description      TEXT,
    state            VARCHAR(20)  NOT NULL DEFAULT 'CREATED',
    date_created     TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_deleted     TIMESTAMPTZ,
    phone_number     VARCHAR(50),
    consult_count    SMALLINT     NOT NULL DEFAULT 0,
    city             VARCHAR(10),
    district         VARCHAR(100),
    medical_category VARCHAR(100)[],
    image_group_id   BIGINT,
    date_updated     TIMESTAMPTZ           DEFAULT NOW()::TIMESTAMPTZ,
    features         VARCHAR(50)[],
    exposed_rank     SMALLINT     NOT NULL,
    admin_id         BIGINT,
    deleted_yn       varchar(1)   NOT NULL DEFAULT 'N',

    CONSTRAINT fk_admin_id FOREIGN KEY (admin_id) REFERENCES t_admin (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_HOSPITAL_SCHEDULE
CREATE SEQUENCE IF NOT EXISTS HOSPITAL_SCHEDULE_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_HOSPITAL_SCHEDULE
(
    id           BIGINT      NOT NULL DEFAULT nextval('HOSPITAL_SCHEDULE_ID_SEQ'::regclass) PRIMARY KEY,
    hospital_id  BIGINT      NOT NULL,
    day          VARCHAR(5)  NOT NULL,
    start_time   VARCHAR(20),
    end_time     VARCHAR(20),
    date_created TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_updated TIMESTAMPTZ,
    day_off      VARCHAR(1),

    CONSTRAINT fk_hospital_id FOREIGN KEY (hospital_id) REFERENCES T_HOSPITAL (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_DOCTOR
CREATE SEQUENCE IF NOT EXISTS DOCTOR_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_DOCTOR
(
    id               BIGINT        NOT NULL DEFAULT nextval('DOCTOR_ID_SEQ'::regclass) PRIMARY KEY,
    hospital_id      BIGINT        NOT NULL,
    name             VARCHAR(50)   NOT NULL,
    medical_category VARCHAR(50)[] NOT NULL,
    position         varchar(50),
    description      TEXT,
    date_created     TIMESTAMPTZ   NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_deleted     TIMESTAMPTZ,
    state            VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    image_group_id   BIGINT,
    date_updated     TIMESTAMPTZ,

    CONSTRAINT fk_hospital_id FOREIGN KEY (hospital_id) REFERENCES T_HOSPITAL (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_PROMOTION_GROUP
CREATE SEQUENCE IF NOT EXISTS PROMOTION_GROUP_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_PROMOTION_GROUP
(
    id              BIGINT      NOT NULL DEFAULT nextval('PROMOTION_GROUP_ID_SEQ'::regclass) PRIMARY KEY,
    date_created    TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_started    TIMESTAMPTZ NOT NULL,
    date_end        TIMESTAMPTZ NOT NULL,
    state           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    exposed_rank    SMALLINT    NOT NULL,
    name            VARCHAR(150),
    category        varchar(150),
    detail_category varchar(150)[],
    description     TEXT,
    image_group_id  BIGINT,
    admin_id        BIGINT,
    date_updated    TIMESTAMPTZ,

    CONSTRAINT fk_admin_id FOREIGN KEY (admin_id) REFERENCES t_admin (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_EVENT
CREATE SEQUENCE IF NOT EXISTS EVENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_EVENT
(
    id                 BIGINT       NOT NULL DEFAULT nextval('EVENT_ID_SEQ'::regclass) PRIMARY KEY,
    hospital_id        BIGINT       NOT NULL,
    name               VARCHAR(100) NOT NULL,
    price              BIGINT       NOT NULL,
    consult_count      SMALLINT     NOT NULL DEFAULT 0,
    date_created       TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_started       TIMESTAMPTZ  NOT NULL,
    date_updated       TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_end           TIMESTAMPTZ  NOT NULL,
    state              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    image_group_id     BIGINT,
    category           varchar(150) NOT NULL,
    detail_category    varchar(150)[],
    description        TEXT,
    view_count         SMALLINT              DEFAULT 0,
    exposed_rank       SMALLINT     NOT NULL,
    promotion_group_id BIGINT,
    type               VARCHAR(50)  NOT NULL DEFAULT 'EVENT',
    admin_id           BIGINT,

    CONSTRAINT fk_hospital_id FOREIGN KEY (hospital_id) REFERENCES T_HOSPITAL (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_promotion_group_id FOREIGN KEY (promotion_group_id) REFERENCES T_PROMOTION_GROUP (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_admin_id FOREIGN KEY (admin_id) REFERENCES T_ADMIN (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_EVENT_HISTORY
CREATE SEQUENCE IF NOT EXISTS EVENT_HISTORY_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_EVENT_HISTORY
(
    id              BIGINT NOT NULL DEFAULT nextval('EVENT_HISTORY_ID_SEQ'::regclass) PRIMARY KEY,
    event_id        BIGINT NOT NULL,
    before_event_id BIGINT NOT NULL,

    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES T_EVENT (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_before_event_id FOREIGN KEY (before_event_id) REFERENCES T_EVENT (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_VIRTUAL_SURGERY
CREATE SEQUENCE IF NOT EXISTS VIRTUAL_SURGERY_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_VIRTUAL_SURGERY
(
    id                     BIGINT      NOT NULL DEFAULT nextval('VIRTUAL_SURGERY_ID_SEQ'::regclass) PRIMARY KEY,
    user_id                BIGINT      NOT NULL,
    original_file_group_id BIGINT,
    virtual_file_group_id  BIGINT,
    virtual_left_group_id  BIGINT,
    virtual_right_group_id BIGINT,
    type                   VARCHAR(20),
    is_counsel_yn          VARCHAR(1)  NOT NULL DEFAULT 'N',
    date_created           TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_updated           TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_VIRTUAL_SURGERY_PART
CREATE SEQUENCE IF NOT EXISTS VIRTUAL_SURGERY_PART_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_VIRTUAL_SURGERY_PART
(
    id          BIGINT NOT NULL DEFAULT nextval('VIRTUAL_SURGERY_PART_ID_SEQ'::regclass) PRIMARY KEY,
    virtual_id  BIGINT NOT NULL,
    category    VARCHAR(50),
    detail_part VARCHAR(150)[],

    CONSTRAINT fk_virtual_id FOREIGN KEY (virtual_id) REFERENCES T_VIRTUAL_SURGERY (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_ARTICLE
CREATE SEQUENCE IF NOT EXISTS ARTICLE_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_ARTICLE
(
    id             BIGINT      NOT NULL DEFAULT nextval('ARTICLE_ID_SEQ'::regclass) PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    article_type   VARCHAR(50) NOT NULL,
    category       VARCHAR(30)[],
    content        TEXT        NOT NULL,
    date_created   TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_updated   TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    view_count     SMALLINT    NOT NULL DEFAULT 0,
    comment_count  SMALLINT    NOT NULL DEFAULT 0,
    image_group_id BIGINT,
    review_type    VARCHAR(50),
    review_type_id BIGINT,
    available_yn   VARCHAR(1)  NOT NULL DEFAULT 'Y',


    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_COMMENT
CREATE SEQUENCE IF NOT EXISTS COMMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_COMMENT
(
    id           BIGINT       NOT NULL DEFAULT nextval('COMMENT_ID_SEQ'::regclass) PRIMARY KEY,
    article_id   BIGINT       NOT NULL,
    user_id      BIGINT       NOT NULL,
    parent_id    BIGINT,
    content      varchar(255) NOT NULL,
    reply        VARCHAR(1)   NOT NULL DEFAULT 'N',
    date_created TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_updated TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    deleted_yn   VARCHAR(1)   NOT NULL DEFAULT 'N',

    CONSTRAINT fk_article_id FOREIGN KEY (article_id) REFERENCES T_ARTICLE (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_CHATTING
CREATE SEQUENCE IF NOT EXISTS CHATTING_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_CHATTING
(
    id              BIGINT       NOT NULL DEFAULT nextval('CHATTING_ID_SEQ'::regclass) PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    subject_user_id BIGINT       NOT NULL,
    last_message    TEXT,
    date_last_send  TIMESTAMPTZ,
    date_created    TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    firebase_key    varchar(255) NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_subject_user_id FOREIGN KEY (subject_user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_BLOCK
CREATE SEQUENCE IF NOT EXISTS BLOCK_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_BLOCK
(
    id              BIGINT      NOT NULL DEFAULT nextval('CHATTING_ID_SEQ'::regclass) PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    subject_user_id BIGINT      NOT NULL,
    date_created    TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_subject_user_id FOREIGN KEY (subject_user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_BOOKMARK
CREATE TABLE IF NOT EXISTS T_BOOKMARK
(
    user_id      BIGINT      NOT NULL,
    type_id      BIGINT      NOT NULL,
    type         VARCHAR(20) NOT NULL,
    date_created TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_COUNSEL
CREATE SEQUENCE IF NOT EXISTS COUNSEL_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_COUNSEL
(
    id             BIGINT      NOT NULL DEFAULT nextval('COUNSEL_ID_SEQ'::regclass) PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    counsel_type   VARCHAR(50) NOT NULL,
    type_id        BIGINT      NOT NULL,
    state          VARCHAR(50) NOT NULL DEFAULT 'PROGRESS',
    date_created   TIMESTAMPTZ NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_completed TIMESTAMPTZ,
    description    TEXT,
    category       VARCHAR(50)[],
    image_group_id BIGINT,
    virtual_id     BIGINT,
    counsel_answer TEXT,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_virtual_id FOREIGN KEY (virtual_id) REFERENCES T_VIRTUAL_SURGERY (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- T_FILES
CREATE SEQUENCE IF NOT EXISTS FILES_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS FILES_GROUP_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_FILES
(
    id                 BIGINT           NOT NULL DEFAULT nextval('FILES_ID_SEQ'::regclass) PRIMARY KEY,
    group_id           BIGINT           NOT NULL,
    original_file_name TEXT             NOT NULL,
    file_name          TEXT             NOT NULL,
    path               TEXT             NOT NULL,
    file_order         SMALLINT         NOT NULL DEFAULT 1,
    extension          VARCHAR(50)      NOT NULL,
    size               DOUBLE PRECISION NOT NULL,
    user_type          VARCHAR(50)      NOT NULL,
    user_id            BIGINT           NOT NULL,
    update_user_type   VARCHAR(50)      NOT NULL,
    update_user_id     BIGINT           NOT NULL,
    date_created       TIMESTAMPTZ      NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_updated       TIMESTAMPTZ      NOT NULL DEFAULT NOW()::TIMESTAMPTZ
);

-- T_NOTIFICATION
CREATE SEQUENCE IF NOT EXISTS NOTIFICATION_ID_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS T_NOTIFICATION
(
    id             BIGINT       NOT NULL DEFAULT nextval('NOTIFICATION_ID_SEQ'::regclass) PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    type           VARCHAR(150) NOT NULL,
    type_id        BIGINT       NOT NULL,
    detail_type    VARCHAR(150),
    detail_type_id BIGINT,
    date_created   TIMESTAMPTZ  NOT NULL DEFAULT NOW()::TIMESTAMPTZ,
    date_read      TIMESTAMPTZ,
    read_yn        VARCHAR(1)   NOT NULL DEFAULT 'N',

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES T_USER (id) ON DELETE CASCADE ON UPDATE CASCADE
);