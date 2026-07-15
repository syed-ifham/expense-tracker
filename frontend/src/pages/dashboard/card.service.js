import api from "../../service/api.js";

export const CARDS = [
  {id: 1, title: "Total Balance", amount: -1.00, variant: "balance"},
  {id: 2, title: "Monthly Income", amount: -1.00, variant: "income"},
  {id: 3, title: "Monthly Expense", amount: -1.00, variant: "expense"},
];

export const CARDS_TEMPLATE = [
  { id: 1, title: "Total Balance", variant: "balance" },
  { id: 2, title: "Monthly Income", variant: "income" },
  { id: 3, title: "Monthly Expense", variant: "expense" },
];


const fetchMonthlyDebit = async () => {
  const response = await api.get('transactions/last-month-expense');
  return response.data ?? 0;
};

const fetchMonthlyCredit = async () => {
  const response = await api.get('transactions/last-month-income');
  return response.data ?? 0;
};

export const getPopulatedCards = async () => {
  try {
    const [income, expense] = await Promise.all([
      fetchMonthlyCredit(),
      fetchMonthlyDebit()
    ]);

    const balance = income - expense;

    return CARDS_TEMPLATE.map((card) => {
      let amount = 0;
      if (card.variant === 'expense') amount = expense;
      if (card.variant === 'income') amount = income;
      if (card.variant === 'balance') amount = balance;

      return { ...card, amount };
    });
  } catch (error) {
    console.error("Error connecting to Spring Boot financial metrics service:", error);
    return CARDS_TEMPLATE.map(card => ({ ...card, amount: 0 }));
  }
};
