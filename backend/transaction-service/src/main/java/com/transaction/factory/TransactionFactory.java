package com.transaction.factory;

import com.common.model.TransactionModel;
import com.common.interfaces.EntityFactory;
import com.transaction.entity.Transaction;

/**
 * Factory interface for creating Transaction entities.
 */
public interface TransactionFactory extends EntityFactory<Transaction, TransactionModel> {
}
