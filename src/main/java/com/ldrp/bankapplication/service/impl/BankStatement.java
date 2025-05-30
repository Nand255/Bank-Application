package com.ldrp.bankapplication.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ldrp.bankapplication.dto.EmailDetails;
import com.ldrp.bankapplication.entity.Transaction;
import com.ldrp.bankapplication.entity.User;
import com.ldrp.bankapplication.repository.TransactionRepository;
import com.ldrp.bankapplication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    /*
     *  retrieve the list of transaction within a date range given an account number
     *  generate a pdf file of transaction
     *  send the file via email
     **/

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\Asus\\Documents\\UserStatement.pdf";

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate){

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        /* this is first option
        // 1. get all the transaction from DB
        // 2. convert it to stream and then filter by account number after that filter by start and end date
        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start) || transaction.getCreatedAt().isEqual(end)).toList();

        return transactionList;*/

        List<Transaction> transactions = transactionRepository.findByAccountNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
                accountNumber,
                start,
                end);

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        try {
            Rectangle statementSize = PageSize.A4;
            Document document = new Document(statementSize);
            log.info("setting size of document.");
            OutputStream outputStream = new FileOutputStream(FILE);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable bankInfoTable = new PdfPTable(1);
            PdfPCell bankName = new PdfPCell(new Phrase("My Bank"));
            bankName.setBorder(0);
            bankName.setBackgroundColor(BaseColor.LIGHT_GRAY);
            bankName.setPadding(20f);
            PdfPCell bankAddress = new PdfPCell(new Phrase("L.D.R.P sector 15, Gandhinagar"));
            bankAddress.setBorder(0);
            bankInfoTable.addCell(bankName);
            bankInfoTable.addCell(bankAddress);

            PdfPTable statementInfo = new PdfPTable(2);
            PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date : "+start));
            customerInfo.setBorder(0);
            PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
            statement.setBorder(0);
            PdfPCell stopDate = new PdfPCell(new Phrase("Last Transaction Date : "+end));
            stopDate.setBorder(0);
            PdfPCell name = new PdfPCell(new Phrase("Customer Name : "+customerName));
            name.setBorder(0);
            PdfPCell space = new PdfPCell();
            space.setBorder(0);
            PdfPCell address = new PdfPCell(new Phrase("Customer Address : "+user.getAddress()));
            address.setBorder(0);

            statementInfo.addCell(customerInfo);
            statementInfo.addCell(statement);
            statementInfo.addCell(stopDate);
            statementInfo.addCell(name);
            statementInfo.addCell(space);
            statementInfo.addCell(address);

            PdfPTable transactionTable = new PdfPTable(4);
            PdfPCell date = new PdfPCell(new Phrase("DATE"));
            date.setBorder(0);
            date.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
            transactionType.setBorder(0);
            transactionType.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
            transactionAmount.setBorder(0);
            transactionAmount.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell status = new PdfPCell(new Phrase("STATUS"));
            status.setBorder(0);
            status.setBackgroundColor(BaseColor.LIGHT_GRAY);

            transactionTable.addCell(date);
            transactionTable.addCell(transactionType);
            transactionTable.addCell(transactionAmount);
            transactionTable.addCell(status);

            transactions.forEach(transaction -> {
                transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
                transactionTable.addCell(new Phrase(transaction.getTransactionType()));
                transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
                transactionTable.addCell(new Phrase(transaction.getStatus()));
            });

            document.add(bankInfoTable);
            document.add(statementInfo);
            document.add(transactionTable);

            document.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("kindly find your requested account statement document!")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactions;
    }

}
