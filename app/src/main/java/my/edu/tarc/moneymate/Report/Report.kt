package my.edu.tarc.moneymate.Report

import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income

sealed class ReportItem {
    data class IncomeItem(val income: Income) : ReportItem()
    data class GoalItem(val goal: Goal) : ReportItem()
    data class ExpenseItem(val expense: Expense) : ReportItem()
}