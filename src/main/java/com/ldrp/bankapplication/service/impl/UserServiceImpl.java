package com.ldrp.bankapplication.service.impl;

import com.ldrp.bankapplication.config.JwtTokenProvider;
import com.ldrp.bankapplication.dto.*;
import com.ldrp.bankapplication.entity.Role;
import com.ldrp.bankapplication.entity.User;
import com.ldrp.bankapplication.repository.UserRepository;
import com.ldrp.bankapplication.utlis.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    UserRepository userRepository;

    EmailService emailService;

    TransactionService transactionService;

    PasswordEncoder passwordEncoder;

    AuthenticationManager authenticationManager;

    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(EmailService emailService, UserRepository userRepository, TransactionService transactionService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /*
        *  Creating an account - saving a new user into the db
        *  Check if user already has an account
        * */

        if (userRepository.existsByEmail(userRequest.getEmail()) || userRepository.existsByAadharNumber(userRequest.getAadharNumber()) ){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                            .firstName(userRequest.getFirstName())
                            .lastName(userRequest.getLastName())
                            .otherName(userRequest.getOtherName())
                            .gender(userRequest.getGender())
                            .address(userRequest.getAddress())
                            .stateOfOrigin(userRequest.getStateOfOrigin())
                            .accountNumber(AccountUtils.generateAccountNumber())
                            .accountBalance(BigDecimal.ZERO)
                            .email(userRequest.getEmail())
                            .password(passwordEncoder.encode(userRequest.getPassword()))
                            .phoneNumber(userRequest.getPhoneNumber())
                            .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                            .aadharNumber(userRequest.getAadharNumber())
                            .status("ACTIVE")
                            .role(Role.valueOf("ROLE_ADMIN"))
                            .build();

        User savedUser = userRepository.save(newUser);

        //send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulation! Your Account Has been Successfully Created. \nYour Account Details: \n " +
                        "Account Name: "+savedUser.getFirstName()+" "+savedUser.getLastName()+"\n" +
                        "Account Number: "+savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        //send a response message
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " "+savedUser.getOtherName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in:")
                .recipient(loginDto.getEmail())
                .messageBody("You logged in your account. If you did not initiate this request, please contact your bank")
                .build();

        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        //check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        //check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() +" "+ foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        //checking if the account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        //crediting the amount
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        //after credit save user data in db
        userRepository.save(userToCredit);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(userToCredit.getEmail())
                .messageBody(creditDebitRequest.getAmount() + " has been credited to your account! Your current balance is : "+userToCredit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        //Save Transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .balance(userToCredit.getAccountBalance())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() +" "+ userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        //1. check if the account
        boolean isAccountExists = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        //2. check if the amount you need to withdraw is not more than the current amount
        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToDebit.getAccountBalance().compareTo(creditDebitRequest.getAmount()) == 1) {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            EmailDetails debitAlert = EmailDetails.builder()
                    .subject("DEBIT ALERT")
                    .recipient(userToDebit.getEmail())
                    .messageBody(creditDebitRequest.getAmount() + " has been debited from your account! Your current balance is : "+userToDebit.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(debitAlert);

            //Save Transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(creditDebitRequest.getAmount())
                    .balance(userToDebit.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " +userToDebit.getLastName() + " " +userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();

        } else {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit(check if the account exists)
        //check if the amount user is debiting is not more than the current amount
        //debit the amount
        //get the account to credit
        //credit the amount

        boolean isDestinationAccountexist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountexist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccount  = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        User destinationAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        String sourceUserName = sourceAccount.getFirstName() +" "+ sourceAccount.getLastName() +" "+ sourceAccount.getOtherName();
        if (sourceAccount.getAccountBalance().compareTo(request.getAmount()) == 1) {
            sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(sourceAccount);

            EmailDetails debitAlert = EmailDetails.builder()
                    .subject("DEBIT ALERT")
                    .recipient(sourceAccount.getEmail())
                    .messageBody(request.getAmount() + " has been deducted from your account! Your current balance is : "+sourceAccount.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(debitAlert);

            destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(request.getAmount()));
            userRepository.save(destinationAccount);

            //String destinationUserName = destinationAccount.getFirstName() +" "+ destinationAccount.getLastName() +" "+destinationAccount.getOtherName();

            EmailDetails creditAlert = EmailDetails.builder()
                    .subject("CREDIT ALERT")
                    .recipient(destinationAccount.getEmail())
                    .messageBody(request.getAmount() + " has been sent to your account! from " +sourceUserName+" Your current balance is : "+destinationAccount.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(creditAlert);

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(destinationAccount.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .balance(destinationAccount.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                    .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

    }
}