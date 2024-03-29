
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


## JUnit Java 8

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.8.2</version> <!-- Sesuaikan versi dengan yang terbaru -->
    <scope>test</scope>
</dependency>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M5</version> <!-- Sesuaikan versi dengan yang terbaru -->
            <dependencies>
                <dependency>
                    <groupId>org.junit.platform</groupId>
                    <artifactId>junit-platform-surefire-provider</artifactId>
                    <version>1.8.2</version> <!-- Sesuaikan versi dengan yang terbaru -->
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {

    @Test
    void testSum() {
        assertEquals(4, 2 + 2, "2 + 2 should equal 4");
    }
}

# Create Table Billing Cor Type 7 Detail
- insert data 3 bulan terakhir
- Input: Type 7, Month November-2023

1. Grouping berdasaran Bulan, lalu jumlahkan semua Market Pricenya
2. Cek hasil penjumlahan 

Di Billing Detail kita tambahkan fied 
- BillingCategory 
- BillingType

kita coba tambahkan data ke Billing Detail
- Date		: 2023-08-31
- PortfolioCode	: 12MUFG
- SecurityCode	: BDMN
- MarketPrice	: 26.195.130.749.660,00 

- Date		: 2023-08-31
- PortfolioCode	: 17GUDH
- SecurityCode	: BDMN
- MarketPrice	: 280.830.051.620,00 

- Date		: 2023-08-31
- PortfolioCode	: 12MUFG
- SecurityCode	: GUDH
- MarketPrice	: 999.500.000,00 



- Date		: 2023-09-30
- PortfolioCode	: 12MUFG
- SecurityCode	: BDMN
- MarketPrice	: 25.569.308.513.320,00  

- Date		: 2023-09-30
- PortfolioCode	: 17GUDH
- SecurityCode	: BDMN
- MarketPrice	: 274.120.801.240,00 
 

- Date		: 2023-09-30
- PortfolioCode	: 12MUFG
- SecurityCode	: GUDH
- MarketPrice	: 999.500.000,00



- Date		: 2023-10-31
- PortfolioCode	: 12MUFG
- SecurityCode	: BDMN
- MarketPrice	: 24.496.470.393.880,00 

- Date		: 2023-10-31
- PortfolioCode	: 17GUDH
- SecurityCode	: BDMN
- MarketPrice	: 262.619.229.160,00  
 

- Date		: 2023-10-31
- PortfolioCode	: 12MUFG
- SecurityCode	: GUDH
- MarketPrice	: 999.500.000,00

  ## Membuat Data di Table Billing Detail
  - untuk kepentingan menarik data Core Type 7, yakni data 3 bulan sebelumnya
  
## Format Billing Number

- C135/SS-BS/1223

- Jika user menekan tombol calculate, maka otomatis semua data yg sudah digenerate akan hilang (REPLACE), hanya sesuai dengan period yang diinputkan
- karena ini adalah urutan process. Jika poin 1 (calculate) dijalankan ulang, maka akan menghapus proses yang sudah ada

# Template Core
1. No VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (without NPWP)
2. VAT (Foreign Client) - type 7.docx
3. VAT (Safekeeping Fee & Trx Handling) - type 1, type 2, type 4 a, type 9, type 11
4. VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (With NPWP)
5. 17 OBAL (EB) - type 4b
6. IIG - type 8
7. No VAT (only Safekeeping Fee) - type 3
8. No VAT (only Trx Handling and Safekeeping Fee) - type 10

# Maintenance
Table BillingFeeParam (untuk fee parameter)
- Long id
- String feeCode
- String feeName
- double feeValue
- String description

Table BillingFeeScedule
- Long id
- Double feeMin
- Double feeMax
- Double feeAmount

Table BillingCustomer (untuk Kyc Customer)
- Long id
- String customerCode
- String customerName
- String category
- String type
- String namaMI
- String alamatMI
- String debitTransfer
- String account
- String accountName
- String glAccountHasil
- String minimumFee
- String npwp
- String nameNPWP
- String customerTemplate
- String alamatNPWP
- String costCenter
- double safekeepingKsei
- String kseiSafeCode
- String currency
- String sellingAgent
- String informationFee

Table BillingEmailProcessing
- Long id
- String customerCode
- String customerName
- String customerEmail
- String period
- String emailStatus
- Date sentAt;
- String description

Table BillingEMaterai
- Long id
- String customerCode
- String securityCode
- String period

Table BillingSecurity
- Long id
- String code
- String group
- String currency
- String issuerName
- String name

# General Ledger (GL)

# Header Column Name SfValCrowdFunding

No | Sett Date | ClientCode | Security | Holding (Face Value) | Price |Market Price | (abaikan) | investor

Ketika akan membeli smartphone produksi Oppo, hindari Oppo seri A
Ketika akan membeli smartphone produksi Realme, hindari Realme seri C
Ketika akan membeli smartphone produksi Vivo, hindari Vivo seri Y
Ketika akan membeli smartphone produksi POCO, hindari POCO seri X
