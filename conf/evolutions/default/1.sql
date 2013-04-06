# --- !Ups

CREATE TABLE conference (
  id INT NOT NULL AUTO_INCREMENT,
  date VARCHAR(255) NOT NULL,
  agenda TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE contact (
  id INT NOT NULL AUTO_INCREMENT,
  conference_id INT NOT NULL,
  number VARCHAR(255),
  email VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (conference_id) REFERENCES conference(id) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE contact;
DROP TABLE conference;
