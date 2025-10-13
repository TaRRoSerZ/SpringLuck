package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Transaction;

public interface SaveTransactionPort {
    void saveTransaction(Transaction transaction);
}
