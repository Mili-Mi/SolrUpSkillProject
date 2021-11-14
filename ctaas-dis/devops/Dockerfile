# Get openjdk:8-alpine image as base image
FROM openjdk:8-alpine
RUN apk add --update --no-cache bash tar zip unzip curl wget openssl vim

#5010 is debugging port
EXPOSE 8080 9390 8787
        
COPY image-fs/ /

RUN \
        addgroup -g 31415 baadmin && \
        addgroup -g 31416 bacdx && \ 
        adduser -D -u 31415 -G bacdx baadmin && \
        adduser -D -u 31416 -G bacdx bacdx && \
        cd /opt/cadent/ctaas-dis && \
        chown baadmin:bacdx -R /opt  
        
USER baadmin
ENTRYPOINT ["sh" , "bootstrap.sh"]
