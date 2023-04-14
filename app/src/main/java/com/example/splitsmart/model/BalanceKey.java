package com.example.splitsmart.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Represents an ordered pair of Users to be used internally in a HashMap in the Balances class.
 * The first user in the pair represents the user who owes money, and the second user is the user who is owed money.
 */
public class BalanceKey implements Serializable {
    private final User payer;
    private final User receiver;

    public BalanceKey(User payer, User receiver) {
        this.payer = payer;
        this.receiver = receiver;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BalanceKey)) return false;
        BalanceKey otherKey = (BalanceKey) obj;

        return (this.payer.id == otherKey.payer.id && this.receiver.id == otherKey.receiver.id);
    }

    @Override
    public int hashCode() {
        return 31 * (int)this.payer.id + (int)this.receiver.id;
    }
}
