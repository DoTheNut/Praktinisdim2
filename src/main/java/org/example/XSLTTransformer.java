package org.example;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class XSLTTransformer {
    public static void transformToHTML(File xml, File xsl, File htmlOutput) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xsl);
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(xml);
        transformer.transform(text, new StreamResult(htmlOutput));
    }

    public static void transformToPDF(File xml, File xsl, File pdfOutput) throws Exception {
        // Initialize FOP factory object where the XSL-FO configuration is defined
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        // Setup output stream. Note: Using a BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
        OutputStream out = new FileOutputStream(pdfOutput);

        try {
            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsl));

            // Setup input for XSLT transformation
            Source src = new StreamSource(xml);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) {
        try {
            File xml = new File("data.xml");
            File xslToHTML = new File("transform.xsl");
            File htmlOutput = new File("output.html");

            transformToHTML(xml, xslToHTML, htmlOutput);

            File xslToPDF = new File("transform.fo.xsl");
            File pdfOutput = new File("output.pdf");

            transformToPDF(xml, xslToPDF, pdfOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
