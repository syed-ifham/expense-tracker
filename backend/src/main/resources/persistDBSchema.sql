--TRACK THE SOURCE
CREATE TABLE IF NOT EXISTS  raw_messages
(
    message_id    INTEGER PRIMARY KEY, -- Matches message_id from source.db
    sender_id     TEXT    NOT NULL,    -- e.g., "JD-SBIUPI-S"
    raw_body      TEXT    NOT NULL,    -- The full SMS text
    sms_timestamp INTEGER NOT NULL,    -- Original unix timestamp
    processed_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

--  THE CORE TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS transactions
(
    message_id       INTEGER PRIMARY KEY, -- Unique ID from source.db
    payment_method   TEXT     NOT NULL,   -- 'UPI', 'CARD'
    direction        TEXT     NOT NULL,   -- 'DEBIT' or 'CREDIT'
    amount           REAL     NOT NULL,   -- Stored as standard paise
    remittance       TEXT,                -- The other party
    transaction_date DATETIME NOT NULL,   -- Standardized format
    category         TEXT DEFAULT 'Uncategorized',

    FOREIGN KEY (message_id) REFERENCES raw_messages (message_id)
);