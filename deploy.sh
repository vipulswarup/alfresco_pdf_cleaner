#!/bin/bash

# Check if alfresco installation path is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <alfresco_installation_path>"
    echo "Example: $0 /opt/eisenvault_installations/alfresco"
    exit 1
fi

ALFRESCO_PATH="$1"

# Validate the path
if [ ! -d "$ALFRESCO_PATH" ]; then
    echo "Error: Alfresco installation path does not exist: $ALFRESCO_PATH"
    exit 1
fi

# Check if required directories exist
if [ ! -d "$ALFRESCO_PATH/tomcat/webapps/alfresco/WEB-INF/lib" ]; then
    echo "Error: Alfresco lib directory not found at: $ALFRESCO_PATH/tomcat/webapps/alfresco/WEB-INF/lib"
    exit 1
fi

echo "Building PDF cleaner..."
ant clean build

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo "Creating required directories..."
mkdir -p "$ALFRESCO_PATH/scripts"
mkdir -p "$ALFRESCO_PATH/alfresco/logs"

echo "Deploying files..."
# Copy JAR file
cp build/alf-pdf-cleaner.jar "$ALFRESCO_PATH/tomcat/webapps/alfresco/WEB-INF/lib/"

# Copy and setup the cleaning script
cp src/scripts/clean-oracle-pdf.sh "$ALFRESCO_PATH/scripts/"
chmod +x "$ALFRESCO_PATH/scripts/clean-oracle-pdf.sh"

# Create symlink if it doesn't exist
if [ ! -L "/usr/local/bin/clean-oracle-pdf.sh" ]; then
    echo "Creating symlink for clean-oracle-pdf.sh..."
    sudo ln -s "$ALFRESCO_PATH/scripts/clean-oracle-pdf.sh" /usr/local/bin/clean-oracle-pdf.sh
fi

# Set proper permissions
echo "Setting permissions..."
sudo chown -R evadm:evadm "$ALFRESCO_PATH/alfresco/logs"

echo "Deployment complete!"
echo "Please restart Alfresco to apply changes:"
echo "sudo systemctl restart alfresco" 