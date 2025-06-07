
# Alfresco PDF Cleaner

This project provides a solution for cleaning Oracle-generated PDFs in Alfresco. It automatically detects PDFs created by Oracle applications and processes them through Ghostscript to ensure proper preview functionality in Alfresco Share.

## Prerequisites

- Ubuntu 24.04 LTS
- Alfresco installed at `/opt/eisenvault_installations/<client_folder>/`
- Java 11 or later
- Apache Ant

## Dependencies

Install the required system packages:

```bash
sudo apt-get update
sudo apt-get install -y \
    ghostscript \
    poppler-utils \
    ant
```

## Installation

1. Clone this repository:
```bash
git clone <repository-url>
cd alfresco_pdf_cleaner
```

2. Build the project:
```bash
ant
```
This will create `build/alf-pdf-cleaner.jar`

3. Deploy the JAR file:
```bash
# Replace <client_folder> with your actual client folder name
sudo cp build/alf-pdf-cleaner.jar /opt/eisenvault_installations/<client_folder>/tomcat/webapps/alfresco/WEB-INF/lib/
```

4. Install the PDF cleaning script:
```bash
# Create scripts directory if it doesn't exist
sudo mkdir -p /opt/eisenvault_installations/<client_folder>/scripts

# Copy the script
sudo cp src/scripts/clean-oracle-pdf.sh /opt/eisenvault_installations/<client_folder>/scripts/

# Make it executable
sudo chmod +x /opt/eisenvault_installations/<client_folder>/scripts/clean-oracle-pdf.sh

# Create a symlink to make it available in PATH
sudo ln -s /opt/eisenvault_installations/<client_folder>/scripts/clean-oracle-pdf.sh /usr/local/bin/clean-oracle-pdf.sh
```

5. Create log directory:
```bash
sudo mkdir -p /opt/eisenvault_installations/<client_folder>/alfresco/logs
sudo chown -R alfresco:alfresco /opt/eisenvault_installations/<client_folder>/alfresco/logs
```

6. Restart Alfresco:
```bash
sudo systemctl restart alfresco
```

## How it Works

The PDF cleaner:
1. Intercepts PDF preview requests in Alfresco
2. Checks if the PDF was created by Oracle
3. If it's an Oracle PDF:
   - Processes it through Ghostscript to clean and optimize
   - Saves the cleaned version for preview
4. If it's not an Oracle PDF:
   - Passes it through unchanged

## Logging

The transformation process is logged to:
```
/opt/eisenvault_installations/<client_folder>/alfresco/logs/oracle_pdf_cleaner.log
```

You can monitor the logs in real-time:
```bash
tail -f /opt/eisenvault_installations/<client_folder>/alfresco/logs/oracle_pdf_cleaner.log
```

## Troubleshooting

1. If PDFs aren't being cleaned:
   - Check if the script is in PATH: `which clean-oracle-pdf.sh`
   - Verify script permissions: `ls -l /usr/local/bin/clean-oracle-pdf.sh`
   - Check Alfresco logs for errors

2. If Ghostscript fails:
   - Verify installation: `gs --version`
   - Check script logs for specific error messages

3. If preview isn't working:
   - Verify JAR deployment: `ls -l /opt/eisenvault_installations/<client_folder>/tomcat/webapps/alfresco/WEB-INF/lib/alf-pdf-cleaner.jar`
   - Check Alfresco logs for transformation errors
