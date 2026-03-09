package com.bankapp.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.FavoriteOperationDTO;
import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.common.interfaces.AccountServiceApi;
import com.bankapp.payment.entity.FavoriteOperation;
import com.bankapp.payment.repository.FavoriteOperationRepository;

@Service
public class FavoriteOperationService {

    private final FavoriteOperationRepository favOpRepo;
    private final AccountServiceApi accountServiceClient;

    public FavoriteOperationService(FavoriteOperationRepository favOpRepo, AccountServiceApi accountServiceClient) {
        this.favOpRepo = favOpRepo;
        this.accountServiceClient = accountServiceClient;
    }

    public List<FavoriteOperationDTO> getFavoriteByAccountId(Long accountId) {
        List<FavoriteOperation> operations = favOpRepo.findByAccountId(accountId);
        if (operations.isEmpty()) {
            return List.of();
        }
        // Fetch account to get userId. Then use it to fetch all account to see if a payment is
        // Internal or External
        AccountDTO account = accountServiceClient.getAccountById(accountId);
        List<AccountDTO> userAccounts = accountServiceClient.getAccountsByUserId(account.getUserId());
        return operations.stream().map(op -> toDTO(op, userAccounts)).toList();
    }

    public FavoriteOperationDTO createFavorite(FavoriteOperationDTO dto) {
        FavoriteOperation op = new FavoriteOperation();
        op.setAccountId(dto.getAccountId());
        op.setName(dto.getName());
        op.setRecipientIban(dto.getRecipientIban());
        op.setAmount(dto.getAmount());
        op.setCategory(dto.getCategory());
        op.setDescription(dto.getDescription());
        op = favOpRepo.save(op);
        
        
        AccountDTO account = accountServiceClient.getAccountById(dto.getAccountId());
        List<AccountDTO> userAccounts = accountServiceClient.getAccountsByUserId(account.getUserId());
        return toDTO(op, userAccounts);
    }
    
    public void deleteFavorite(Long id) {
        if (!favOpRepo.existsById(id)) {
            throw new ResourceNotFoundException("Operation not found among favorites", id);
        }
        favOpRepo.deleteById(id);
    }

    private FavoriteOperationDTO toDTO(FavoriteOperation favOp, List<AccountDTO> userAccounts) {
        FavoriteOperationDTO dto = new FavoriteOperationDTO();
        dto.setId(favOp.getId());
        dto.setAccountId(favOp.getAccountId());
        dto.setName(favOp.getName());
        dto.setRecipientIban(favOp.getRecipientIban());
        dto.setAmount(favOp.getAmount());
        dto.setCategory(favOp.getCategory());
        dto.setDescription(favOp.getDescription());

        AccountDTO matchingAccount = userAccounts.stream()
            .filter(acc -> acc.getIban().equals(favOp.getRecipientIban()))
            .findFirst()
            .orElse(null);

        if (matchingAccount != null) {
            dto.setType("INTERNAL");
            dto.setRecipientAccountName(matchingAccount.getName() != null && !matchingAccount.getName().isEmpty() 
                ? matchingAccount.getName() 
                : matchingAccount.getIban());
        } else {
            dto.setType("EXTERNAL");
        }

        return dto;
    }
}