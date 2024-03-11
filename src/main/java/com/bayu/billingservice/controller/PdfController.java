package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.PdfRequest;
import com.bayu.billingservice.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/pdf")
public class PdfController {

    private final PdfGenerator pdfGenerator;
    private final TemplateEngine templateEngine;

    public PdfController(PdfGenerator pdfGenerator, TemplateEngine templateEngine) {
        this.pdfGenerator = pdfGenerator;
        this.templateEngine = templateEngine;
    }

    @PostMapping("/generate-pdf")
    public void generatePdf(@RequestBody PdfRequest pdfRequest, HttpServletResponse response) throws IOException {
        // Extract data from the request body
        String title = pdfRequest.getTitle();
        String content = pdfRequest.getContent();

        // Render Thymeleaf template to HTML
        // Render Thymeleaf template to HTML
        String htmlContent = renderThymeleafTemplate(title, content);

        // Set response headers
        response.setHeader("Content-Disposition", "inline; filename=generated.pdf");
        response.setContentType("application/pdf");

        // Generate PDF from HTML content and write it to the response stream
//        try {
//            pdfGenerator.generatePdfFromHtml(htmlContent, response.getOutputStream());
//            response.flushBuffer();
//        } catch (Exception e) {
//            // Handle exceptions appropriately (e.g., log or return an error response)
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().write("Error generating PDF");
//        }
    }

    private String renderThymeleafTemplate(String title, String content) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("content", content);
        return templateEngine.process("myTemplate", context);
    }
}
