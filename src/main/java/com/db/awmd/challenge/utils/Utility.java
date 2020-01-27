package com.db.awmd.challenge.utils;

import java.math.BigDecimal;

public class Utility {
	/**
	 * Checks whether account balance is either greater than or equal to the
	 * transferred amount.
	 * 
	 * @param balance
	 * @param amount
	 * @return
	 */
	public static boolean validateAmount(BigDecimal balance, BigDecimal amount) {
		if (balance.subtract(amount).compareTo(BigDecimal.ZERO) >= 0)
			return true;

		return false;
	}
}
