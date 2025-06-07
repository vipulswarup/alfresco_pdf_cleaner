package org.alfresco.extension.transformer;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CustomPdfFixTransformer extends AbstractContentTransformer2 {
    private static final Log logger = LogFactory.getLog(CustomPdfFixTransformer.class);
    private static final String SCRIPT_PATH = "/usr/local/bin/clean-oracle-pdf.sh";

    @Override
    protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        logger.info("Starting PDF transformation...");
        logger.info("Source mimetype: " + reader.getMimetype());
        logger.info("Target mimetype: " + writer.getMimetype());
        logger.info("Content URL: " + reader.getContentUrl());

        // Create temporary files for input and output
        Path tempInput = Files.createTempFile("pdf-input-", ".pdf");
        Path tempOutput = Files.createTempFile("pdf-output-", ".pdf");

        logger.info("Created temporary files:");
        logger.info("Input: " + tempInput);
        logger.info("Output: " + tempOutput);

        try {
            // Write input content to temporary file
            logger.info("Writing input content to temporary file...");
            try (FileOutputStream fos = new FileOutputStream(tempInput.toFile())) {
                reader.getContent(fos);
            }

            // Execute the cleaning script
            logger.info("Executing cleaning script: " + SCRIPT_PATH);
            ProcessBuilder pb = new ProcessBuilder(SCRIPT_PATH, tempInput.toString(), tempOutput.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            logger.info("Script execution completed with exit code: " + exitCode);
            
            if (exitCode != 0) {
                String error = new String(process.getInputStream().readAllBytes());
                logger.error("Script error output: " + error);
                throw new IOException("PDF cleaning script failed with exit code: " + exitCode);
            }

            // Write the cleaned output back to Alfresco
            logger.info("Writing cleaned output back to Alfresco...");
            writer.putContent(tempOutput.toFile());
            logger.info("Transformation completed successfully");

        } catch (Exception e) {
            logger.error("Error during transformation", e);
            throw e;
        } finally {
            // Clean up temporary files
            logger.info("Cleaning up temporary files...");
            Files.deleteIfExists(tempInput);
            Files.deleteIfExists(tempOutput);
        }
    }

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options) {
        boolean isTransformable = sourceMimetype.equals("application/pdf") && targetMimetype.equals("application/pdf");
        logger.info("Checking transformability - Source: " + sourceMimetype + ", Target: " + targetMimetype + ", Result: " + isTransformable);
        return isTransformable;
    }
} 