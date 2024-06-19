package com.bayu.billingservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Slf4j
@SpringBootTest
class BillingServiceApplicationTests {

	@Test
	void contextLoads() {

	}

	@Test
	void multiplyDecimal() {
		BigDecimal safekeepingFee = new BigDecimal("0.00020");
		BigDecimal bigDecimal = safekeepingFee.multiply(new BigDecimal(100))
				.setScale(5, RoundingMode.HALF_UP);
		System.out.println(bigDecimal);
		String plainString = bigDecimal.stripTrailingZeros().toPlainString();
		System.out.println(plainString);
	}

	private static BigDecimal cleanedTransactionAmountToBigDecimal(String value) {
		value = value==null?"0":value;
		String cleanedInput = value.replace(".", "").replace(",", ".");
		return new BigDecimal(cleanedInput);
	}

	private static String formattedBigDecimalToString(BigDecimal bigDecimal) {
		BigDecimal newBigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat decimalFormat = new DecimalFormat("###,###.##", symbols);

		return decimalFormat.format(newBigDecimal);
	}

	@Test
	void test() {
		BigDecimal value1 = new BigDecimal("0.003500");
		BigDecimal value2 = new BigDecimal("0.200000");

		// Menghilangkan angka nol yang tidak diperlukan
		value1 = value1.stripTrailingZeros();
		value2 = value2.stripTrailingZeros();

		// Mengonversi ke string
		String strValue1 = value1.toPlainString();
		String strValue2 = value2.toPlainString();

		System.out.println("Value 1: " + strValue1);
		System.out.println("Value 2: " + strValue2);
	}
}
