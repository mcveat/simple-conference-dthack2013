# --- !Ups

ALTER TABLE conference ADD title VARCHAR(255) DEFAULT 'originally no title';

# --- !Downs

ALTER TABLE conference DROP title;
