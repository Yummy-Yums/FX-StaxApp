CREATE TABLE trader (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    fxTender VARCHAR(255) NOT NULL,
    bankAccount_id INT NOT NULL,
    FOREIGN KEY (bankAccount_id) REFERENCES Account(id)
);

CREATE TABLE account (
     id INT PRIMARY KEY AUTO_INCREMENT,
     balance FLOAT NOT NULL,
     currency VARCHAR(255) NOT NULL,
     trader_id INT NOT NULL,
     FOREIGN KEY (trader_id) REFERENCES Trader(id)
);

