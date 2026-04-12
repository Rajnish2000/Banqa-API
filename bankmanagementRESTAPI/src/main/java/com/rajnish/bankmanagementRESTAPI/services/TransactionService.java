package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.TransactionRequestDTO;
import com.rajnish.bankmanagementRESTAPI.dtos.TransactionResponseDTO;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.Transaction;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public TransactionResponseDTO withdrawOrDepositMoney(TransactionRequestDTO transactionRequestDTO) throws InvalidAttributeValueException {
        Transaction transaction;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findCustomerByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("No customer found for the user."));
        List<Account> availableAccounts = customer.getAccountId();

        boolean exists = availableAccounts.stream()
                .anyMatch(account -> Objects.equals(
                        account.getAccountNumber(),
                        transactionRequestDTO.getAccountNum()
                ));

        if (!exists) {
            throw new NoSuchElementException("Money can be withdraw or deposit from your Account number not others.");
        }

        if(transactionRequestDTO.getTransactionType() == TransactionType.DEPOSIT){
            if(transactionRequestDTO.getTransaction_amount() < 1000000) {
                transaction =  transactionRepository.save(Transaction.builder()
                        .accountNum(transactionRequestDTO.getAccountNum())
                        .transaction_amount(transactionRequestDTO.getTransaction_amount())
                        .transactionType(transactionRequestDTO.getTransactionType())
                        .transactionMode(transactionRequestDTO.getTransactionMode())
                        .transaction_remark(transactionRequestDTO.getTransaction_remark())
                        .transactionStatus(TransactionStatus.COMPLETED)
                        .crdr(TransactionNature.CR)
                        .build());
                Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNum()).orElse(null);
                if(account != null) {
                    account.setBalance(transaction.getTransaction_amount());
                    accountRepository.save(account);
                }else{
                    throw new NoSuchElementException("Account Number Not Found. Invalid Account Number.");
                }
            }else{
                throw new IllegalArgumentException("Amount is too high can not deposit. Amount must be less than 1000000");
            }
        } else if (transactionRequestDTO.getTransactionType() == TransactionType.WITHDRAWAL) {
            Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNum()).orElse(null);
            if(account != null) {
                if(account.getBalance() > transactionRequestDTO.getTransaction_amount()){
                     transaction =  transactionRepository.save(Transaction.builder()
                            .accountNum(transactionRequestDTO.getAccountNum())
                            .transaction_amount(transactionRequestDTO.getTransaction_amount())
                            .transactionType(transactionRequestDTO.getTransactionType())
                            .transactionMode(transactionRequestDTO.getTransactionMode())
                            .transaction_remark(transactionRequestDTO.getTransaction_remark())
                            .transactionStatus(TransactionStatus.COMPLETED)
                            .crdr(TransactionNature.DR)
                            .build());
                    account.setBalance(account.getBalance() - transaction.getTransaction_amount());
                    accountRepository.save(account);
                }else{
                    throw new IndexOutOfBoundsException("Insufficient balance in account.");
                }
            }else{
                throw new NoSuchElementException("Account Number Not Found. Invalid Account Number.");
            }
        }else {
            throw new InvalidAttributeValueException("Invalid Transaction type");
        }
        return modelMapper.map(transaction,TransactionResponseDTO.class);
    }

    @Transactional
    public TransactionResponseDTO transferMoney(TransactionRequestDTO transactionRequestDTO) throws InvalidAttributeValueException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findCustomerByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("No customer found for the user."));
        List<Account> availableAccounts = customer.getAccountId();

        boolean exists = availableAccounts.stream()
                .anyMatch(account -> Objects.equals(
                        account.getAccountNumber(),
                        transactionRequestDTO.getAccountNum()
                ));

        if (!exists) {
            throw new NoSuchElementException("Money can be transfer from your Account number not others.");
        }
        Transaction debit;
        if(transactionRequestDTO.getTransactionType() == TransactionType.TRANSFER){
            Account from_account = accountRepository.findByAccountNumber(transactionRequestDTO.getToAccountNum()).orElseThrow(() -> new NoSuchElementException("Sender Account not found"));
            Account to_account = accountRepository.findByAccountNumber(transactionRequestDTO.getToAccountNum()).orElseThrow(() -> new NoSuchElementException("Receiver Account not found"));
            if(from_account.getBalance() > transactionRequestDTO.getTransaction_amount()){
                debit =  transactionRepository.save(Transaction.builder()
                        .accountNum(transactionRequestDTO.getAccountNum())
                        .transaction_amount(transactionRequestDTO.getTransaction_amount())
                        .toAccountNum(transactionRequestDTO.getToAccountNum())
                        .transactionType(transactionRequestDTO.getTransactionType())
                        .transactionMode(transactionRequestDTO.getTransactionMode())
                        .transaction_remark(transactionRequestDTO.getTransaction_remark())
                        .transactionStatus(TransactionStatus.COMPLETED)
                        .crdr(TransactionNature.DR)
                        .build());
               Transaction credit =  transactionRepository.save(Transaction.builder()
                        .accountNum(transactionRequestDTO.getToAccountNum())
                        .transaction_amount(transactionRequestDTO.getTransaction_amount())
                        .toAccountNum(transactionRequestDTO.getAccountNum())
                        .transactionType(transactionRequestDTO.getTransactionType())
                        .transactionMode(transactionRequestDTO.getTransactionMode())
                        .transaction_remark(transactionRequestDTO.getTransaction_remark())
                        .transactionStatus(TransactionStatus.COMPLETED)
                        .crdr(TransactionNature.CR)
                        .build());
                from_account.setBalance(from_account.getBalance() - debit.getTransaction_amount());
                accountRepository.save(from_account);
                to_account.setBalance(to_account.getBalance() + credit.getTransaction_amount());
                accountRepository.save(to_account);
            }else{
                throw new IndexOutOfBoundsException("Insufficient balance in account.");
            }
        }else{
            throw new InvalidAttributeValueException("Invalid Transaction type");
        }
        return modelMapper.map(debit,TransactionResponseDTO.class);
    }
}
