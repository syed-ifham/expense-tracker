# 💸 Expense Tracker

> A smart, real-world expense tracker for transaction monitoring, SMS parsing, and fast account insights.

## ✨ Why this project stands out

- 📲 **SMS-powered tracking** — reads bank/UPI-style messages and turns them into structured transaction data.
- ⚡ **Real-time application feel** — built for quick visibility into recent activity and daily spending.
- 🧠 **Smart parsing logic** — uses pattern matchers for credits, debits, balances, and message formats.
- 🗂️ **Local SQLite storage** — keeps temporary and persistent data separate for cleaner workflows.
- 🌐 **API-first backend** — Spring Boot controllers expose health, transaction, and summary endpoints.

## 🔗 Quick links

- 🧩 [Backend API overview](./src/main/java/tracker/controller)
- 🧠 [Transaction parsing services](./src/main/java/tracker/service)
- 💾 [Database configuration](./src/main/resources/application.yaml)
- 🎨 [Frontend app](./frontend/trackerUI)
- 📝 [Project help notes](./HELP.md)

## 🚀 What it does

1. 📥 Collects transaction messages from the configured source.
2. 🔍 Detects credits, debits, balance updates, and UPI patterns.
3. 💽 Stores data in local SQLite databases for temporary and persistent use.
4. 📊 Serves transaction lists and summary endpoints for dashboards.
5. 🖥️ Provides a React + Vite UI for a clean user experience.

## 📌 Real-world application

- 🏦 Bank SMS transaction monitoring
- 📱 UPI payment tracking
- 📉 Spending visibility for daily budgeting
- 📅 Recent transaction review for quick financial checks
- 🧾 Lightweight personal finance logging

## 🛠️ API highlights

- `GET /health` — app health check
- `GET /transactions` — all transactions
- `GET /transactions/recent` — latest 10 transactions
- `GET /transactions/recent/{Id}` — recent transactions with custom limit
- `GET /transactions/summary/weekly` — weekly summary placeholder
- `GET /transactions/summary/monthly` — monthly summary placeholder
- `GET /transactions/summary/range` — range summary placeholder

## 🧰 Tech stack

- ☕ Java 17
- 🍃 Spring Boot
- 🗃️ SQLite
- ⚛️ React
- ⚡ Vite

[//]: # (## 🔒 Privacy & safety)


## ▶️ Run it locally

- Start the Spring Boot app from the project root.
- Launch the frontend from `frontend/trackerUI`.
- Open the UI and use the API endpoints to inspect transactions.

