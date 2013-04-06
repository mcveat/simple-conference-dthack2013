# --- !Ups

ALTER TABLE contact ADD name VARCHAR(255) NOT NULL DEFAULT 'originally no name';
ALTER TABLE contact ADD initiator BOOL DEFAULT false;

# --- !Downs

ALTER TABLE contact DROP name;
ALTER TABLE contact DROP initiator;
