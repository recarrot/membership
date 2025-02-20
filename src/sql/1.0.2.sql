ALTER TABLE transactions ADD COLUMN consumptiontype ENUM('SHORTLINE', 'LONGLINE','EMPTY');

ALTER TABLE members ADD COLUMN total_amount_before_deduction DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE transactions ADD COLUMN deduction_amount DECIMAL(10,2) NULL;

UPDATE members SET total_amount_before_deduction = total_amount;