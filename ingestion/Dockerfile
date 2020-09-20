FROM effeerre/hadoop
USER root

RUN mkdir /usr/local/flume
RUN mkdir /usr/local/flume/apache-flume-1.9.0-bin
ADD apache-flume-1.9.0-bin /usr/local/flume/apache-flume-1.9.0-bin
ENV FLUME_HOME /usr/local/flume/apache-flume-1.9.0-bin/
ENV PATH $PATH:$FLUME_HOME/bin/
