#!/bin/sh
set -e

# Check if the application is running by making a request to the root URL
# Adjust the URL if needed based on your application's structure
wget --spider --quiet http://localhost:3000/ || exit 1

# If we get here, the application is running
exit 0