package com.bayu.billingservice.util;

import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;

@Component
public class PdfGenerator {

    public void generatePdfFromHtml(String htmlContent, OutputStream outputStream) throws Exception {
        try (OutputStream os = outputStream) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
        }
    }
}
