package com.example.splitsmart.model;

/**
 * A builder pattern that helps create ExpenseFilters.
 */
public class ExpenseFilterBuilder {
    private ExpenseFilter filter;

    public ExpenseFilterBuilder() {
        this.reset();
    }

    public void reset() {
        this.filter = new ExpenseFilter();
        this.filter.where = "1=1";
    }

    public ExpenseFilter build() {
        return this.filter;
    }

    public void setGroup(Group group) {
        this.filter.where += " AND groupid=?";
        this.filter.args.add(group.id+"");
    }

    public void setRelevantToMeOnly(User requester) {
        this.filter.requester = requester;
        this.filter.relevantToMeOnlyFlag = true;
    }

    public void setAmountMin(String amountMin) {
        this.filter.where += " AND amount>=?";
        this.filter.args.add(amountMin);
        this.filter.amountMin = amountMin;
    }

    public void setAmountMax(String amountMax) {
        this.filter.where += " AND amount<=?";
        this.filter.args.add(amountMax);
        this.filter.amountMax = amountMax;
    }

    public void setDateMin(String dateMin) {
        this.filter.where += " AND date>=?";
        this.filter.args.add(dateMin);
        this.filter.dateMin = dateMin;
    }

    public void setDateMax(String dateMax) {
        this.filter.where += " AND date<=?";
        this.filter.args.add(dateMax);
        this.filter.dateMax = dateMax;
    }

    public void setDesc(String desc) {
        this.filter.where += " AND desc LIKE ?";
        this.filter.args.add(String.format("%%%s%%", desc));
        this.filter.desc = desc;
    }

    public void setPayer(User payer) {
        this.filter.where += " AND payerid = ?";
        this.filter.args.add(payer.id+"");
        this.filter.payer = payer;
    }
}
