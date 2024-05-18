
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


<tbody align="center">
                                <tr style="font-size:10pt;">
                                    <td th:text="${safekeepingFee} + ' %'" style="text-align: center;">[SK_Fee]</td>
                                </tr>
                                <tr align="center" style="font-size:9pt;">
                                    <td colspan="2">p.a each</td>
                                </tr>
                                </tbody>

## Validation

```java
public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest) {
    // Other code...

    for (InvestmentManagementDTO dto : investmentManagementListRequest.getInvestmentManagementRequestList()) {
        Errors errors = validateInvestmentManagementDTO(dto);

        if (errors.hasErrors()) {
            // Handle validation errors
            List<String> errorMessages = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));

            ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO();
            errorMessageDTO.setCode(dto.getCode());
            errorMessageDTO.setErrorMessageList(errorMessages);
            errorMessageInvestmentManagementDTOList.add(errorMessageDTO);
            totalDataFailed++;
        } else {
            // DTO is valid, proceed with processing
            // Save data, etc.
            totalDataSuccess++;
        }
    }

    // Other code...

    return CreateInvestmentManagementListResponse.builder()
            .totalDataSuccess(totalDataSuccess)
            .totalDataFailed(totalDataFailed)
            .errorMessages(errorMessageInvestmentManagementDTOList)
            .build();
}
```

# Flow Approve

```java 
  public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data: {}", investmentManagementListRequest);
        Long dataChangeId = investmentManagementListRequest.getDataChangeId();
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();
        
        try {
            for (InvestmentManagementDTO dto : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(dto);

                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }

                if (isCodeAlreadyExists(dto.getCode())) {
                    errorMessages.add("Code '" + dto.getCode() + "' is already taken");
                }

                if (errorMessages.isEmpty()) {
                    InvestmentManagement investmentManagementEntity = investmentManagementRepository.findById(dto.getId())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management with id '" + dto.getId() + "' not found"));
                    
                    investmentManagementEntity.setCode(dto.getCode());
                    investmentManagementEntity.setName(dto.getName());
                    investmentManagementEntity.setEmail(dto.getEmail());
                    investmentManagementEntity.setAddress1(dto.getAddress1());
                    investmentManagementEntity.setAddress2(dto.getAddress2());
                    investmentManagementEntity.setAddress3(dto.getAddress3());
                    investmentManagementEntity.setAddress4(dto.getAddress4());
                    InvestmentManagement investmentManagementSave = investmentManagementRepository.save(investmentManagementEntity);

                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementSave);
                    BillingDataChange dataChangeEntity = dataChangeRepository.findById(dataChangeId)
                            .orElseThrow(() -> new DataNotFoundException("Data Change with id '" + dataChangeId + "' not found"));
                    dataChangeEntity.setActionStatus(ActionStatus.EDIT);
                    dataChangeEntity.setInputId(inputId);
                    dataChangeEntity.setInputIPAddress(inputIPAddress);
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    
                    dataChangeRepository.save(dataChangeEntity);
                    totalDataSuccess++;
                } else {
                    // Update data change, dengan Approval Status Reject dan description nya adalah error list
                    InvestmentManagement investmentManagementEntity = investmentManagementRepository.findById(dto.getId())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management with id '" + dto.getId() + "' not found"));

                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(dto);
                    BillingDataChange dataChangeEntity = dataChangeRepository.findById(dataChangeId)
                            .orElseThrow(() -> new DataNotFoundException("Data Change with id '" + dataChangeId + "' not found"));
                    dataChangeEntity.setActionStatus(ActionStatus.EDIT);
                    dataChangeEntity.setInputId(inputId);
                    dataChangeEntity.setInputIPAddress(inputIPAddress);
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription(StringUtil.joinStrings(errorMessages));

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataFailed++;
                }
            }

            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("Error when update list: {}", e.getMessage());
            throw new CreateDataException("Error saving investment management data", e);
        }
    }
```

//    private void handleGeneralError(Exception e, List<ErrorMessageInvestmentManagementDTO> errorMessageList) {
//        log.error("An error occurred while processing investment management records: {}", e.getMessage());
//        String errorMessage = "An error occurred: " + e.getMessage();
//        errorMessageList.add(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(errorMessage)));
//    }

//                errorMessages.add("Error processing customer: " + customerDTO.getCustomerCode() + " - " + e.getMessage());


    @Column(name = "json_data_before", columnDefinition = "nvarchar(max)")


@GetMapping("/download")
public ResponseEntity<FileSystemResource> downloadFile() {
String fileName = "file.zip"; // Nama file yang akan diunduh
File file = new File(DOWNLOAD_FOLDER + "/" + fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

# Tomorrow
- Perbaiki model billing dengan menambahkan Approval
- Perbaiki service billing (Fund, Core, Retail) dengan menambahkan inputId, inputIPAddress, inputDate, approveId, approveIPAddress, approveDate
- Bagaimana approve billingnya? soalnya ketika approve juga membawa approveId dan approveIPAddress
- Lanjut menambahkan Data Change pada maintenance
- Pindahi Transaction Handling yang diambil dari Customer

# Yang perlu Data Change
- Investment Management (DONE)
- Customer (DONE)
- Fee Parameter (Edit dan Upload (create List) untuk data change). Yg masuk data change hanya edit 
- Fee Schedule (semua ada, kecuali upload atau create list). Create nya satu per satu
- Selling Agent (semua ada, kecuali upload atau create list). Create nya satu per satu
- Nasabah Transfer Asset (semua ada, kecuali upload atau create list). Create nya satu per satu
- Exchange Rate (hanya edit). Edit masuk data change. Dan edit approve

# Customer DT0

```json
{
  "id": "Long", 
  "customerCode": "String",
  "customerName": "String", 
  "billingCategory": "String", 
  "billingType": "String", 
  "billingTemplate": "String", 
  "currency": "String", 
  "investmentManagementCode": "String", 
  "investmentManagementName": "String",
  "account": "String", 
  "costCenterDebit": "String", 
  "accountName": "String", 
  "glAccountHasil": "String", 
  "customerMinimumFee": "String", 
  "customerSafekeepingFee": "String", 
  "transactionHandling": "String", 
  "npwpNumber": "String", 
  "npwpName": "String", 
  "npwpAddress": "String",
  "costCenter": "String", 
  "kseiSafeCode": "String", 
  "sellingAgentCode": "String"
}
```

# Customer Entity

```json
{
  "id": "Long",
  "customerCode": "String",
  "customerName": "String",
  "billingCategory": "String",
  "billingType": "String",
  "billingTemplate": "String",
  "currency": "String",
  "investmentManagementCode": "String",
  "investmentManagementName": "String",
  "account": "String",
  "costCenterDebit": "String",
  "accountName": "String",
  "glAccountHasil": "String",
  "customerMinimumFee": "Big Decimal",
  "customerSafekeepingFee": "Big Decimal",
  "transactionHandling": "Big Decimal",
  "npwpNumber": "String",
  "npwpName": "String",
  "npwpAddress": "String",
  "costCenter": "String",
  "kseiSafeCode": "String",
  "sellingAgentCode": "String"
}
```

# Handle Json Processing Exception

    private void handleJsonProcessingException(InvestmentManagementDTO investmentManagementDTO, JsonProcessingException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Error processing JSON during data change logging: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("Error processing JSON during data change logging: " + e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, validationErrors));
    }

public static void main(String[] args) {
// JSON string data
String jsonDataAfter = "{\"code\":\"A002\",\"name\":\"PT Pacific Capital Investment\",\"email\":\"pacific@mail.com\",\"address1\":\"Menara Jamsostek Menara Utara Lantai 12A\",\"address2\":\"Jl. Jendral Gatot Subroto No. 38\",\"address3\":\"\",\"address4\":\"\"}";

        try {
            // ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to Map
            Map<String, Object> jsonMap = objectMapper.readValue(jsonDataAfter, new TypeReference<Map<String, Object>>() {});

            // ModelMapper instance
            ModelMapper modelMapper = new ModelMapper();

            // Convert Map to InvestmentManagementDTO
            InvestmentManagementDTO investmentManagementDTO = modelMapper.map(jsonMap, InvestmentManagementDTO.class);

            // Print the mapped object
            System.out.println(investmentManagementDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    // @Pattern(regexp = "^[0-9]*$", message = "Code must contain only numeric digits") Hanya ANGKA
//    @Pattern(regexp = "^[0-9.-]*$", message = "Input must contain only numbers, dots, or dashes")
//    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Input must contain only alphabetic characters and spaces")


    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END "
            + "FROM selling_agent "
            + "WHERE code = :code", nativeQuery = true)

# Tomorrow
1. Create model and service Report Generator
2. Repair model Customer, and add some field. Make sure to equal customer DTO
3. Insert data investment management
4. Insert data customer
5. Repair calculate billing Fund
6. Repair calculate billing Core


# Customer DTO for Pattern


    // need a pattern (numeric & alphabet, not special character)
    @NotBlank(message = "Customer Code cannot be blank")
    private String customerCode;

    // need a pattern (numeric & alphabet, not special character)
    @NotBlank(message = "Customer Name cannot be blank")
    private String customerName;

    @NotBlank(message = "Billing Category cannot be blank")
    private String billingCategory;

    @NotBlank(message = "Billing Type cannot be blank")
    private String billingType;

    @NotBlank(message = "Billing Template cannot be blank")
    private String billingTemplate;

    @NotEmpty(message = "Currency cannot be blank")
    private String currency;

    @NotEmpty(message = "MI Code cannot be blank")
    private String investmentManagementCode;

    private String investmentManagementName;

    // need a patter, must be numeric for string
    private String account;

    // need a patter, must be numeric for string
    private String debitTransfer;

    // need a pattern (numeric & alphabet, not special character)
    private String accountName;

    // need a pattern, must be numeric for string
    private String glAccountHasil;

    // need a pattern must be numeric because big decimal
    private String customerMinimumFee;

    // need a pattern (must be numeric because big decimal)
    @NotEmpty(message = "Customer Safekeeping Fee cannot be empty")
    private String customerSafekeepingFee;

    // need a pattern must be numeric because big decimal
    private String customerTransactionHandling;

    // need a pattern, must be numeric for string
    private String npwpNumber;

    // need a pattern (numeric & alphabet, not special character)
    private String npwpName;

    private String npwpAddress;

    // need a pattern, must be numeric for string
    @NotEmpty(message = "Cost Center cannot be empty")
    private String costCenter;

    // need a pattern (numeric & alphabet, not special character)
    private String kseiSafeCode;

    // need a pattern (numeric & alphabet, not special character)
    private String sellingAgentCode;

```json
[
  {
    "templateName": "FUND_TEMPLATE",
    "category": "FUND",
    "type": "TYPE_1"
  },
  {
    "templateName": "CORE_TEMPLATE_1",
    "category": "CORE",
    "type": "TYPE_1"
  },
  {
    "templateName": "CORE_TEMPLATE_1",
    "category": "CORE",
    "type": "TYPE_2"
  }
]
```