package com.transaction.factory;

import com.common.dto.TransactionDTO;
import com.common.interfaces.EntityFactory;
import com.transaction.entity.Transaction;

/**
 * Factory interface for creating Transaction entities.
 */
public interface TransactionFactory extends EntityFactory<Transaction, TransactionDTO> {
}
