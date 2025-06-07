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
        // Create temporary files for input and output
        Path tempInput = Files.createTempFile("pdf-input-", ".pdf");
        Path tempOutput = Files.createTempFile("pdf-output-", ".pdf");

        try {
            // Write input content to temporary file
            try (FileOutputStream fos = new FileOutputStream(tempInput.toFile())) {
                reader.getContent(fos);
            }

            // Execute the cleaning script
            ProcessBuilder pb = new ProcessBuilder(SCRIPT_PATH, tempInput.toString(), tempOutput.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("PDF cleaning script failed with exit code: " + exitCode);
            }

            // Write the cleaned output back to Alfresco
            writer.putContent(tempOutput.toFile());

        } finally {
            // Clean up temporary files
            Files.deleteIfExists(tempInput);
            Files.deleteIfExists(tempOutput);
        }
    }

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options) {
        return sourceMimetype.equals("application/pdf") && targetMimetype.equals("application/pdf");
    }
} 