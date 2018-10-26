The solution has been built using JDK 8 and the Spring Framework.

Build: To build runt he following
mvn clean install

Testing: To run the unit tests run the following:
mvn test

Using Lombok and its builder pattern. IntelliJ Lombok Plugin installation required.
Using BigDecimal in field types for highest precision. Not necessary in this limited scope but will come in handy later.
Only using Long Data type if working with integers and non-decimal digits.

The way I understood and implemented the disbursement requirements:

Case1: Provided all denominations available. Please see relevant tests for this logic:
100 = 50+20+20+5+5 (OK, Providing smallest possible number of notes along with at least one 5 note)
100 = 50+50 (Not OK, as the 5 note was available but dinot disbursed by ATM)

Case2: Provided denomination 5 not available:
100 = 50+50 (OK, as ATM donot have 5 notes and smallest number of notes are handed over)

Assumptions:
No overdraft for any account. Transaction will not be successful in case of overdraft and account balance will remain the same.
ATMServiceImple.replenish: Returns new ATM state after replenishing the submitted list of currencyNotes where the CurrencyNote class is a wrapper for Denomination.
Not using Locale for amount String formatting. Keeping it simple here and formatting thousands separator with commas and decimal with decimal point.
Not persisting states after withdraw etc. I believe it is not required for implementation and testing of this task.
