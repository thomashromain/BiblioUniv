FROM mysql:8.0

ENV MYSQL_DATABASE=library_db
ENV MYSQL_USER=USER
ENV MYSQL_PASSWORD=XpJfd5oLdiDo-8
ENV MYSQL_ROOT_PASSWORD=rootpassword

# Copy seed SQL from repository into the image init directory.
# Note: build with repository root as context (e.g. `docker build -f bdd/Dockerfile .`).
COPY backend/src/main/resources/db/seed.sql /docker-entrypoint-initdb.d/seed.sql

EXPOSE 3306

CMD ["mysqld"]
