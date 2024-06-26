
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

        if (billingType.equalsIgnoreCase(BillingType.TYPE_4.getValue())) {
                if (billingTemplate.equalsIgnoreCase(CORE_TEMPLATE_5.name())) {
                    fileName = generateFileNameEB(customerCode, billingNumber);
                } else if (billingTemplate.equalsIgnoreCase(CORE_TEMPLATE_3.name())) {
                    fileName = generateFileNameITAMA(customerCode, billingNumber);
                }
            } else {
                fileName = generateFileName(customerCode, subCode, billingNumber);
            }


1. Tambahkan data investment management
2. Tambahkan data fee parameter
3. Tambahkan data customer
4. Hitung calculate billing fund

# Update Satuan
- Semua field akan dikirim ke belakang semua, tapi untuk data yg tidak diupdate akan diisi string kosong ""
- Misalnya:
```json
{
  "inputId": "V00028732",
  "inputIPAddress": "10.151.50.122",
  "id": "1",
  "customerCode": "22PWBF",
  "subCode": "",
  "customerName": "Reksa Dana Pinnacle Winner Balanced Fund",
  "billingCategory": "FUND",
  "billingType": "TYPE_1",
  "billingTemplate": "FUND_TEMPLATE",
  "currency": "IDR",
  "miCode": "PIN",
  "miName": "PT PINNACLE PERSADA INVESTAMA",
  "account": "3674882026",
  "debitTransfer": "",
  "accountName": "Reksa Dana Pinnacle Winner Balanced Fund",
  "glAccountHasil": "490125",
  "customerMinimumFee": "2000000.00",
  "npwpNumber": "99.498.267.6-014.000",
  "npwpName": "Reksa Dana Pinnacle Winner Balanced Fund",
  "npwpAddress": "Capital Place Lt 41 Jl Jenderal Gatot Subroto Kav 18 RT 006 Kuningan Barat. Mampang Prapatan. Kota Adm. Jakarta Selatan DKI Jakarta ",
  "costCenter": "9207",
  "kseiSafeCode": "",
  "sellingAgent": "",
  "customerSafekeepingFee": "10000",
  "customerTransactionHandling": "0",
  "gl": "false"
}
```

# Update List
- Hanya field yang akan diupdate yang dikirim ke belakang
- Misalnya:
```json
{
  "inputerId": "V00028732",
  "inputerIPAddress": "10.151.50.122",
  "customerDataListRequests": [
    {
      "Customer Code": "22PWBF",
      "Billing Category": "CORE"
    },
    {
      "Customer Code": "21SMUN",
      "Billing Type": "TYPE_6"
    },
    {
      "Customer Code": "22SMBK",
      "Currency": "USD"
    },
    {
      "Customer Code": "15JATJ",
      "Billing Template": "CORE_TEMPLATE_3"
    }
  ]
}
```

    private void handleDataNotFoundException(InvestmentManagementDTO investmentManagementDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Investment Management not found with id: {}", investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, validationErrors));
    }

# Delete or Audit Trail

public InvestmentManagementResponse deleteSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress) {
log.info("Approve when delete investment management with request: {}", approveRequest);
int totalDataSuccess = 0;
int totalDataFailed = 0;
List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
List<String> validationErrors = new ArrayList<>();
InvestmentManagementDTO investmentManagementDTO = null;

    try {
        /* validate dataChangeId whether it exists or not */
        validateDataChangeId(approveRequest.getDataChangeId());

        /* get data from DataChange */
        Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
        BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
        Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

        /* get investment management by id */
        InvestmentManagement investmentManagement = investmentManagementRepository.findById(entityId)
                .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

        /* maps from entity to dto */
        investmentManagementDTO = investmentManagementMapper.mapToDto(investmentManagement);

        /* set data change for approve id and approve ip address */
        dataChangeDTO.setApproveId(approveRequest.getApproveId());
        dataChangeDTO.setApproveIPAddress(approveIPAddress);
        dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
        dataChangeDTO.setDescription("Successfully approve data change and delete data entity with id: " + investmentManagement.getId());

        /* delete data entity in the database */
        try {
            investmentManagementRepository.delete(investmentManagement);
            totalDataSuccess++;
            dataChangeService.approvalStatusIsApproved(dataChangeDTO); // Catat perubahan setelah penghapusan berhasil
        } catch (Exception e) {
            // Jika penghapusan gagal, catat perubahan tetapi tambahkan pesan ke dalam daftar kesalahan
            handleDeletionError(dataChangeDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
    } catch (DataNotFoundException ex) {
        handleNotFoundError(ex, validationErrors, errorMessageDTOList);
        totalDataFailed++;
    } catch (Exception e) {
        handleGeneralError(investmentManagementDTO, e, validationErrors, errorMessageDTOList);
        totalDataFailed++;
    }
    return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
}

private void handleDeletionError(BillingDataChangeDTO dataChangeDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageDTOList) {
validationErrors.add("Failed to delete data: " + e.getMessage());
// Log the error or perform additional handling if needed
// Catat perubahan meskipun penghapusan gagal
try {
dataChangeService.approvalStatusIsApproved(dataChangeDTO);
} catch (Exception ex) {
validationErrors.add("Failed to record data change: " + ex.getMessage());
// Log the error or perform additional handling if needed
}
}

public CalculateBillingResponse calculateV2(CoreCalculateRequest request) {
    log.info("Start calculate Billing Core type 1 V2 with request : {}", request);

    int totalDataSuccess = 0;
    int totalDataFailed = 0;
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

    try {
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        // ... rest of the code ...

        for (BillingCustomer billingCustomer : billingCustomerList) {
            try {
                // ... customer processing logic ...
            } catch (Exception e) {
                // Handle specific error for this customer and increment totalDataFailed
                String errorMessage = "Error processing customer " + aid + ": " + e.getMessage();
                log.error(errorMessage, e);
                errorMessageDTOList.add(new ErrorMessageDTO(aid, errorMessage));
                totalDataFailed++;
            }
        }
    } catch (Exception e) {
        // Handle general error during overall processing
        String errorMessage = "Unexpected error during Billing Core type 1 V2 calculation: " + e.getMessage();
        log.error(errorMessage, e);
        errorMessageDTOList.add(new ErrorMessageDTO(null, errorMessage)); // Set aid to null for general error
        throw new CalculateBillingException(errorMessage);
    } finally {
        // Close resources if needed
    }

    return CalculateBillingResponse.builder()
            .totalDataSuccess(totalDataSuccess)
            .totalDataFailed(totalDataFailed)
            .errorMessageDTOList(errorMessageDTOList)
            .build();
}

## Refactor

  public CalculateBillingResponse calculateV2(CoreCalculateRequest request) {
        log.info("Start calculate Billing Core type 1 V2 with request : {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            String categoryUpperCase = request.getCategory().toUpperCase();
            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            int year = Integer.parseInt(monthMinus1.get("year"));

            Map<String, String> monthNow = convertDateUtil.getMonthNow();
            String monthNameNow = monthNow.get("monthName");
            int yearNow = Integer.parseInt(monthNow.get("year"));

            // Initialization variable
            int transactionHandlingValueFrequency;
            BigDecimal transactionHandlingAmountDue;
            BigDecimal safekeepingValueFrequency;
            BigDecimal safekeepingAmountDue;
            BigDecimal subTotal;
            BigDecimal vatAmountDue;
            BigDecimal totalAmountDue;
            Instant dateNow = Instant.now();

            BigDecimal vatFee = feeParamService.getValueByName(VAT.name());

            List<BillingCustomer> billingCustomerList = billingCustomerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

            for (BillingCustomer billingCustomer : billingCustomerList) {
                try {
                    String aid = billingCustomer.getCustomerCode();
                    BigDecimal customerMinimumFee = billingCustomer.getCustomerMinimumFee();
                    BigDecimal customerSafekeepingFee = billingCustomer.getCustomerSafekeepingFee();
                    BigDecimal transactionHandlingFee = billingCustomer.getCustomerTransactionHandling();
                    String billingCategory = billingCustomer.getBillingCategory();
                    String billingType = billingCustomer.getBillingType();
                    String miCode = billingCustomer.getMiCode();

                    // check and delete existing billing data with the same month and year
                    coreGeneralService.checkingExistingBillingCore(monthName, year, aid, billingCategory, billingType);

                    InvestmentManagementDTO billingMIDTO = billingMIService.getByCode(miCode);

                    List<SkTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

                    List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

                    transactionHandlingValueFrequency = calculateTransactionHandlingValueFrequency(aid, skTransactionList);

                    transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(aid, transactionHandlingFee, transactionHandlingValueFrequency);

                    safekeepingValueFrequency = calculateSafekeepingValueFrequency(aid, sfValRgDailyList);

                    safekeepingAmountDue = calculateSafekeepingAmountDue(aid, sfValRgDailyList);

                    subTotal = calculateSubTotalAmountDue(aid, transactionHandlingAmountDue, safekeepingAmountDue);

                    vatAmountDue = calculateVATAmountDue(aid, subTotal, vatFee);

                    totalAmountDue = calculateTotalAmountDue(aid, subTotal, vatAmountDue);

                    BillingCore billingCore = BillingCore.builder()
                            .createdAt(dateNow)
                            .updatedAt(dateNow)
                            .approvalStatus(ApprovalStatus.Pending)
                            .billingStatus(BillingStatus.Generated)
                            .customerCode(billingCustomer.getCustomerCode())
                            .customerName(billingCustomer.getCustomerName())
                            .month(monthName)
                            .year(year)
                            .billingPeriod(monthName + " " + year)
                            .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                            .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                            .billingCategory(billingCustomer.getBillingCategory())
                            .billingType(billingCustomer.getBillingType())
                            .billingTemplate(billingCustomer.getBillingTemplate())
                            .investmentManagementName(billingMIDTO.getName())
                            .investmentManagementAddress1(billingMIDTO.getAddress1())
                            .investmentManagementAddress2(billingMIDTO.getAddress2())
                            .investmentManagementAddress3(billingMIDTO.getAddress3())
                            .investmentManagementAddress4(billingMIDTO.getAddress4())
                            .investmentManagementEmail(billingMIDTO.getEmail())
                            .investmentManagementUniqueKey(billingMIDTO.getUniqueKey())

                            .customerMinimumFee(customerMinimumFee)
                            .customerSafekeepingFee(customerSafekeepingFee)

                            .gefuCreated(false)
                            .paid(false)
                            .accountName(billingCustomer.getAccountName())
                            .account(billingCustomer.getAccount())
                            .currency(billingCustomer.getCurrency())

                            .transactionHandlingValueFrequency(transactionHandlingValueFrequency)
                            .transactionHandlingFee(transactionHandlingFee)
                            .transactionHandlingAmountDue(transactionHandlingAmountDue)
                            .safekeepingValueFrequency(safekeepingValueFrequency)
                            .safekeepingFee(customerSafekeepingFee)
                            .safekeepingAmountDue(safekeepingAmountDue)
                            .subTotal(subTotal)
                            .vatFee(vatFee)
                            .vatAmountDue(vatAmountDue)
                            .totalAmountDue(totalAmountDue)
                            .build();

                    String number = billingNumberService.generateSingleNumber(monthNameNow, yearNow);
                    billingCore.setBillingNumber(number);
                    billingCoreRepository.save(billingCore);
                    billingNumberService.saveSingleNumber(number);
                    totalDataSuccess++;
                } catch (Exception e) { ;
                    handleGeneralError(billingCustomer, e, errorMessageDTOList);
                    totalDataFailed++;
                }
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CalculateBillingResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }


else {
if (billingFundRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYearAndPaid(aid, FUND.getValue(), TYPE_1.getValue(), month, year, true).isPresent()) {
List<String> errorMessages = new ArrayList<>();
errorMessages.add("Billing with customer code " + customer.getCustomerCode() + " period "+ month + " " + year + " has already been paid and cannot be regenerated.");
BillingCalculationErrorMessageDTO calculationErrorMessageDTO = new BillingCalculationErrorMessageDTO(customer.getCustomerCode(), errorMessages);
calculationErrorMessages.add(calculationErrorMessageDTO);
totalDataFailed++;
}
}


# Format Billing Number
C001/SS-BS/0624
C002/SS-BS/0624
C003/SS-BS/0624
C004/SS-BS/0624

C100/SS-BS/0624
C101/SS-BS/0624
C102/SS-BS/0624
C103/SS-BS/0624

C1000/SS-BS/0624
C1001/SS-BS/0624
C1002/SS-BS/0624
C1003/SS-BS/0624

# Generate Name PDF File MUFG
  private String generateFileNameMUFG(String billingCategory, String billingType, String billingPeriod, String billingNumber) {
        // Billing Period = Aug 2023 - Oct 2023
        String[] split = billingPeriod.split(" - ");
        String[] s = split[0].split(" ");

        String monthString1 = s[0]; // OCTOBER
        String s2 = s[1]; // 2023

        Month month1 = MonthConverterUtil.getMonth(monthString1.toUpperCase());
        int monthValue1 = month1.getValue();

        String formattedMonth1 = (monthValue1 < 10) ? "0" + monthValue1 : String.valueOf(monthValue1);

        String[] s3 = split[1].split(" ");
        String monthString2 = s3[0]; // AUGUST

        String s5 = s3[1]; // 2023

        Month month2 = MonthConverterUtil.getMonth(monthString2.toUpperCase());

        int monthValue2 = month2.getValue();
        String formattedMonth2 = (monthValue2 < 10) ? "0" + monthValue2 : String.valueOf(monthValue2);

        String replaceBillingNumber = billingNumber.replaceAll("/", "_")
                .replaceAll("-", "_");
        // return "MUFG_" + billingCategory + "_" + billingType + "_" + formattedMonth1 + s2 + "-" + formattedMonth2 + s5 + ".pdf";
        return replaceBillingNumber + ".pdf";
    }

   if (billingType.equalsIgnoreCase(BillingType.TYPE_7.getValue())) {
                String[] split = billingPeriod.split(" - ");
                monthYearMap = convertDateUtil.extractMonthYearInformation(split[1]);
                yearMonthFormat = monthYearMap.get("year") + monthYearMap.get("monthValue");
            } else {
                monthYearMap = convertDateUtil.extractMonthYearInformation(billingPeriod);
                yearMonthFormat = monthYearMap.get("year") + monthYearMap.get("monthValue");
            }

# Test

1. Test update list for customer
2. Test generate PDF billing with different month
3. 
# Footer Template
<!-- Footer -->
<footer style="position: fixed; bottom: 0;">
    <!--<table>-->
        <!--<tbody>-->
        <!--<tr style="text-align: left">-->
            <!--<td style="padding: 10px 0 0 20px;; font-size: 12px; color: gray;">-->
                <!--PT Bank Danamon Indonesia, Tbk.-->
            <!--</td>-->
        <!--</tr>-->
        <!--<tr style="text-align: center">-->
            <!--<td>-->
                <!--<div>-->
                    <!--<img-->
                            <!--th:src="${imageUrlFooter}"-->
                            <!--style="margin-left: 20px"-->
                            <!--alt="Image"-->
                    <!--/>-->
                <!--</div>-->
            <!--</td>-->
        <!--</tr>-->
        <!--</tbody>-->
    <!--</table>-->
    <footer style="position: fixed; bottom: 0; display: flex; justify-content: space-between; align-items: center; padding: 10px 20px; margin-left: 20px;">
        <span style="font-size: 12px; color: gray;">PT Bank Danamon Indonesia, Tbk.</span>
        <img th:src="${imageUrlFooter}" alt="Image" />
    </footer>
</footer>

untuk UAT Release 8 masih berjalan, per jumat kemarin User sudah masuk phase generate billing category CORE. Dan juga sudah memvalidasi hasil kalkulasi sistem dengan manual dari User. karena di category CORE ini ada 11 type dan masing-masing tipe memiliki banyak customer, User butuh waktu lebih untuk validasi satu per satu billing customer. 

========================================


Untuk update per Jumat kemarin progress UAT saat ini di angkat 63% persen.

dan untuk catatan dari User sudah kita tambahkan dan kita juga sudah update di hari jumat. Catatan terkait content yang ada di file PDF, misalnya logo header atau footer.


Dan Hasil validasi yg dilakukan oleh User untuk category CORE juga sudah sesuai dengan manual User.

Sekaran user akan mulai testing terkait negative test.

//        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
//                .map(SfValRgDaily::getEstimationSafekeepingFee)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .setScale(0, RoundingMode.HALF_UP);
//
//        log.info("[Core Type 4 ITAMA] Safekeeping amount due: {}", safekeepingAmountDue);
//        return safekeepingAmountDue;
