FROM gradle:8.7

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD build/install/app/bin/app