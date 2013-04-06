# --- !Ups

ALTER TABLE conference ADD agenda_url VARCHAR(255) DEFAULT NULL;

# --- !Downs

ALTER TABLE conference DROP agenda_url;
