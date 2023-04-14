package com.example.splitsmart.model;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A data object that stores the balances that users owe to one another for a particular group.
 */
public class Balances implements Serializable {
    private final HashMap<BalanceKey, Float> balances;
    private final Group group;
    private final ArrayList<Long> groupMemberIds;

    public Balances(Context context, Group group) {
        this.balances = new HashMap<>();
        this.group = group;
        this.groupMemberIds = (ArrayList<Long>) group.members.stream().map(user -> user.id).collect(Collectors.toList());

        initializeBalances();
        calculateBalancesFromGroupExpenses(context);
        calculateBalancesFromGroupPayments(context);
    }

    /**
     * Adds an amount of money to the balance between two users.
     * @param payer the user who owes money.
     * @param receiver the user who is owed money.
     * @param amountPaid the amount the payer owes the receiver.
     */
    public void addBalance(User payer, User receiver, float amountPaid) {
        if(!groupMemberIds.contains(payer.id) || !groupMemberIds.contains(receiver.id)) {
            return; // Do not add balance if one of the users is no longer a member of the group
        }

        BalanceKey key = new BalanceKey(payer, receiver);
        Float currentBalance = balances.get(key);

        // If the key is not in the balances HashMap, then the order of the users in the key is backwards,
        // and we should invert the sign of the debt to determine what it is from the payer's perspective.
        if(currentBalance != null) {
            balances.put(key, currentBalance + amountPaid);
        } else {
            addBalance(receiver, payer, -amountPaid);
        }
    }

    /**
     * Gets the amount of money that a user owes another user.
     * @param payer the user who owes money.
     * @param receiver the user who is owed money.
     * @return the amount the payer owes the receiver. Note that if the receiver actually owes the payer, this amount will be negative.
     */
    public float getBalance(User payer, User receiver) {
        BalanceKey key = new BalanceKey(payer, receiver);
        Float currentBalance = balances.get(key);
        return (currentBalance != null ? currentBalance : -getBalance(receiver, payer));
    }

    /**
     * @param requester the user whose perspective we are viewing the balances from.
     * @return a human-readable list of all of the outstanding balances for a particular user.
     */
    public String prettyPrintBalances(User requester) {
        StringBuilder printedBalances = new StringBuilder();

        for(User member : this.group.members) {
            if(member.id != requester.id) {
                float balance = getBalance(requester, member);
                if(balance > 0.005) {
                    printedBalances.append(String.format(Locale.US, "You owe %s $%.2f\n", member, balance));
                } else if (balance < -0.005) {
                    printedBalances.append(String.format(Locale.US, "%s owes you $%.2f\n", member, -balance));
                }
            }
        }
        return printedBalances.toString();
    }

    /**
     * Sets up a $0 balance for all unique permutations of pairs of group members.
     * NOTE: This function will not create a balance for what A owes B and what B owes A. It will create just one balance for these two users. The sign of the balance indicates in which direction the debt is owed.
     */
    private void initializeBalances() {
        for(int i = 0; i < group.members.size(); i++) {
            for(int j = i; j < group.members.size(); j++) {
                BalanceKey key = new BalanceKey(group.members.get(i), group.members.get(j));
                balances.put(key, 0f);
            }
        }
    }

    /**
     * Adds the debts accumulated for each user based on all of the expenses the group has generated so far.
     * @param context the current activity context.
     */
    private void calculateBalancesFromGroupExpenses(Context context) {
        for(Expense expense : Expense.selectAllByGroup(context, group)) {
            User payer = expense.payer;

            for(ExpenseSplit split : expense.expenseSplits) {
                if(split.user.id != payer.id && split.amount > 0) {
                    addBalance(split.user, payer, split.amount);
                }
            }
        }
    }

    /**
     * Subtracts all of the payments from the balances owed between group members.
     * @param context the current activity context.
     */
    private void calculateBalancesFromGroupPayments(Context context) {
        for(Payment payment : Payment.selectAllByGroup(context, group)) {
            addBalance(payment.payer, payment.recipient, -payment.amount);
        }
    }
}
