
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
