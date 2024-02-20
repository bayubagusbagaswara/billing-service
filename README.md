
@Query("SELECT t FROM Transaction t " +
"WHERE YEAR(t.settlementDate) = YEAR(CURRENT_DATE) " +
"AND MONTH(t.settlementDate) = MONTH(CURRENT_DATE) " +
"AND DAY(t.settlementDate) BETWEEN 1 AND 30")
List<Transaction> findTransactionsForCurrentMonth();

    @Query("SELECT t FROM Transaction t " +
            "WHERE YEAR(t.settlementDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(t.settlementDate) = MONTH(CURRENT_DATE)")
    List<Transaction> findTransactionsForCurrentMonth();

        // Filter transactions for the current month
        List<Transaction> filteredTransactions = transactionList.stream()
                .filter(transaction -> {
                    LocalDate settlementDate = LocalDate.parse(transaction.getSettlementDate());
                    return settlementDate.getYear() == currentYear && settlementDate.getMonthValue() == currentMonth;
                })
                .collect(Collectors.toList());

    // Filter transactions for the current month (1-30)
    List<Transaction> filteredTransactions = transactionList.stream()
            .filter(transaction -> {
                LocalDate settlementDate = LocalDate.parse(transaction.getSettlementDate());
                int settlementMonth = settlementDate.getMonthValue();
                int settlementYear = settlementDate.getYear();
                return settlementYear == currentYear && settlementMonth == currentMonth &&
                        settlementDate.getDayOfMonth() >= 1 && settlementDate.getDayOfMonth() <= 30;
            })
            .collect(Collectors.toList());

# Billing Fund Generate Process

- User akan mengupload file Fee Report, beserta Period nya
- Dari front fee report ini akan di breakdown menjadi data List JSON, lalu akan dikirim ke belakang
- Perlu catatan: di belakang juga harus cek apakah file SKTRAN, RG Daily, RG Monthly pada bulan tahun (dari inputan depan) tersebut tersedia

# Billing Core Generate Process
- SK Transaction, RG Daily dan RG Monthly sudah ada di folder server

# Perlu dipertanyakan?
- Apakah semua nasabah akan di generate billingnya? meskipun tidak ada di file upload nya

```csv
1,Safekeeping fee for account BDMN123456789
2,Return liquidity to BI Fee
3,Safekeeping fee for account BDMN12345678
4,Instruction fee - RECEIPT BOND
```

- cari method untuk mengambil tanggal terakhir pada bulan sekarang
- cari method untuk mengambil tanggal awal pada bulan sekarang


@RestController
@RequestMapping("/api")
public class PdfController {

    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping("/generate-pdf")
    public String generatePdf(@RequestBody PdfRequest pdfRequest) throws IOException {
        // Extract data from the request body
        String title = pdfRequest.getTitle();
        String content = pdfRequest.getContent();

        // Render Thymeleaf template to HTML
        String htmlContent = renderThymeleafTemplate(title, content);

        // Generate PDF from HTML content
        byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

        // Save the PDF to a folder on the server
        String outputPath = "/path/to/your/server/folder/generated.pdf";
        savePdfToFile(pdfBytes, outputPath);

        // Return a response, e.g., the file path
        return "PDF saved to: " + outputPath;
    }

    private void savePdfToFile(byte[] pdfBytes, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(pdfBytes);
            fos.flush();
        }
    }

    private String renderThymeleafTemplate(String title, String content) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("content", content);
        return templateEngine.process("myTemplate", context);
    }
}

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfGenerator {

    public byte[] generatePdfFromHtml(String htmlContent) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos, false);
            renderer.finishPDF();
            return baos.toByteArray();
        }
    }

========
@PostMapping("/generate-pdf")
public String generatePdf(@RequestBody PdfRequest pdfRequest) throws IOException {
// Extract data from the request body
String title = pdfRequest.getTitle();
String content = pdfRequest.getContent();

        // Render Thymeleaf template to HTML
        String htmlContent = renderThymeleafTemplate(title, content);

        // Generate PDF from HTML content
        byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

        // Generate a unique file name based on the current date
        String fileName = generateFileName();

        // Save the PDF to a folder on the server
        String outputPath = "/path/to/your/server/folder/" + fileName;
        savePdfToFile(pdfBytes, outputPath);

        // Return a response, e.g., the file path
        return "PDF saved to: " + outputPath;
    }

    private void savePdfToFile(byte[] pdfBytes, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(pdfBytes);
            fos.flush();
        }
    }

    private String renderThymeleafTemplate(String title, String content) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("content", content);
        return templateEngine.process("myTemplate", context);
    }

    private String generateFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        String formattedDate = dateFormat.format(new Date());
        return "PDF_" + formattedDate + ".pdf";
    }