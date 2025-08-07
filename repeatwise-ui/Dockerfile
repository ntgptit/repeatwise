# Multi-stage build for Flutter web application
FROM debian:latest AS build-env

# Install Flutter dependencies
RUN apt-get update && \
    apt-get install -y curl git wget unzip libgconf-2-4 gdb libstdc++6 libglu1-mesa fonts-droid-fallback lib32stdc++6 python3 && \
    apt-get clean

# Clone Flutter
RUN git clone https://github.com/flutter/flutter.git /usr/local/flutter

# Add Flutter to PATH
ENV PATH="/usr/local/flutter/bin:/usr/local/flutter/bin/cache/dart-sdk/bin:${PATH}"

# Enable web support
RUN flutter channel stable && \
    flutter upgrade && \
    flutter config --enable-web

# Set working directory
WORKDIR /app

# Copy pubspec files
COPY pubspec.yaml pubspec.lock ./

# Get dependencies
RUN flutter pub get

# Copy source code
COPY . .

# Build web app
RUN flutter build web --release --web-renderer html

# Production stage
FROM nginx:alpine

# Copy built web app to nginx
COPY --from=build-env /app/build/web /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
