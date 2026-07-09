import {ArrowDownLeft, ArrowUpRight, Wallet} from "lucide-react";

export const CARDS = [
  {  id:1,title: "Total Balance", amount: 1000.00, variant: "balance"},
  {  id:2,title: "Monthly Income", amount: 1000.00, variant: "income"},
  {  id:3,title: "Monthly Expense", amount: 1000.00, variant: "expense"},
];

export const CARDS_STYLE = {
  balance: {

    bg: 'bg-indigo-600 text-white border-transparent',
    titleColor: 'text-indigo-200',
    amountColor: 'text-white',
    iconBg: 'bg-indigo-500/30 text-white',
    Icon: Wallet,
  },
  income: {
    bg: 'bg-emerald-600 text-white border-transparent',
    titleColor: 'text-emerald-200',
    amountColor: 'text-white',
    iconBg: 'bg-emerald-500/30 text-white',
    Icon: ArrowDownLeft, // Income flowing in
  },
  expense: {
    bg: 'bg-rose-600 text-white border-transparent',
    titleColor: 'text-rose-200',
    amountColor: 'text-white',
    iconBg: 'bg-rose-500/30 text-white',
    Icon: ArrowUpRight, // Expenses flowing out
  },
};